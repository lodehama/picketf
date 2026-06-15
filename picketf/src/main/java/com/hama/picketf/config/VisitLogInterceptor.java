package com.hama.picketf.config;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hama.picketf.security.CustomUser;
import com.hama.picketf.service.PageViewLogService;
import com.hama.picketf.service.VisitLogService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class VisitLogInterceptor implements HandlerInterceptor {

  private static final String ANONYMOUS_COOKIE_NAME = "picketf_vid";
  private static final int COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24 * 365;

  private final VisitLogService visitLogService;
  private final PageViewLogService pageViewLogService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!"GET".equalsIgnoreCase(request.getMethod())) {
      return true;
    }

    String path = request.getRequestURI();
    String deviceType = detectDeviceType(request.getHeader("User-Agent"));
    String anonymousId = getOrCreateAnonymousId(request, response);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Integer usNum = null;
    String visitorKey = "anon:" + anonymousId;

    try {
      if (authentication != null && authentication.isAuthenticated()
          && authentication.getPrincipal() instanceof CustomUser customUser) {
        usNum = customUser.getMember().getUs_num();
        visitorKey = "user:" + usNum;
        visitLogService.deleteAnonymousVisits("anon:" + anonymousId, usNum);
      }

      visitLogService.recordVisit(visitorKey, usNum, path);
      pageViewLogService.recordPageView(visitorKey, usNum, path, deviceType);
    } catch (Exception e) {
      log.warn("[VISIT_LOG] failed path={}, message={}", path, e.getMessage());
    }
    return true;
  }

  private String getOrCreateAnonymousId(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (ANONYMOUS_COOKIE_NAME.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
          return cookie.getValue();
        }
      }
    }

    String anonymousId = UUID.randomUUID().toString();
    Cookie cookie = new Cookie(ANONYMOUS_COOKIE_NAME, anonymousId);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
    response.addCookie(cookie);
    return anonymousId;
  }

  private String detectDeviceType(String userAgent) {
    if (userAgent == null || userAgent.isBlank()) {
      return "unknown";
    }

    String ua = userAgent.toLowerCase();

    if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider")) {
      return "bot";
    }
    if (ua.contains("ipad") || ua.contains("tablet")) {
      return "tablet";
    }
    if (ua.contains("mobi") || ua.contains("iphone") || ua.contains("android")) {
      return "mobile";
    }
    return "desktop";
  }
}
