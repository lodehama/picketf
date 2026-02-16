package com.hama.picketf.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hama.picketf.dto.IsaDTO;
import com.hama.picketf.dto.IsaDepositLogDTO;

public interface IsaDAO {
  int insertIsa(IsaDTO dto); // 신규 ISA 계좌 개설

  IsaDTO findByUsNum(@Param("usNum") int usNum); // 해당 유저의 ISA 계좌 조회

  int increaseTotalAmount(@Param("usNum") int usNum, @Param("amount") long amount); // 예수금 추가: isa_total_amount 증가

  int deleteByUsNum(@Param("usNum") int usNum); // 해당 유저 ISA 삭제

  int insertDepositLog(IsaDepositLogDTO dto); // 예수금 추가 로그 기록

  // 최근 납입 로그
  List<IsaDepositLogDTO> selectRecentDepositLogs(
      @Param("isaNum") int isaNum,
      @Param("limit") int limit);

}
