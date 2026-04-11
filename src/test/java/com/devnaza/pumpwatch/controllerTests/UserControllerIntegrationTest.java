package com.devnaza.pumpwatch.controllerTests;

import com.devnaza.pumpwatch.dto.ApiResponseDto;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?>postgres = new PostgreSQLContainer<>("postgres:18");

    @BeforeAll
    static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    static void afterAll(){
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        log.info("Configuring properties");
        log.info("server | port: {}", postgres.getJdbcUrl());
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp(){
        RestAssured.baseURI = "http://localhost:" + port;
        userRepository.deleteAll();
    }

    @Test
    public void createUser(){
        UserDto userDto = new UserDto("sammy", "dev", "devnaza", "devnaza@gmail.com", "12345678", "09080170598");
        ResponseEntity<ApiResponseDto> response = restTemplate.postForEntity("/api/v1/auth/signup", userDto, ApiResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        User saved = userRepository.findByEmail("devnaza@gmail.com").orElseThrow();
        assertThat(saved.getEmail()).isEqualTo("devnaza@gmail.com");
        assertThat(saved.getId()).isNotNull();
    }


}
