package com.hama.picketf.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hama.picketf.dao.IsaDAO;
import com.hama.picketf.dto.IsaDTO;

@Service
public class IsaService {

  private static final long YEAR_LIMIT = 20_000_000L;
  private static final long TOTAL_LIMIT = 100_000_000L;

  @Autowired
  private IsaDAO isaDAO;

  // ISA 생성
  public void createIsa(int usNum, int year, String type, long initialDeposit) {

    int nowYear = LocalDate.now().getYear();
    if (year < 2000 || year > nowYear) {
      throw new IllegalArgumentException("개설 연도 범위가 이상함");
    }

    if (!"NORMAL".equals(type) && !"LOW_INCOME".equals(type)) {
      throw new IllegalArgumentException("계좌 유형이 이상함");
    }

    if (initialDeposit < 0) {
      throw new IllegalArgumentException("금액이 음수일 수 없음");
    }

    IsaDTO exists = isaDAO.findByUsNum(usNum);
    if (exists != null) {
      throw new IllegalStateException("이미 ISA 계좌가 존재함");
    }

    long cap = calcCapByOpenedYear(year);
    if (initialDeposit > cap) {
      throw new IllegalArgumentException("초기 납입금액이 현재 누적 한도를 초과함");
    }

    IsaDTO dto = new IsaDTO();
    dto.setIsaUsNum(usNum);
    dto.setIsaYear(year);
    dto.setIsaType(type);
    dto.setIsaTotalAmount(initialDeposit);

    isaDAO.insertIsa(dto);
  }

  // 유저의 ISA 1건 조회 + 화면용 타입 변환
  public IsaDTO getIsaByUser(int usNum) {
    IsaDTO isa = isaDAO.findByUsNum(usNum);

    if (isa == null)
      return null;

    // 화면용 값으로 치환 (HTML 수정 없이 해결)
    if ("NORMAL".equalsIgnoreCase(isa.getIsaType())) {
      isa.setIsaType("일반형");
    } else if ("LOW_INCOME".equalsIgnoreCase(isa.getIsaType())) {
      isa.setIsaType("서민형");
    }

    return isa;
  }

  // 평생 납입 한도 (법적 최대치)
  public long calcTotalRemain(IsaDTO isa) {
    if (isa == null)
      return 0L;
    return Math.max(0, TOTAL_LIMIT - isa.getIsaTotalAmount());
  }

  /**
   * 예수금 추가 = isa_total_amount 증가
   * - 추천: UPDATE로 바로 +amount (원자적)
   */
  @Transactional
  public void addCash(int usNum, long amount) {

    if (amount <= 0) {
      throw new IllegalArgumentException("추가 금액이 0 이하임");
    }

    IsaDTO isa = isaDAO.findByUsNum(usNum);
    if (isa == null) {
      throw new IllegalStateException("ISA 계좌가 없음");
    }

    // 현재년도 기준 누적 한도(cap) 계산
    long cap = calcCapByOpenedYear(isa.getIsaYear());

    // 누적 납입액 + 이번 납입액이 cap을 넘으면 막기
    long after = isa.getIsaTotalAmount() + amount;
    if (after > cap) {
      throw new IllegalArgumentException("현재 누적 납입 한도를 초과함");
    }

    int updated = isaDAO.increaseTotalAmount(usNum, amount);
    if (updated != 1) {
      throw new IllegalStateException("예수금 추가 실패(업데이트 행 수 이상)");
    }
  }

  private int getNowYear() {
    return LocalDate.now().getYear();
  }

  /**
   * 개설년도 기준, "현재년도까지 누적 납입 가능 한도" 계산
   * cap = min(1억, (현재년도 - 개설년도 + 1) * 2천)
   */
  public long calcCapByOpenedYear(int openedYear) {
    int nowYear = getNowYear();

    if (openedYear > nowYear) {
      // 미래년도 개설은 말이 안 되니까 0 또는 예외
      throw new IllegalArgumentException("개설년도는 현재년도보다 클 수 없음");
    }

    long yearsCount = (long) (nowYear - openedYear + 1);
    long cap = yearsCount * YEAR_LIMIT;

    return Math.min(TOTAL_LIMIT, cap);
  }

  // 현재 누적 납입 가능 금액 (이월 포함)
  public long calcTotalRemainByRule(IsaDTO isa) {
    if (isa == null)
      return 0L;
    long cap = calcCapByOpenedYear(isa.getIsaYear());
    return Math.max(0, cap - isa.getIsaTotalAmount());
  }

  // 올해 기준 납입 가능 금액 계산
  public long calcYearRemainByRule(IsaDTO isa) {
    if (isa == null)
      return 0L;

    int nowYear = LocalDate.now().getYear();
    int openYear = isa.getIsaYear();

    if (openYear > nowYear) {
      throw new IllegalArgumentException("개설년도는 현재년도보다 클 수 없음");
    }

    long deposited = isa.getIsaTotalAmount();

    // 1) 현재년도 기준 누적 한도 (예: 2026개설이면 2천, 2025개설이면 4천...)
    long currentCap = calcCapByOpenedYear(openYear);

    // 2) 작년까지 누적 한도 (올해 한도 계산에 필요)
    // nowYear == openYear 이면 0, 1년 지났으면 2천, 2년 지났으면 4천...
    long prevCap = Math.min(
        TOTAL_LIMIT,
        (long) (nowYear - openYear) * YEAR_LIMIT);

    // 3) “올해 납입분”을 계산하려면,
    // 이미 납입한 돈 중에서 "작년까지 허용된 구간(prevCap)"은 이전년도 몫으로 보고,
    // 그 이상만 올해 사용분으로 간주하는 방식이 가장 단순하고 일관됨.
    long effectiveDepositedForThisYear = Math.max(0L, deposited - prevCap);

    // 4) 올해 남은 금액 = 올해 한도(2천) - 올해 사용분
    long yearRemain = YEAR_LIMIT - effectiveDepositedForThisYear;

    // 5) 하지만 누적 한도(currentCap)도 동시에 못 넘는다.
    long totalRemain = Math.max(0L, currentCap - deposited);

    // 6) 둘 중 더 작은 값이 “올해 실제로 더 넣을 수 있는 금액”
    return Math.max(0L, Math.min(yearRemain, totalRemain));
  }

  // ISA 삭제 (유저 기준으로 1개 삭제)
  @Transactional
  public boolean deleteIsaByUser(int usNum) {

    IsaDTO isa = isaDAO.findByUsNum(usNum);
    if (isa == null) {
      return false; // 삭제할 게 없음
    }

    int deleted = isaDAO.deleteByUsNum(usNum);

    // usNum이 UNIQUE라면 0 또는 1이 정상
    if (deleted != 1) {
      throw new IllegalStateException("ISA 삭제 실패(삭제 행 수 이상)");
    }

    return true;
  }

}
