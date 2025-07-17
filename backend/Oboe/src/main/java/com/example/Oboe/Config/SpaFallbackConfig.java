package com.example.Oboe.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaFallbackConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Fallback route để tất cả request frontend đều trả về index.html
        registry.addViewController("/{spring:[a-zA-Z0-9-_]+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/{spring:[a-zA-Z0-9-_]+}/**{spring:?!(\\.js|\\.css|\\.png|\\.jpg|\\.jpeg|\\.svg|\\.woff|\\.ttf)$}")
                .setViewName("forward:/index.html");

        //  hỗ trợ redirect sau khi đăng nhập OAuth2
        registry.addViewController("/oauth2/redirect")
                .setViewName("forward:/index.html");
    }
}
