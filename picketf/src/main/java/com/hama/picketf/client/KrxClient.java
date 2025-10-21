package com.hama.picketf.client;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

@Component
public class KrxClient {

  private final WebClient webClient;

  @Value("${krx.api.base-url}")
  private String baseUrl;

  @Value("${krx.api.path}")
  private String path;

  @Value("${krx.api.key:}")
  private String apiKey;

  @Value("${krx.api.auth.type:none}")
  private String authType;

  @Value("${krx.api.auth.header-name:Authorization}")
  private String headerName;

  @Value("${krx.api.auth.header-format:Bearer %s}")
  private String headerFormat;

  @Value("${krx.api.auth.query-name:serviceKey}")
  private String queryName;

  public KrxClient(WebClient krxWebClient) {
    this.webClient = krxWebClient;
  }

  /** bodyParams 예: Map.of("basDd","20251020") */
  public JsonNode post(Map<String, String> bodyParams) {
    // 1) URL 단순 결합
    String url = baseUrl + path;

    // 2) 쿼리 인증(type=query)일 경우에만 안전하게 붙임
    if ("query".equalsIgnoreCase(authType) && apiKey != null && !apiKey.isBlank()) {
      url = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam(queryName, apiKey)
            .toUriString();
    }

    WebClient.RequestHeadersSpec<?> spec = webClient.post()
        .uri(url) // ★ uriBuilder 사용 안 함
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(bodyParams == null ? Map.of() : bodyParams);

    // 3) 헤더 인증(type=header)일 경우에만 추가
    if ("header".equalsIgnoreCase(authType) && apiKey != null && !apiKey.isBlank()) {
      spec = spec.header(headerName, String.format(headerFormat, apiKey));
    }

    // 4) 상태코드 에러 처리 + 응답 파싱
    try {
      return spec.retrieve()
          .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
              resp -> resp.createException().flatMap(Mono::error))
          .bodyToMono(JsonNode.class)
          .block();
    } catch (Exception e) {
      // 필요 시 로그로 교체 가능
      e.printStackTrace();
      throw e;
    }
  }
}
