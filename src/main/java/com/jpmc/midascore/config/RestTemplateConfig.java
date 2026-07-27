package com.jpmc.midascore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean       //An object that is created and managed by Spring.
    public RestTemplate restTemplate() {    //A Java object that can make API calls
        return new RestTemplate();
    }
}
