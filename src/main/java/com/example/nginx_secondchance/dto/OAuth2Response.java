package com.example.nginx_secondchance.dto;

public interface OAuth2Response {
    String getProviderId(); //공급자 id ex) 각각의 id
    String getProvider(); //공급자 ex) kakao, google
    String getName(); //사용자 이름 ex)홍길동
    String getEmail(); //사용자 이메일 ex)~@~.com
}
