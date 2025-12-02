package com.hama.picketf.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SubsDTO {
    private Long subsNum;
    private Long subsUsNum;
    private String subsName;
    private Integer subsPrice;
    private String subsType;
    private Integer subsBillingDay;
    private Boolean subsActive;
    private LocalDate subsStartDate;
    private String subsIcon;
}