package com.example.nginx_secondchance.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @PackageName : com.example.nginx_secondchance.controller
 * @FileName : FeatureController
 * @Author : noglass_gongdae
 * @Date : 2024-06-30
 * @Blog : https://blog.naver.com/noglass_gongdae
 * @GitHub :
 */

@RestController
@RequestMapping("/feature")
public class FeatureController {

    private final Logger LOGGER = LoggerFactory.getLogger(FeatureController.class);

    @GetMapping("/donation")
    public String check(){

        LOGGER.info("hi donation");

        return "hi";

    }

}
