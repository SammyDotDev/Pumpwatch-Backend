package com.devnaza.moniepointinspobackend.config;

import com.devnaza.moniepointinspobackend.utils.helpers.ToDto;
import com.devnaza.moniepointinspobackend.utils.helpers.ToEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    ToEntity toEntity(){
        return new ToEntity();
    }

    @Bean
    ToDto toDto(){
        return new ToDto();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
