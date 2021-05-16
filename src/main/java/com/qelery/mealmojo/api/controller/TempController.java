package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.service.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TempController {

    private TempService tempService;

    @Autowired
    public void setOrderService(TempService tempService) {
        this.tempService = tempService;
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
