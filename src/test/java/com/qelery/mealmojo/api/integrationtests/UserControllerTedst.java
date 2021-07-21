


//package com.qelery.mealmojo.api.unitTests.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.qelery.mealmojo.api.controller.UserController;
//import com.qelery.mealmojo.api.exception.GlobalExceptionHandler;
//import com.qelery.mealmojo.api.model.entity.User;
//import com.qelery.mealmojo.api.model.enums.Role;
//import com.qelery.mealmojo.api.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.json.JacksonTester;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//    private User dummyCustomerUser;
//
//    private MockMvc mockMvc;
//    private JacksonTester<User> jsonUser;
//
//    @BeforeEach
//    protected void setup() {
//        JacksonTester.initFields(this, new ObjectMapper());
//        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
//                .setControllerAdvice(new GlobalExceptionHandler())
//                .build();
//        this.dummyCustomerUser = new User(1L, "test@test.com", "password", Role.CUSTOMER, null, null, null);
//    }
//
//
//    @Test
//    void shouldRegisterNewUser() throws Exception {
//        doNothing().when(userService).createUserWithCustomerRole(Mockito.any(User.class));
//
//        mockMvc.perform(
//                post("/auth/users/register/customer")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonUser.write(dummyCustomerUser).getJson()))
//                .andExpect(status().isNoContent())
//                .andExpect(content().string("")
//                );
//    }
//
//}
