package com.hama.picketf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final VisitLogInterceptor visitLogInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(visitLogInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/health",
            "/actuator/**",
            "/api/**",
            "/css/**",
            "/img/**",
            "/js/**",
            "/favicon.ico",
            "/error");
  }
}
