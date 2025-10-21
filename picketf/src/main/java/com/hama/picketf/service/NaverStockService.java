package com.hama.picketf.service;

import com.hama.picketf.dto.NaverStockQuote;

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

  public NaverStockQuote fetchQuote(String code) {
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

      // 주식용 필드(있으면 쓰고, ETF면 0일 수 있음)
      String marketCap = "0", per = "N/A", pbr = "N/A";

      // --- ETF/추가 데이터 ---
      String lastClosePrice = "0";
      String marketValue = "0"; // ✅ AUM (시가총액 성격: etfKeyIndicator.marketValue)
      String accumulatedTradingValue = "0";

      String oneMonthEarnRate = "N/A", threeMonthEarnRate = "N/A",
          sixMonthEarnRate = "N/A", oneYearEarnRate = "N/A";

      String nav = "0", fundPay = "N/A", issueName = "N/A";

      JsonNode infos = root.path("totalInfos");
      if (infos.isArray()) {
        for (JsonNode node : infos) {
          String c = getText(node, "code", "");
          String v = getText(node, "value", "");

          // 주식 기본
          if ("per".equalsIgnoreCase(c))
            per = v;
          else if ("pbr".equalsIgnoreCase(c))
            pbr = v;

          // ETF/공통
          else if ("marketValue".equalsIgnoreCase(c))
            marketValue = v; // ✅ 변경: 예전엔 marketCap에 넣었음
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

      // ✅ etfKeyIndicator 폴백
      JsonNode key = root.path("etfKeyIndicator");
      if (!key.isMissingNode() && !key.isNull()) {
        if ("0".equals(marketValue) || "-".equals(marketValue)) {
          marketValue = getText(key, "marketValue", marketValue); // 예: "5조 9,010억"
        }
        if ("0".equals(nav) || "N/A".equalsIgnoreCase(nav)) {
          nav = getText(key, "nav", nav);
        }
        if ("N/A".equalsIgnoreCase(issueName)) {
          issueName = getText(key, "issuerName", issueName);
        }
        if ("N/A".equalsIgnoreCase(fundPay) && key.hasNonNull("totalFee")) {
          fundPay = key.get("totalFee").asText() + "%"; // 0.0062 → "0.0062%"
        }
      }

      // 가격 보정(옵션)
      if ("0".equals(price) && !"0".equals(lastClosePrice))
        price = lastClosePrice;

      // ✅ 괴리율 계산
      String premium = "-";
      double priceVal = parseNumber(price);
      double navVal = parseNumber(nav);
      if (priceVal > 0 && navVal > 0) {
        double rate = ((priceVal - navVal) / navVal) * 100.0;
        premium = String.format("%+.2f%%", rate);
      }

      return NaverStockQuote.builder()
          .code(code).name(name).price(price)
          .marketCap(marketCap).per(per).pbr(pbr)
          .lastClosePrice(lastClosePrice)
          .marketValue(marketValue) // ✅ 추가
          .accumulatedTradingValue(accumulatedTradingValue)
          .oneMonthEarnRate(oneMonthEarnRate).threeMonthEarnRate(threeMonthEarnRate)
          .sixMonthEarnRate(sixMonthEarnRate).oneYearEarnRate(oneYearEarnRate)
          .nav(nav).fundPay(fundPay).issueName(issueName)
          .premium(premium)
          .build();

    } catch (Exception e) {
      return NaverStockQuote.builder()
          .code(code).name("N/A").price("0")
          .marketCap("0").per("N/A").pbr("N/A")
          .lastClosePrice("0")
          .marketValue("0") // ✅ 추가
          .accumulatedTradingValue("0")
          .oneMonthEarnRate("N/A").threeMonthEarnRate("N/A")
          .sixMonthEarnRate("N/A").oneYearEarnRate("N/A")
          .nav("0").fundPay("N/A").issueName("N/A")
          .premium("-")
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

  public List<NaverStockQuote> fetchQuotes(List<String> codes) {
    return codes.parallelStream().map(this::fetchQuote).collect(Collectors.toList());
  }
}
