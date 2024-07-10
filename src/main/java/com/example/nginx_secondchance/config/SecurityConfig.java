package com.example.nginx_secondchance.config;

import com.example.nginx_secondchance.handler.CustomSuccessHandler;
import com.example.nginx_secondchance.jwt.JWTFilter;
import com.example.nginx_secondchance.jwt.JWTUtil;
import com.example.nginx_secondchance.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    @Autowired
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil){
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOrigin("http://localhost:5173");
//        configuration.addAllowedMethod("GET");
//        configuration.addAllowedMethod("POST");
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //oauth2
        httpSecurity
                .oauth2Login((oauth2) -> oauth2
                        //.defaultSuccessUrl(successUrl)
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );
        //JWTFilter 추가
        httpSecurity
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //From 로그인 방식 disable
        httpSecurity
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        httpSecurity
                .httpBasic((auth) -> auth.disable());
//        httpSecurity
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
//        httpSecurity
//            .csrf((csrf) -> csrf
//                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/v1/feature/**"))
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//            );
        httpSecurity
                .csrf((auth) -> auth.disable());
//        httpSecurity
//            .authorizeHttpRequests((auth) -> auth
//                .requestMatchers("/v1/login/**").permitAll()
//                .requestMatchers("/v1/features/**").hasRole("USER")
//                .anyRequest().authenticated());
        //경로별 인가 작업
        httpSecurity
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/swagger/**", "/v3/**").permitAll()
                        .requestMatchers("/v1/feature/**").hasRole("USER")
                        .requestMatchers("feature/**").permitAll()
                        .anyRequest().authenticated());
        //세션 설정 : STATELESS
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//        //Cors 설정
//        httpSecurity
//                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//
//                        CorsConfiguration configuration = new CorsConfiguration();
//
//                        configuration.setAllowedOrigins(Collections.singletonList(address));
//                        configuration.setAllowedMethods(Collections.singletonList("*"));
//                        configuration.setAllowCredentials(true); //서버가 클라이언트에게 인증된 사용자 정보를 전달할 수 있는지 여부를 결정
//                        configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
//                        configuration.setMaxAge(3600L);
//                        //서버에서 클라이언트로 반환될 때 노출되는 헤더를 설정하는 부분
//                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie")); //쿠키를 설정할 때 사용
//                        configuration.setExposedHeaders(Collections.singletonList("Authorization")); //인증된 요청을 할 때 사용
//                        //서버로부터 반환되는 응답에 있는 헤더들을 접근할 수 있게됨.
//                        return configuration;
//                    }
//                }));

        //h2 콘솔 사용을 위함
//            httpSecurity
//                .csrf(csrf -> csrf.disable());
//            httpSecurity
//                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));
        return httpSecurity.build();
    }
}