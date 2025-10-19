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
      if (deal.isArray() && deal.size() > 0)
        price = getText(deal.get(0), "closePrice", "0");

      String marketCap = "0";
      String per = "N/A";
      String pbr = "N/A";

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
        }
      }

      return new StockQuote(code, name, price, marketCap, per, pbr);

    } catch (Exception e) {
      return new StockQuote(code, "N/A", "0", "0", "N/A", "N/A");
    }
  }

  private static String getText(JsonNode node, String field, String def) {
    JsonNode v = node.get(field);
    return (v != null && !v.isNull()) ? v.asText() : def;
  }

  public List<StockQuote> fetchQuotes(List<String> codes) {
    // 병렬 스트림으로 여러 종목 동시에 처리
    return codes.parallelStream()
        .map(this::fetchQuote) // 기존 단건 호출 재사용
        .collect(Collectors.toList());
  }
}
