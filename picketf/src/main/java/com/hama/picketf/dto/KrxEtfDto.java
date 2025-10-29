package com.hama.picketf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class KrxEtfDTO {
    
    @JsonProperty("BAS_DD") private String BAS_DD;                     // 기준일자
    @JsonProperty("ISU_CD") private String ISU_CD;                     // 종목코드
    @JsonProperty("ISU_NM") private String ISU_NM;                     // 종목명
    @JsonProperty("TDD_CLSPRC") private String TDD_CLSPRC;             // 종가
    @JsonProperty("CMPPREVDD_PRC") private String CMPPREVDD_PRC;       // 전일대비 (가격)
    @JsonProperty("FLUC_RT") private String FLUC_RT;                   // 등락률 (%)
    @JsonProperty("NAV") private String NAV;                           // 순자산가치(NAV)
    @JsonProperty("TDD_OPNPRC") private String TDD_OPNPRC;             // 시가
    @JsonProperty("TDD_HGPRC") private String TDD_HGPRC;               // 고가
    @JsonProperty("TDD_LWPRC") private String TDD_LWPRC;               // 저가
    @JsonProperty("ACC_TRDVOL") private String ACC_TRDVOL;             // 거래량
    @JsonProperty("ACC_TRDVAL") private String ACC_TRDVAL;             // 거래대금
    @JsonProperty("MKTCAP") private String MKTCAP;                     // 시가총액
    @JsonProperty("INVSTASST_NETASST_TOTAMT") private String INVSTASST_NETASST_TOTAMT;   // 순자산총액
    @JsonProperty("LIST_SHRS") private String LIST_SHRS;               // 상장좌수
    @JsonProperty("IDX_IND_NM") private String IDX_IND_NM;             // 기초지수명
    @JsonProperty("OBJ_STKPRC_IDX") private String OBJ_STKPRC_IDX;     // 기초지수 종가
    @JsonProperty("CMPPREVDD_IDX") private String CMPPREVDD_IDX;       // 기초지수 전일대비
    @JsonProperty("FLUC_RT_IDX") private String FLUC_RT_IDX;           // 기초지수 등락률 (%)
}
