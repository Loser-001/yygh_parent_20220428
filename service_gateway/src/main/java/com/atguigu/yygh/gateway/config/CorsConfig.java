package com.atguigu.yygh.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @author Jerry
 * @create 2022-02-28 21:51
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");

        // 允许的请求头
//        corsConfiguration.addAllowedHeader("*");
//        // 允许的请求源 （如：http://localhost:8080）
//        corsConfiguration.addAllowedOrigin("*");
//        // 允许的请求方法 ==> GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
//        corsConfiguration.addAllowedMethod("*");
        // URL 映射 （如： /admin/**）


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);

    }
}
