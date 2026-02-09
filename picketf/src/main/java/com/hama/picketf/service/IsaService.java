package com.hama.picketf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hama.picketf.dao.IsaDAO;
import com.hama.picketf.dto.IsaDTO;

@Service
public class IsaService {

  @Autowired
  private IsaDAO isaDAO;

  public void createIsa(int usNum, int year, String type, long initialDeposit) {

    // 1) 간단 검증
    if (year < 2000 || year > 2026) {
      throw new IllegalArgumentException("개설 연도 범위가 이상함");
    }

    if (!"NORMAL".equals(type) && !"LOW_INCOME".equals(type)) {
      throw new IllegalArgumentException("계좌 유형이 이상함");
    }

    // 금액 음수 입력 방지
    if (initialDeposit < 0) {
      throw new IllegalArgumentException("금액이 음수일 수 없음");
    }

    // 2) DTO 구성
    IsaDTO dto = new IsaDTO();
    dto.setIsaUsNum(usNum);
    dto.setIsaYear(year);
    dto.setIsaType(type);
    dto.setIsaTotalAmount(initialDeposit);

    // 3) INSERT
    isaDAO.insertIsa(dto);
  }
}
