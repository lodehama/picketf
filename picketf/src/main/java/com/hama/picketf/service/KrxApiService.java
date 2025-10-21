// KrxApiService.java
package com.hama.picketf.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hama.picketf.dto.KrxEtf;

@Service
public class KrxApiService {

  private final RestTemplate rt = new RestTemplate();
  private final ObjectMapper om = new ObjectMapper();

  @Value("${krx.api.base-url}")
  private String baseUrl; // https://data-dbg.krx.co.kr
  @Value("${krx.api.path}")
  private String apiPath; // /svc/apis/etp/etf_bydd_trd
  @Value("${krx.api.key}")
  private String apiKey;
  @Value("${krx.api.auth.header-name}")
  private String authHeaderName;
  @Value("${krx.api.auth.header-format}")
  private String authHeaderFmt;
  @Value("${krx.api.test.basDd:}")
  private String testBasDd; // 비워두면 오늘

  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

  private String today() {
    return LocalDate.now().format(FMT);
  }

  /** 날짜 한 번 조회해서 전체 리스트를 Map(ISU_CD -> DTO)로 반환 */
  private Map<String, KrxEtf> fetchAllEtfsByDate(String basDd) {
    String date = (basDd == null || basDd.isBlank())
        ? (testBasDd != null && !testBasDd.isBlank() ? testBasDd : today())
        : basDd;

    String url = String.format("%s%s?basDd=%s", baseUrl, apiPath, date);

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.set(authHeaderName, String.format(authHeaderFmt, apiKey)); // Authorization: Bearer {키}
    HttpEntity<Void> req = new HttpEntity<>(headers);

    try {
      ResponseEntity<String> res = rt.exchange(url, HttpMethod.GET, req, String.class);
      if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null)
        return Map.of();

      JsonNode root = om.readTree(res.getBody());
      JsonNode arr = root.path("OutBlock_1"); // 이 API는 대개 배열 키가 OutBlock_1

      Map<String, KrxEtf> map = new LinkedHashMap<>();
      if (arr.isArray()) {
        for (JsonNode node : arr) {
          // 배열의 각 행을 DTO로
          KrxEtf dto = om.treeToValue(node, KrxEtf.class);
          String code = node.path("ISU_CD").asText("");
          if (!code.isBlank()) {
            map.put(code, dto);
          }
        }
      }
      return map;
    } catch (Exception e) {
      // 필요시 로깅
      return Map.of();
    }
  }

  /** 단건: 전체 리스트에서 코드로 매칭 */
  public KrxEtf getEtf(String isuCd, String basDd) {
    Map<String, KrxEtf> all = fetchAllEtfsByDate(basDd);
    return all.getOrDefault(isuCd, null);
  }

  /** 배치: 한 번만 조회해서 요청한 코드들만 추출 */
  public Map<String, KrxEtf> getEtfs(List<String> isuCds, String basDd) {
    Map<String, KrxEtf> all = fetchAllEtfsByDate(basDd);
    Map<String, KrxEtf> result = new LinkedHashMap<>();
    for (String code : isuCds) {
      result.put(code, all.getOrDefault(code, null));
    }
    return result;
  }
}
