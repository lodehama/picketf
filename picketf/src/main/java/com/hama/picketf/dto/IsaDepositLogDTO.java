package com.hama.picketf.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class IsaDepositLogDTO {
  private Integer isaDepositLogNum;      // PK
  private Integer isaDepositLogIsaNum;   // FK -> isa.isa_num
  private Long isaDepositLogAmount;      // BIGINT UNSIGNED
  private String isaDepositLogMemo;      // VARCHAR(255)
  private LocalDateTime isaDepositLogCreated; // DATETIME (DB default)
}
