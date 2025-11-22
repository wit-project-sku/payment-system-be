/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wit.payment.global.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsConfig corsConfig;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (request, response, ex) -> {
                          // 인증 실패 → 401
                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType("application/json;charset=UTF-8");
                          response
                              .getWriter()
                              .write(
                                  """
                          {
                            "success": false,
                            "code": 401,
                            "message": "로그인이 필요합니다."
                          }
                          """);
                        })
                    .accessDeniedHandler(
                        (request, response, ex) -> {
                          // 권한 부족 → 403
                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType("application/json;charset=UTF-8");
                          response
                              .getWriter()
                              .write(
                                  """
                          {
                            "success": false,
                            "code": 403,
                            "message": "접근 권한이 없습니다."
                          }
                          """);
                        }))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/",
                        "/api/users/register",
                        "/api/auth/login/**",
                        "/api/auth/refresh",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                    .permitAll()

                    // 키오스크용 API
                    .requestMatchers(
                        "/api/products/{product-id}",
                        "/api/categories/{category-id}/products",
                        "/api/categories",
                        "/api/categories/first")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
