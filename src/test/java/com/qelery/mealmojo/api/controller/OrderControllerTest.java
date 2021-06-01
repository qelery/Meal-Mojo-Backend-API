package com.qelery.mealmojo.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qelery.mealmojo.api.exception.GlobalExceptionHandler;
import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
import com.qelery.mealmojo.api.model.Restaurant;
import com.qelery.mealmojo.api.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    private JacksonTester<Restaurant> jsonRestaurant;

    private MockMvc mockMvc;

    @BeforeEach
    protected void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void canRetrieveRestaurantByIdWhenExists() throws Exception {
        // given
        given(orderService.getRestaurant(2L))
                .willReturn(new Restaurant(2L, "Business Inc", "Test description.",
                        "America/Chicago", "", "", true, 1.00,
                        30, 30, null, null, null, null,
                        null, null));

        // when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/order/restaurants/2")
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(
                jsonRestaurant.write(new Restaurant(2L, "Business Inc", "Test description.",
                        "America/Chicago", "", "", true, 1.00,
                        30, 30, null, null, null, null,
                        null, null)).getJson()
        );
    }

    @Test
    void canRetrieveRestaurantByIdWhenDoesNotExist() throws Exception {
        //given
        given(orderService.getRestaurant(2L))
                .willThrow(new RestaurantNotFoundException(2L));

        // when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/order/restaurants/2")
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }
}
