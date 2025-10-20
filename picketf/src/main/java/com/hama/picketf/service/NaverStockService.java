package com.hama.picketf.service;

import com.hama.picketf.dto.StockQuote;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NaverStockService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper om = new ObjectMapper();

  public StockQuote fetchQuote(String code) {
    try {
      String url = "https://m.stock.naver.com/api/stock/" + code + "/integration";
      String response = restTemplate.getForObject(url, String.class);
      JsonNode root = om.readTree(response);

      // 기본 필드
      String name = getText(root, "stockName", "N/A");
      String price = "0";

      JsonNode deal = root.path("dealTrendInfos");
      if (deal.isArray() && deal.size() > 0) {
        price = getText(deal.get(0), "closePrice", "0");
      }

      // totalInfos 에서 추가 항목들 파싱
      String marketCap = "0";
      String per = "N/A";
      String pbr = "N/A";

      String lastClosePrice = "0";
      String accumulatedTradingValue = "0"; // 원문이 "79,854백만" 형태면 그대로 둠(혹은 여기서 파싱해도 됨)

      String oneMonthEarnRate = "N/A";
      String threeMonthEarnRate = "N/A";
      String sixMonthEarnRate = "N/A";
      String oneYearEarnRate = "N/A";

      String nav = "0";
      String fundPay = "N/A";
      String issueName = "N/A";

      JsonNode infos = root.path("totalInfos");
      if (infos.isArray()) {
        for (JsonNode node : infos) {
          String c = getText(node, "code", "");
          String v = getText(node, "value", "");

          if ("marketValue".equalsIgnoreCase(c))
            marketCap = v;
          else if ("per".equalsIgnoreCase(c))
            per = v;
          else if ("pbr".equalsIgnoreCase(c))
            pbr = v;

          else if ("lastClosePrice".equalsIgnoreCase(c))
            lastClosePrice = v;
          else if ("accumulatedTradingValue".equalsIgnoreCase(c))
            accumulatedTradingValue = v;

          else if ("oneMonthEarnRate".equalsIgnoreCase(c))
            oneMonthEarnRate = v;
          else if ("threeMonthEarnRate".equalsIgnoreCase(c))
            threeMonthEarnRate = v;
          else if ("sixMonthEarnRate".equalsIgnoreCase(c))
            sixMonthEarnRate = v;
          else if ("oneYearEarnRate".equalsIgnoreCase(c))
            oneYearEarnRate = v;

          else if ("nav".equalsIgnoreCase(c))
            nav = v;
          else if ("fundPay".equalsIgnoreCase(c))
            fundPay = v;
          else if ("issueName".equalsIgnoreCase(c))
            issueName = v;
        }
      }

      // Lombok Builder 사용: 필요한 것만 채워서 반환
      return StockQuote.builder()
          .code(code)
          .name(name)
          .price(price)
          .marketCap(marketCap)
          .per(per)
          .pbr(pbr)
          .lastClosePrice(lastClosePrice)
          .accumulatedTradingValue(accumulatedTradingValue)
          .oneMonthEarnRate(oneMonthEarnRate)
          .threeMonthEarnRate(threeMonthEarnRate)
          .sixMonthEarnRate(sixMonthEarnRate)
          .oneYearEarnRate(oneYearEarnRate)
          .nav(nav)
          .fundPay(fundPay)
          .issueName(issueName)
          .build();

    } catch (Exception e) {
      return StockQuote.builder()
          .code(code).name("N/A").price("0")
          .marketCap("0").per("N/A").pbr("N/A")
          .lastClosePrice("0").accumulatedTradingValue("0")
          .oneMonthEarnRate("N/A").threeMonthEarnRate("N/A")
          .sixMonthEarnRate("N/A").oneYearEarnRate("N/A")
          .nav("0").fundPay("N/A").issueName("N/A")
          .build();
    }
  }

  private static String getText(JsonNode node, String field, String def) {
    JsonNode v = node.get(field);
    return (v != null && !v.isNull()) ? v.asText() : def;
  }

  public List<StockQuote> fetchQuotes(List<String> codes) {
    return codes.parallelStream()
        .map(this::fetchQuote)
        .collect(Collectors.toList());
  }
}
