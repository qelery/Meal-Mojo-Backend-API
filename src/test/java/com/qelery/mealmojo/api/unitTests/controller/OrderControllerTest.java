package com.qelery.mealmojo.api.unitTests.controller;

import com.qelery.mealmojo.api.controller.OrderController;
import com.qelery.mealmojo.api.model.dto.OrderCreationDto;
import com.qelery.mealmojo.api.model.dto.OrderDto;
import com.qelery.mealmojo.api.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Test
    @DisplayName("Should return orders from service")
    void getOrders() {
        List<OrderDto> expectedDtoList = List.of(new OrderDto());
        when(orderService.getOrders(anyLong()))
                .thenReturn(expectedDtoList);

        List<OrderDto> actualDtoList = orderController.getOrders(1L);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    @DisplayName("Should return order by id from restaurant")
    void getSingleOrder() {
        OrderDto expectedDto = new OrderDto();
        when(orderService.getSingleOrder(anyLong()))
                .thenReturn(expectedDto);

        OrderDto actualDto = orderController.getSingleOrder(1L);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return the submitted order from service")
    void submitOrder() {
        OrderDto expectedDto = new OrderDto();
        when(orderService.submitOrder(any(OrderCreationDto.class)))
                .thenReturn(expectedDto);

        OrderDto actualDto = orderController.submitOrder(new OrderCreationDto());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return order that has been marked as complete from service")
    void markOrderComplete() {
        OrderDto expectedDto = new OrderDto();
        when(orderService.markOrderComplete(anyLong()))
                .thenReturn(expectedDto);

        OrderDto actualDto = orderController.markOrderComplete(1L);

        assertEquals(expectedDto, actualDto);
    }
}

