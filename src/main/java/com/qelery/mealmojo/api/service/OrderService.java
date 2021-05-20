package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.*;
import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.model.enums.PurchaseStatus;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.repository.*;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final LocationService locationService;

    @Autowired
    public OrderService(RestaurantRepository restaurantRepository,
                             OrderRepository orderRepository,
                             OrderLineRepository orderLineRepository,
                             LocationService locationService) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.locationService = locationService;
    }

    public List<Restaurant> getRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getRestaurantsWithinDistance(double latitude, double longitude, int maxDistance) {
        return locationService.findRestaurantsWithinDistance(latitude, longitude, maxDistance);
    }

    public Restaurant getRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return restaurant.get();
        } else {
            throw new RestaurantNotFoundException(restaurantId);
        }
    }

    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurant(restaurantId);
        return restaurant.getMenuItems();
    }

    public MenuItem getMenuItemByRestaurant(Long menuItemId, Long restaurantId) {
        Restaurant restaurant = getRestaurant(restaurantId);
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        return optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
    }

    public List<Order> getOrders(Long restaurantId) {
        if (restaurantId == null) {
            return orderRepository.findAllByUserId(getLoggedInUser().getId());
        } else {
            return getRestaurant(restaurantId).getOrders()
                    .stream()
                    .filter(o -> o.getUser().getId().equals(getLoggedInUser().getId()))
                    .collect(Collectors.toList());
        }
    }

    public ResponseEntity<OrderLine> addOrderLineToCart(Long restaurantId, Long menuItemId, Integer quantity) {
        MenuItem menuItem = this.getMenuItemByRestaurant(menuItemId, restaurantId);

        List<OrderLine> itemsInCart = orderLineRepository.findAllByPurchaseStatusAndUserId(PurchaseStatus.CART,
                                                                                           getLoggedInUser().getId());
        boolean itemsFromOtherRestaurantCarted = itemsInCart.stream()
                                                      .anyMatch(line -> !line.getRestaurant().getId().equals(restaurantId));
        if (itemsFromOtherRestaurantCarted) {
            clearCart();
        }

        Optional<OrderLine> orderLineAlreadyInCart = itemsInCart.stream()
                                                        .filter(line -> line.getMenuItem().equals(menuItem))
                                                        .findFirst();
        if (orderLineAlreadyInCart.isPresent()) {
            int quantityInCart = orderLineAlreadyInCart.get().getQuantity();
            orderLineAlreadyInCart.get().setQuantity(quantity + quantityInCart);
            return new ResponseEntity<>(orderLineRepository.save(orderLineAlreadyInCart.get()), HttpStatus.CREATED);
        } else {
            OrderLine orderLine = new OrderLine();
            orderLine.setRestaurant(menuItem.getRestaurant());
            orderLine.setRestaurantName(menuItem.getRestaurant().getBusinessName());
            orderLine.setUser(getLoggedInUser());
            orderLine.setQuantity(quantity);
            orderLine.setPriceEach(menuItem.getPrice());
            orderLine.setMenuItem(menuItem);
            orderLine.setPurchaseStatus(PurchaseStatus.CART);
            return new ResponseEntity<>(orderLineRepository.save(orderLine), HttpStatus.CREATED);
        }
    }

    public ResponseEntity<OrderLine> editOrderLineInCart(Long restaurantId, Long menuItemId, Integer quantity) {
        MenuItem menuItem = this.getMenuItemByRestaurant(menuItemId, restaurantId);

        List<OrderLine> itemsInCart =
                orderLineRepository.findAllByPurchaseStatusAndUserId(PurchaseStatus.CART, getLoggedInUser().getId());
        Optional<OrderLine> optionalOrderLine = itemsInCart.stream()
                .filter(item -> item.getMenuItem().equals(menuItem)).findFirst();

        if (optionalOrderLine.isPresent()) {
            optionalOrderLine.get().setQuantity(quantity);
            return new ResponseEntity<>(orderLineRepository.save(optionalOrderLine.get()), HttpStatus.OK);
        } else {
            return addOrderLineToCart(restaurantId, menuItemId, quantity);
        }
    }

    public ResponseEntity<Void> deleteOrderLineFromCart(Long restaurantId, Long menuItemId) {
        Optional<OrderLine> optionalOrderLine =
                orderLineRepository.findAllByPurchaseStatusAndUserIdAndMenuItemId(PurchaseStatus.CART,
                                                                                   getLoggedInUser().getId(),
                                                                                   menuItemId);
        if (optionalOrderLine.isPresent()) {
            orderLineRepository.delete(optionalOrderLine.get());
            return ResponseEntity.noContent().build();
        } else {
            throw new OrderLineNotFoundException(restaurantId, menuItemId);
        }
    }


    public List<OrderLine> getCart() {
        return orderLineRepository.findAllByPurchaseStatusAndUserId(PurchaseStatus.CART,
                getLoggedInUser().getId());
    }

    public Order checkoutCart(Order order) {
        List<OrderLine> itemsInCart = orderLineRepository.findAllByPurchaseStatusAndUserId(PurchaseStatus.CART,
                                                                                           getLoggedInUser().getId());

        if (itemsInCart.isEmpty()) {
            throw new EmptyCartException();
        }

        order.setRestaurant(itemsInCart.get(0).getRestaurant());
        order.setUser(getLoggedInUser());
        order.setOrderLines(new ArrayList<>());
        orderRepository.save(order);
        for (OrderLine orderLine: itemsInCart) {
            orderLine.setOrder(order);
            orderLine.setPurchaseStatus(PurchaseStatus.PURCHASED);
            orderLineRepository.save(orderLine);
            order.getOrderLines().add(orderLine);
        }
        return orderRepository.save(order);
    }


    public ResponseEntity<Void> clearCart() {
        orderLineRepository.deleteAllByPurchaseStatusAndUserId(PurchaseStatus.CART, getLoggedInUser().getId());
        return ResponseEntity.noContent().build();
    }

    private User getLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

}
