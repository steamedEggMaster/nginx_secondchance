package com.example.nginx_secondchance.jwt;

import com.example.nginx_secondchance.dto.CustomOAuth2User;
import com.example.nginx_secondchance.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(JWTFilter.class);
    private final JWTUtil jwtUtil;
    public JWTFilter(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String path = request.getRequestURI(); //로그인 요청 및 api 문서 접근 uri는 통과시키기
        if (path.equals("/swagger/second-chance" )) {
            filterChain.doFilter(request, response);
            return;
        }

        //Cookie들을 불러온 뒤, Authorization Key에 담긴 쿠키를 찾음
        String authorization = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            LOGGER.info("doFilterInternal cookie.getName() : {}", cookie.getName());
            //System.out.println(cookie.getName());
            if(cookie.getName().equals("Authorization")){
                authorization = cookie.getValue();
                LOGGER.info("authorization : {}", authorization);
            }
        }

        //Authorization 헤더 검증
        if(authorization == null){
            LOGGER.info("token null");
            //System.out.println("token null");
            filterChain.doFilter(request, response);
            // 조건이 해당되면 메서드 종료(필수!)
            return;
        }

        //토큰
        String token = authorization;

        //토큰 소멸 시간 검증
        if(jwtUtil.isExpired(token)){
            LOGGER.info("token expired");
            //System.out.println("token expired");
            filterChain.doFilter(request, response);
            // 조건이 해당되면 메서드 종료(필수!)
            return;
        }

        LOGGER.info("Good State Token");

        //토큰에서 username, role 획득
        String username = jwtUtil.getUsername(token);
        //LOGGER.info("username : {}", username);
        String role = jwtUtil.getRole(token);
        //LOGGER.info("role : {}", role);

        //userDto를 생성하여 값 set
        UserDto userDto = UserDto.builder()
                .username(username)
                .role(role)
                .build();

        //userDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDto, null);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    //모든 요청에 대해 필터링을 수행할지, 특정 요청에만 수행할지 설정하는 메서드
    //이 메서드를 사용하지 않는다면 자동 false
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        // 여기서는 모든 요청에 대해 필터링을 수행하므로 false를 반환합니다.
//        return false;
//    }

}
