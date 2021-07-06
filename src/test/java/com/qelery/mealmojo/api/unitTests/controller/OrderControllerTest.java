//package com.qelery.mealmojo.api.unitTests.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.qelery.mealmojo.api.controller.OrderController;
//import com.qelery.mealmojo.api.exception.GlobalExceptionHandler;
//import com.qelery.mealmojo.api.exception.MenuItemNotFoundException;
//import com.qelery.mealmojo.api.exception.RestaurantNotFoundException;
//import com.qelery.mealmojo.api.model.MenuItem;
//import com.qelery.mealmojo.api.model.Order;
//import com.qelery.mealmojo.api.model.OrderLine;
//import com.qelery.mealmojo.api.model.Restaurant;
//import com.qelery.mealmojo.api.model.enums.PaymentMethod;
//import com.qelery.mealmojo.api.model.enums.PurchaseStatus;
//import com.qelery.mealmojo.api.service.OrderService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.json.JacksonTester;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.BDDMockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderControllerTest {
//
//    @InjectMocks
//    private OrderController orderController;
//
//    @Mock
//    private OrderService orderService;
//
//    private JacksonTester<Restaurant> jsonRestaurant;
//    private JacksonTester<List<Restaurant>> jsonListRestaurants;
//    private JacksonTester<List<MenuItem>> jsonListMenuItems;
//    private JacksonTester<MenuItem> jsonMenuItem;
//    private JacksonTester<OrderLine> jsonOrderLine;
//    private JacksonTester<List<OrderLine>> jsonListOrderLines;
//    private JacksonTester<Order> jsonOrder;
//    private JacksonTester<List<Order>> jsonListOrders;
//
//    private MockMvc mockMvc;
//    private Restaurant dummyRestaurant;
//    private MenuItem dummyMenuItem;
//    private OrderLine dummyOrderLine;
//    private Order dummyOrder;
//
//    @BeforeEach
//    protected void setup() {
//        JacksonTester.initFields(this, new ObjectMapper());
//        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
//                .setControllerAdvice(new GlobalExceptionHandler())
//                .build();
////        dummyRestaurant = new Restaurant(2L, "Business Inc", "Test description.",
////                "America/Chicago", "", "", true, 1.00,
////                30, 30, null, null, null,
////                null, null, null);
////        dummyMenuItem = new MenuItem(50L, "Cheeseburger", 4.99, "",
////                true, dummyRestaurant, dummyRestaurant.getId());
//        dummyOrderLine = new OrderLine(4L, 3, dummyMenuItem.getPrice(), dummyRestaurant.getBusinessName(),
//                PurchaseStatus.CART, null, dummyMenuItem, dummyRestaurant, null);
//        dummyOrder = new Order(6L, null, 3.00, false, PaymentMethod.CARD,
//                true, null, null, dummyRestaurant);
//    }
//
//    @Test
//    void canRetrieveAllRestaurants() throws Exception {
//        when(orderService.getRestaurants())
//                .thenReturn(List.of(dummyRestaurant));
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/restaurants")
//                    .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonListRestaurants.write(List.of(dummyRestaurant)).getJson()
//        );
//    }
//
//    @Test
//    void canRetrieveRestaurantByIdWhenExists() throws Exception {
//        when(orderService.getRestaurant(2L))
//                .thenReturn(dummyRestaurant);
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/restaurants/2")
//                    .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonRestaurant.write(dummyRestaurant).getJson()
//        );
//    }
//
//    @Test
//    void canRetrieveRestaurantByIdWhenDoesNotExist() throws Exception {
//        when(orderService.getRestaurant(2L))
//                .thenThrow(new RestaurantNotFoundException(2L));
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/restaurants/2")
//                    .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
//        assertThat(response.getContentAsString())
//                .contains(new RestaurantNotFoundException(2L).getMessage());
//    }
//
//    @Test
//    void canRetrieveAllMenuItemsByRestaurantId() throws Exception {
//        when(orderService.getMenuItemsByRestaurant(2L))
//            .thenReturn(List.of(dummyMenuItem));
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/restaurants/2/menuitems")
//                    .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonListMenuItems.write(List.of(dummyMenuItem)).getJson()
//        );
//    }
//
//    @Test
//    void canRetrieveMenuItemByRestaurantIdAndMenuItemIdWhenExists() throws Exception {
//        when(orderService.getMenuItemByRestaurant(2L, 50L))
//                .thenReturn(dummyMenuItem);
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/restaurants/2/menuitems/50")
//                    .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonMenuItem.write(dummyMenuItem).getJson()
//        );
//    }
//
//    @Test
//    void canRetrieveMenuItemByRestaurantIdAndMenuItemIdWhenDoesNotExist() throws Exception {
//        when(orderService.getMenuItemByRestaurant(2L, 75L))
//                .thenThrow(new MenuItemNotFoundException(75L));
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/restaurants/2/menuitems/75")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
//        assertThat(response.getContentAsString())
//                .contains(new MenuItemNotFoundException(75L).getMessage());
//    }
//
//    @Test
//    void canAddOrderLineByRestaurantIdAndMenuItemIdAndQuantity() throws Exception {
//        when(orderService.addOrderLineToCart(2L, 50L, 3))
//                .thenReturn(dummyOrderLine);
//
//        MockHttpServletResponse response = mockMvc.perform(
//                post("/api/order/restaurants/2/menuitems/50/orderlines/3")
//                    .contentType(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonOrderLine.write(dummyOrderLine).getJson()
//        );
//    }
//
//    @Test
//    void canEditOrderLineByRestaurantIdAndMenuItemIdAndQuantity() throws Exception {
//        dummyOrderLine.setQuantity(4);
//        when(orderService.editOrderLineInCart(2L, 50L, 4))
//                .thenReturn(dummyOrderLine);
//
//        MockHttpServletResponse response = mockMvc.perform(
//                put("/api/order/restaurants/2/menuitems/50/orderlines/4")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonOrderLine.write(dummyOrderLine).getJson()
//        );
//    }
//
//    @Test
//    void canDeleteOrderLineByRestaurantIdAndMenuItemId() throws Exception {
//        doNothing().when(orderService).deleteOrderLineFromCart(2L, 50L);
//
//        MockHttpServletResponse response = mockMvc.perform(
//                delete("/api/order/restaurants/2/menuitems/50/orderlines")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
//    }
//
//    @Test
//    void canRetrieveAllOrders() throws Exception {
//        when(orderService.getOrders(null))
//                .thenReturn(List.of(dummyOrder));
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/past")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonListOrders.write(List.of(dummyOrder)).getJson()
//        );
//    }
//
//    @Test
//    void canRetrieveOrdersByRestaurantId() throws Exception {
//        when(orderService.getOrders(2L))
//                .thenReturn(List.of(dummyOrder));
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/past?restaurantId=2")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonListOrders.write(List.of(dummyOrder)).getJson()
//        );
//    }
//
//    @Test
//    void canRetrieveCartOrderLines() throws Exception {
//        when(orderService.getCart())
//                .thenReturn(List.of(dummyOrderLine));
//
//        MockHttpServletResponse response = mockMvc.perform(
//                get("/api/order/cart")
//                    .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonListOrderLines.write(List.of(dummyOrderLine)).getJson()
//        );
//    }
//
//    @Test
//    void canCheckoutCart() throws Exception {
//        when(orderService.checkoutCart(Mockito.any(Order.class)))
//                .thenReturn(dummyOrder);
//
//        MockHttpServletResponse response = mockMvc.perform(
//                post("/api/order/cart/checkout").contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonOrder.write(dummyOrder).getJson()))
//                .andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo(
//                jsonOrder.write(dummyOrder).getJson()
//        );
//    }
//
//}
