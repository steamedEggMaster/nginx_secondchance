package com.example.nginx_secondchance.handler;

import com.example.nginx_secondchance.dto.CustomOAuth2User;
import com.example.nginx_secondchance.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomSuccessHandler.class);

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Value("${redirect.location}")
    private String location;

    //로그인 성공 시 JWT 발급
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        //정답! response가 이미 commit되는 이유였음
        //super.onAuthenticationSuccess(request, response, authentication);

        LOGGER.info("onAuthenticationSuccess Processing");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            LOGGER.info("response headers = {}", headerName + ": " + headerValue);
        }


        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUserDto().getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token =jwtUtil.createJwt(username, role, 60*60*60L);
        //response.sendRedirect(location); //성공 시 띄울 페이지(메인 페이지)
        if (response.isCommitted()) {
            logger.debug("응답이 이미 커밋된 상태입니다. " + location + "로 리다이렉트하도록 바꿀 수 없습니다.");
            return;
        }
        response.addCookie(createCookie("Authorization", token));
        getRedirectStrategy().sendRedirect(request, response, location);
    }

    private Cookie createCookie(String key, String value){
        LOGGER.info("createCookie Processing");
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60); // 쿠키 수명 설정
        //cookie.setSecure(true); //https 프로토콜을 통해서만 전송
        cookie.setPath("/"); // 해당 경로와 그 이하 경로에서 사용 가능
        cookie.setHttpOnly(true); //javaScript에서 해당 쿠키에 액세스 불가능 -> XSS 공격 대비 가능
        return cookie;
    }
}