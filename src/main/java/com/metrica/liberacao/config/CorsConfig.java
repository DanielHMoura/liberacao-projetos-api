package com.metrica.liberacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                "https://metricaarquitetura.com",
                "http://metricaarquitetura.com",
                "https://www.metricaarquitetura.com",
                "http://www.metricaarquitetura.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");

    }
    /** correcaoe do cors para permitir acesso do frontend hospedado em metricaarquitetura.com */
}
