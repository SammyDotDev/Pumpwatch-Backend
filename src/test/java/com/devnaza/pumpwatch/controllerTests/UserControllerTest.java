package com.devnaza.pumpwatch.controllerTests;

import com.devnaza.pumpwatch.modules.auth.controller.AuthController;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.model.User;
import com.devnaza.pumpwatch.modules.user.service.UserServiceImpl;
import com.devnaza.pumpwatch.utils.helpers.ToDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@WebMvcTest(AuthController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ToDto toDto;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceImpl userService;



    @Test
    public void signupSuccess() throws Exception {
        UserDto userDto = new UserDto("sammy", "dev", "devnaza", "devnaza@gmail.com", "12345678", "09080170598");

        User saved = new User();
        saved.setId(UUID.randomUUID());
        saved.setFirstName(userDto.firstName());
        saved.setLastName(userDto.lastName());
        saved.setUsername(userDto.username());
        saved.setPassword(userDto.password());
        saved.setEmail(userDto.email());
        saved.setPhoneNumber(userDto.phoneNumber());


        UserDto savedDto = new UserDto(
                saved.getFirstName(),
                saved.getLastName(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getPassword(),
                saved.getPhoneNumber()
        );


        when(toDto.convertToUserDto(any(User.class))).thenReturn(savedDto);
        when(userService.createUser(any(UserDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDto))).andExpect(status().isCreated()).andExpect(jsonPath("data.email").value(saved.getEmail()));

    }

}
