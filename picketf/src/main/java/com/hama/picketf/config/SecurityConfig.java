package com.hama.picketf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import com.hama.picketf.model.util.UserRole;
import com.hama.picketf.service.UserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private UserDetailService memberDetailService;

  @Value("${security.rememberme.key}")
  private String rememberMeKey;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
    repo.setHeaderName("X-XSRF-TOKEN"); // csrf 토큰 헤더 이름 변경

    // XOR 마스킹 말고, "원본 토큰"을 헤더로 받도록 고정
    CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();

    http
        .csrf(csrf -> csrf.csrfTokenRepository(repo)
            .csrfTokenRequestHandler(handler) // CSRF 토큰을 헤더에서 읽도록 설정
        )
        .authorizeHttpRequests(req -> req
            .requestMatchers("/isa", "/subs", "/subs/**").hasAuthority(UserRole.USER.name())
            .requestMatchers("/admin/**").hasAnyAuthority(UserRole.ADMIN.name())
            .anyRequest().permitAll())
        .formLogin(form -> form
            .permitAll()
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/"))
        .rememberMe(rm -> rm
            .userDetailsService(memberDetailService)
            .key(rememberMeKey)
            .rememberMeCookieName("rememberme")
            .tokenValiditySeconds(60 * 60 * 24 * 300))
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .permitAll());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}