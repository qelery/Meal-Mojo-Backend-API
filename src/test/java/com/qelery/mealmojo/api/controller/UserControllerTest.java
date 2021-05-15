package com.qelery.mealmojo.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.security.JwtRequestFilter;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsServiceImpl;
import com.qelery.mealmojo.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(controllers=UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private JwtUtils jwtUtils;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerAcceptsUserObject() throws Exception {
        User userObject = new User("testuser93014@gmail.com", "password", null, null);
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/users/register")
                .content(asJsonString(userObject))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void registerReturns400WhenNonNullableValuesAreNull() throws Exception {
        User userObject = new User(null, "password", null, null);
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/users/register")
                .content(asJsonString(userObject))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void loginUser() {
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
