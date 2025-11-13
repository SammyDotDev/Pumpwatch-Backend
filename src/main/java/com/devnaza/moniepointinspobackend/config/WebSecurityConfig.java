package com.devnaza.moniepointinspobackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable()).authorizeHttpRequests((requests) -> requests.requestMatchers(
                "/signup","/api/v1/auth/**").permitAll().anyRequest().authenticated());
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
}
