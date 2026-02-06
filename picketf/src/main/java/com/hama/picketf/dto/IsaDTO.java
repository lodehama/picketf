package com.hama.picketf.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IsaDTO {
  private Integer isaNum;          // PK (AUTO_INCREMENT)
  private Integer isaUsNum;        // FK (user)
  private Integer isaYear;
  private String isaType;          // "NORMAL" or "LOW_INCOME"
  private Long isaTotalAmount;     // BIGINT UNSIGNED
  private LocalDateTime isaCreated;
}
