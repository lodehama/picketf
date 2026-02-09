package com.hama.picketf.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hama.picketf.dto.KrxEtfDTO;

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
  private String authHeaderName; // 예: Authorization

  @Value("${krx.api.auth.header-format}")
  private String authHeaderFmt; // 예: Bearer %s

  /** dev 테스트용(선택). 비워두면 미사용 */
  @Value("${krx.api.test.basDd:}")
  private String testBasDd;

  /** 최신 영업일 탐색 시도 일수(기본 7일) */
  @Value("${krx.api.latest.fallback-days:7}")
  private int fallbackDays;

  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

  /** 실제로 사용된 기준일(화면 표기를 위해 공개) */
  private volatile String lastResolvedBasDd;

  private String today() {
    return LocalDate.now().format(FMT);
  }

  /** basDd가 없으면: today() → (옵션) testBasDd 순으로 시작일 결정 */
  private String resolveStartDate(String basDd) {
    if (basDd != null && !basDd.isBlank())
      return basDd;
    // dev 환경에서 특정 날짜를 강제로 시작점으로 쓰고 싶다면 testBasDd 우선
    if (testBasDd != null && !testBasDd.isBlank())
      return testBasDd;
    return today();
  }

  /** 실제 호출 + 파싱 (주어진 날짜 1회 시도) */
  private Map<String, KrxEtfDTO> fetchOnce(String date) {
    String url = String.format("%s%s?basDd=%s", baseUrl, apiPath, date);

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.set(authHeaderName, String.format(authHeaderFmt, apiKey));

    try {
      ResponseEntity<String> res = rt.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
      if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null)
        return Map.of();

      JsonNode root = om.readTree(res.getBody());
      JsonNode arr = root.path("OutBlock_1");
      if (!arr.isArray() || arr.isEmpty())
        return Map.of();

      // 트러블 슈팅 : 비영업일/미확정 데이터 걸러내기
      String close = arr.get(0).path("TDD_CLSPRC").asText("");
      if (close.isBlank() || "-".equals(close))
        return Map.of();

      Map<String, KrxEtfDTO> map = new LinkedHashMap<>();
      for (JsonNode node : arr) {
        KrxEtfDTO dto = om.treeToValue(node, KrxEtfDTO.class);
        String code = node.path("ISU_CD").asText("");
        if (!code.isBlank())
          map.put(code, dto);
      }
      return map;

    } catch (Exception e) {
      // 필요 시 로깅
      return Map.of();
    }
  }

  /**
   * 날짜 한 번 조회해서 전체 리스트를 Map(ISU_CD -> DTO)로 반환.
   * basDd 없으면: 오늘부터 fallbackDays 만큼 과거로 내려가며 첫 성공 날짜를 사용.
   */
  private Map<String, KrxEtfDTO> fetchAllEtfsByDate(String basDd) {
    String start = resolveStartDate(basDd);
    LocalDate cursor = LocalDate.parse(start, FMT);

    for (int i = 0; i < Math.max(1, fallbackDays); i++) {
      String tryDate = cursor.minusDays(i).format(FMT);
      Map<String, KrxEtfDTO> result = fetchOnce(tryDate);
      if (!result.isEmpty()) {
        // 성공: 실제 사용 기준일 저장
        this.lastResolvedBasDd = tryDate;
        return result;
      }
    }
    // 실패: 기준일 정보 초기화
    this.lastResolvedBasDd = null;
    return Map.of();
  }

  /** 단건: 전체 리스트에서 코드로 매칭 */
  public KrxEtfDTO getEtf(String isuCd, String basDd) {
    Map<String, KrxEtfDTO> all = fetchAllEtfsByDate(basDd);
    return all.getOrDefault(isuCd, null);
  }

  /** 배치: 한 번만 조회해서 요청한 코드들만 추출 */
  public Map<String, KrxEtfDTO> getEtfs(List<String> isuCds, String basDd) {
    Map<String, KrxEtfDTO> all = fetchAllEtfsByDate(basDd);
    Map<String, KrxEtfDTO> result = new LinkedHashMap<>();
    for (String code : isuCds) {
      result.put(code, all.getOrDefault(code, null));
    }
    return result;
  }

  /** 화면에 "기준일자: YYYYMMDD"를 찍고 싶을 때 사용 */
  public String getLastResolvedBasDd() {
    return lastResolvedBasDd;
  }
}
