package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.configuration.security.CustomUserDetailService;
import com.openclassrooms.etudiant.configuration.security.JwtAuthenticationFilter;
import com.openclassrooms.etudiant.dto.LoginRequestDTO;
import com.openclassrooms.etudiant.dto.RegisterDTO;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.mapper.UserDtoMapper;
import com.openclassrooms.etudiant.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private static final String REGISTER_URL = "/api/register";
    private static final String LOGIN_URL = "/api/login";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

    // Mocks nécessaires pour la configuration Spring Security
    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void registerUserWithoutRequiredData() throws Exception {
        // GIVEN
        RegisterDTO registerDTO = new RegisterDTO();
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .content(objectMapper.writeValueAsString(registerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void registerAlreadyExistUser() throws Exception {
        // GIVEN
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName(FIRST_NAME);
        registerDTO.setLastName(LAST_NAME);
        registerDTO.setLogin(LOGIN);
        registerDTO.setPassword(PASSWORD);

        when(userDtoMapper.toEntity(any())).thenReturn(new User());
        doThrow(new IllegalArgumentException("User already exists"))
                .when(userService).register(any());
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .content(objectMapper.writeValueAsString(registerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void registerUserSuccessful() throws Exception {
        // GIVEN
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName(FIRST_NAME);
        registerDTO.setLastName(LAST_NAME);
        registerDTO.setLogin(LOGIN);
        registerDTO.setPassword(PASSWORD);

        when(userDtoMapper.toEntity(any())).thenReturn(new User());
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .content(objectMapper.writeValueAsString(registerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void loginWithNonExistentUser() throws Exception {
        // GIVEN
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setLogin(null);
        loginRequestDTO.setPassword(PASSWORD);

        doThrow(new IllegalArgumentException("Login cannot be null"))
                .when(userService).login(null, PASSWORD);
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void loginWithNonExistentPassword() throws Exception {
        // GIVEN
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setLogin(LOGIN);
        loginRequestDTO.setPassword(null);

        doThrow(new IllegalArgumentException("Password cannot be null"))
                .when(userService).login(LOGIN, null);
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void loginSuccessful_returns200WithToken() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setLogin(LOGIN);
        loginRequestDTO.setPassword(PASSWORD);
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void registerSuccessful_verifyMapperAndServiceAreCalled() throws Exception {

    }
    
    @Test
    public void loginWithBadCredentials_returns400() throws Exception {

    }
}
