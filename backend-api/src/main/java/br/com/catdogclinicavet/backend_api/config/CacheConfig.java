package br.com.catdogclinicavet.backend_api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // A configuração do Redis já é feita automaticamente pelo Spring Boot
    // através do application.properties
}