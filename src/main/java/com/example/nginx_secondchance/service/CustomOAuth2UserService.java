package com.example.nginx_secondchance.service;

import com.example.nginx_secondchance.dto.CustomOAuth2User;
import com.example.nginx_secondchance.dto.OAuth2Response;
import com.example.nginx_secondchance.dto.UserDto;
import com.example.nginx_secondchance.dto.impl.KakaoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public CustomOAuth2UserService(@Lazy BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

//    @Value("${encoding.password}")
//    private String password;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        LOGGER.info("CustomOAuth2UserService oAuth2User.getAttributes() : {}", oAuth2User.getAttributes());
        //System.out.println(oAuth2User.getAttributes());
        LOGGER.info("CustomOAuth2UserService userRequest.getClientRegistration().getRegistrationId() : {}", userRequest.getClientRegistration().getRegistrationId());
        //System.out.println(userRequest.getClientRegistration().getRegistrationId());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {
            LOGGER.info("CustomOAuth2UserService 로그인 성공");
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            LOGGER.info("CustomOAuth2UserService kakaoResponse : {}", oAuth2Response);
        } else {
            LOGGER.info("CustomOAuth2UserService 로그인 실패");
            //System.out.println("로그인 실패");
            return null;
        }
        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        //LOGGER.info("CustomOAuth2UserService getProviderId() : {}", providerId);
        String username = provider + "_" + providerId;
        String name = oAuth2Response.getName();
        //LOGGER.info("CustomOAuth2UserService getName() : {}", name);
        String email = oAuth2Response.getEmail();
        String role = "ROLE_USER";
        Long sharingCount = 0L;
        String takeaway = "";

        UserDto userDto = UserDto.builder()
                .name(name)
                .role(role)
                .username(username)
                .takeaway(takeaway)
                .build();
        return new CustomOAuth2User(userDto, oAuth2User.getAttributes());
    }
}