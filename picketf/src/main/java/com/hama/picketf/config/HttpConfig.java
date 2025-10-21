// src/main/java/com/hama/picketf/config/HttpConfig.java
package com.hama.picketf.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
public class HttpConfig {

  @Value("${http.client.connect-timeout:3000}")
  private int connectTimeout;

  @Value("${http.client.read-timeout:5000}")
  private int readTimeout;

  @Bean
  public WebClient krxWebClient() {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
        .responseTimeout(Duration.ofMillis(readTimeout));

    ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
        .build();

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .exchangeStrategies(strategies)
        .build();
  }
}
