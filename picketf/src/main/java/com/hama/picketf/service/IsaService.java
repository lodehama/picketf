package com.hama.picketf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    if (year < 2000 || year > 2026) {
      throw new IllegalArgumentException("개설 연도 범위가 이상함");
    }

    if (!"NORMAL".equals(type) && !"LOW_INCOME".equals(type)) {
      throw new IllegalArgumentException("계좌 유형이 이상함");
    }

    if (initialDeposit < 0) {
      throw new IllegalArgumentException("금액이 음수일 수 없음");
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

  // 총 납입 가능한 금액 = 1억 - 누적납입
  public long calcTotalRemain(IsaDTO isa) {
    if (isa == null)
      return 0L;
    return Math.max(0, TOTAL_LIMIT - isa.getIsaTotalAmount());
  }

  // 올해 납입 가능한 금액 (임시)
  public long calcYearRemainTemp() {
    return YEAR_LIMIT;
  }
}
