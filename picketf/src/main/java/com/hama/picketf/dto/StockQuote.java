package com.hama.picketf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockQuote {

    private String code;
    private String name;
    private String price;
    private String marketCap;
    private String per;
    private String pbr;

    // --- [ETF/추가 데이터] ---
    private String lastClosePrice;
    private String accumulatedTradingValue;

    private String oneMonthEarnRate;
    private String threeMonthEarnRate;
    private String sixMonthEarnRate;
    private String oneYearEarnRate;

    private String nav;
    private String fundPay;
    private String issueName;
}
