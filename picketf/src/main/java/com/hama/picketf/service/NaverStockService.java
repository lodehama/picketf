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

      String name = getText(root, "stockName", "N/A");
      String price = "0";

      JsonNode deal = root.path("dealTrendInfos");
      if (deal.isArray() && deal.size() > 0) {
        price = getText(deal.get(0), "closePrice", "0");
      }

      String marketCap = "0", per = "N/A", pbr = "N/A";
      String lastClosePrice = "0", accumulatedTradingValue = "0";

      String oneMonthEarnRate = "N/A", threeMonthEarnRate = "N/A",
          sixMonthEarnRate = "N/A", oneYearEarnRate = "N/A";

      String nav = "0", fundPay = "N/A", issueName = "N/A";

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

      // 가격 보정: price가 0이면 전일가로 대체(옵션)
      if ("0".equals(price) && !"0".equals(lastClosePrice))
        price = lastClosePrice;

      // ✅ 괴리율 계산 = ((시장가 - NAV) / NAV) * 100
      String premium = "-";
      double priceVal = parseNumber(price);
      double navVal = parseNumber(nav);
      if (priceVal > 0 && navVal > 0) {
        double rate = ((priceVal - navVal) / navVal) * 100.0;
        premium = String.format("%+.2f%%", rate); // 예: +0.31%
      }

      return StockQuote.builder()
          .code(code).name(name).price(price)
          .marketCap(marketCap).per(per).pbr(pbr)
          .lastClosePrice(lastClosePrice).accumulatedTradingValue(accumulatedTradingValue)
          .oneMonthEarnRate(oneMonthEarnRate).threeMonthEarnRate(threeMonthEarnRate)
          .sixMonthEarnRate(sixMonthEarnRate).oneYearEarnRate(oneYearEarnRate)
          .nav(nav).fundPay(fundPay).issueName(issueName)
          .premium(premium) // ✅ 추가
          .build();

    } catch (Exception e) {
      return StockQuote.builder()
          .code(code).name("N/A").price("0")
          .marketCap("0").per("N/A").pbr("N/A")
          .lastClosePrice("0").accumulatedTradingValue("0")
          .oneMonthEarnRate("N/A").threeMonthEarnRate("N/A")
          .sixMonthEarnRate("N/A").oneYearEarnRate("N/A")
          .nav("0").fundPay("N/A").issueName("N/A")
          .premium("-") // ✅ 추가
          .build();
    }
  }

  private static String getText(JsonNode node, String field, String def) {
    JsonNode v = node.get(field);
    return (v != null && !v.isNull()) ? v.asText() : def;
  }

  // 숫자 문자열 → double (쉼표 제거)
  private static double parseNumber(String s) {
    try {
      return Double.parseDouble(s.replace(",", ""));
    } catch (Exception ignore) {
      return 0d;
    }
  }

  public List<StockQuote> fetchQuotes(List<String> codes) {
    return codes.parallelStream().map(this::fetchQuote).collect(Collectors.toList());
  }
}
