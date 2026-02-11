package com.hama.picketf.dao;

import org.apache.ibatis.annotations.Param;

import com.hama.picketf.dto.IsaDTO;

public interface IsaDAO {
  int insertIsa(IsaDTO dto); // 신규 ISA 계좌 개설

  IsaDTO findByUsNum(@Param("usNum") int usNum); // 해당 유저의 ISA 계좌 조회

  int increaseTotalAmount(@Param("usNum") int usNum, @Param("amount") long amount); // 예수금 추가: isa_total_amount 증가

  int deleteByUsNum(@Param("usNum") int usNum); // 해당 유저 ISA 삭제

}
