package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.Order;
import com.qelery.mealmojo.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }




//    @GetMapping("/restaurants/{restaurantId}/orders/{orderId}")
//    public Order getOrderByRestaurant(@PathVariable Long restaurantId,
//                          @PathVariable Long orderId) {
//        return this.orderService.getOrderByRestaurant(restaurantId, orderId);
//    }


//    @PostMapping("/restaurants/{restaurantId}/menuitems/{menuItemId}/orderlines/{quantity}")
//    public Order addOrderLineToCart(@PathVariable Long restaurantId,
//                                    @PathVariable Long menuItemId,
//                                    @PathVariable Integer quantity) {
//        return this.orderService.addOrderLineToCart(restaurantId, menuItemId, quantity);
//    }


//    @PatchMapping("/restaurants/{restaurantId}/orders/{orderId}")
//    public ResponseEntity<String> changeOrderCompletionStatus(@PathVariable Long restaurantId,
//                                                              @PathVariable Long orderId,
//                                                              @RequestBody Boolean completionStatus) {
//        return this.orderService.changeOrderCompletionStatus(restaurantId, orderId, completionStatus);
//    }
}
