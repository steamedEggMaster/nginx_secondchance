package com.example.nginx_secondchance.dto.impl;

import com.example.nginx_secondchance.dto.OAuth2Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {
    private final Logger LOGGER = LoggerFactory.getLogger(KakaoResponse.class);
    private Map<String, Object> attributes;
    private Map<String, Object> kakaoAccountAttributes;
    private Map<String, Object> propertiesAttributes;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        this.propertiesAttributes = (Map<String, Object>) attributes.get("properties");
    }

    @Override
    public String getProviderId() {
        LOGGER.info("call KakaoResponse getProviderId()");
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        LOGGER.info("call KakaoResponse getName()");
        return propertiesAttributes.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return null;
    }
}
