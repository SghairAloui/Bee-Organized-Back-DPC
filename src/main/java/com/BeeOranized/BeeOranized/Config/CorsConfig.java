package com.BeeOranized.BeeOranized.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/stomp-endpoint")
                .allowedOrigins("http://localhost:4200","*") // Remplacez ceci par l'URL de votre frontend
                .allowedMethods("*") // Autorisez toutes les méthodes HTTP
                .allowCredentials(true); // Autorise les credentials (cookies, autorisation)
    }
}

