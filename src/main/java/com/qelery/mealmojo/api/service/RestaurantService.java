package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.*;
import com.qelery.mealmojo.api.model.*;
import com.qelery.mealmojo.api.model.enums.PurchaseStatus;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.repository.*;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final AddressRepository addressRepository;
    private final MenuItemRepository menuItemRepository;
    private final LocationService locationService;
    private final PropertyCopier propertyCopier;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository,
                             OperatingHoursRepository operatingHoursRepository,
                             OrderRepository orderRepository,
                             OrderLineRepository orderLineRepository,
                             AddressRepository addressRepository,
                             MenuItemRepository menuItemRepository,
                             LocationService locationService,
                             PropertyCopier propertyCopier) {
        this.restaurantRepository = restaurantRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.addressRepository = addressRepository;
        this.menuItemRepository = menuItemRepository;
        this.locationService = locationService;
        this.propertyCopier = propertyCopier;
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

    public Restaurant getRestaurantByUser(Long restaurantId, Long userId) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByIdAndUserId(restaurantId, userId);
        return optionalRestaurant.orElseThrow(() ->  new RestaurantNotFoundException(restaurantId));
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setUser(getLoggedInUser());
        return restaurantRepository.save(restaurant);
    }

    public ResponseEntity<String> updateRestaurant(Long restaurantId, Restaurant newRestaurant) {
        Restaurant oldRestaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        propertyCopier.copyNonNull(newRestaurant, oldRestaurant);
        restaurantRepository.save(oldRestaurant);
        return ResponseEntity.ok("Restaurant updated");
    }

    public ResponseEntity<String> updateRestaurantHours(Long restaurantId, List<OperatingHours> newHoursList) {
        Role roleOfLoggedInUser = getLoggedInUser().getRole();

        Restaurant restaurant;
        if (roleOfLoggedInUser.equals(Role.ADMIN)) {
            restaurant = getRestaurant(restaurantId);
        } else {
            restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        }

        for (OperatingHours newHours: newHoursList) {
            Optional<OperatingHours> hours = operatingHoursRepository.findByRestaurantIdAndDayOfWeek(restaurantId, newHours.getDayOfWeek());
            if (hours.isPresent()) {
                OperatingHours oldHours = hours.get();
                propertyCopier.copyNonNull(newHours, oldHours);
                operatingHoursRepository.save(oldHours);
            } else {
                newHours.setRestaurant(restaurant);
                operatingHoursRepository.save(newHours);
            }
        }
        return ResponseEntity.ok("Hours updated");
    }

    public ResponseEntity<String> updateRestaurantAddress(Long restaurantId, Address newAddress) {
        Role roleOfLoggedInUser = getLoggedInUser().getRole();

        Restaurant restaurant;
        if (roleOfLoggedInUser.equals(Role.ADMIN)) {
            restaurant = getRestaurant(restaurantId);
        } else {
            restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        }

        Address oldAddress = restaurant.getAddress();
        propertyCopier.copyNonNull(newAddress, oldAddress);
        addressRepository.save(oldAddress);

        return ResponseEntity.ok("Address updated");
    }

    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurant(restaurantId); // handles RestaurantNotFoundException
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

    public MenuItem getMenuItemByRestaurantAndUser(Long menuItemId, Long restaurantId, Long userId) {
        Restaurant restaurant = getRestaurantByUser(restaurantId, userId);
        Optional<MenuItem> optionalMenuItem = restaurant.getMenuItems()
                .stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
        return optionalMenuItem.orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
    }

    public MenuItem createMenuItem(Long restaurantId, MenuItem menuItem) {
        Restaurant restaurant = getRestaurantByUser(restaurantId, getLoggedInUser().getId());
        menuItem.setRestaurant(restaurant);
        return menuItemRepository.save(menuItem);
    }

    public ResponseEntity<String> updateMenuItem(Long restaurantId, Long menuItemId, MenuItem newMenuItem) {
        Role roleOfLoggedInUser = getLoggedInUser().getRole();

        MenuItem oldMenuItem;
        if (roleOfLoggedInUser.equals(Role.ADMIN)) {
            oldMenuItem = getMenuItemByRestaurant(menuItemId, restaurantId); // handles RestaurantNotFound and MenuItemNotFound exceptions
        } else {
            oldMenuItem = getMenuItemByRestaurantAndUser(menuItemId, restaurantId, getLoggedInUser().getId()); // handles RestaurantNotFound and MenuItemNotFound exceptions
        }
        propertyCopier.copyNonNull(newMenuItem, oldMenuItem);
        menuItemRepository.save(oldMenuItem);
        return ResponseEntity.ok("Menu Item updated");
    }


    public List<Order> getOrders(Long restaurantId, Long userId) {
        if (restaurantId == null && userId == null) {
            return orderRepository.findAll();
        } else if (restaurantId == null) {
            return orderRepository.findAllByUserId(userId);
        } else if (userId == null) {
            return getRestaurant(restaurantId).getOrders();
        } else {
            return getRestaurant(restaurantId).getOrders()
                    .stream()
                    .filter(o -> o.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        }
    }

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        return getRestaurant(restaurantId).getOrders();
    }

//    public Order getOrderByRestaurant(Long restaurantId, Long orderId) {
//        Optional<Order> optionalOrder = orderRepository.findByIdAndRestaurantId(orderId, restaurantId);
//        return optionalOrder.orElseThrow(() -> new OrderNotFoundException(orderId));
//    }


    public OrderLine addOrderLineToCart(Long restaurantId, Long menuItemId, Integer quantity) {
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
            return orderLineRepository.save(orderLineAlreadyInCart.get());
        } else {
            OrderLine orderLine = new OrderLine();
            orderLine.setRestaurant(menuItem.getRestaurant());
            orderLine.setUser(getLoggedInUser());
            orderLine.setQuantity(quantity);
            orderLine.setPriceEach(menuItem.getPrice());
            orderLine.setMenuItem(menuItem);
            orderLine.setPurchaseStatus(PurchaseStatus.CART);
            return orderLineRepository.save(orderLine);
        }
    }

    public OrderLine editOrderLineInCart(Long restaurantId, Long menuItemId, Integer quantity) {
        MenuItem menuItem = this.getMenuItemByRestaurant(menuItemId, restaurantId);

        List<OrderLine> itemsInCart =
                orderLineRepository.findAllByPurchaseStatusAndUserId(PurchaseStatus.CART, getLoggedInUser().getId());
        Optional<OrderLine> optionalOrderLine = itemsInCart.stream()
                .filter(item -> item.getMenuItem().equals(menuItem)).findFirst();

        if (optionalOrderLine.isPresent()) {
            optionalOrderLine.get().setQuantity(quantity);
            return orderLineRepository.save(optionalOrderLine.get());
        } else {
            return addOrderLineToCart(restaurantId, menuItemId, quantity);
        }
    }


    public ResponseEntity<String> deleteOrderLineFromCart(Long restaurantId, Long menuItemId) {
        Optional<OrderLine> optionalOrderLine =
                orderLineRepository.findAllByPurchaseStatusAndUserIdAndMenuItemId(PurchaseStatus.CART,
                                                                                   getLoggedInUser().getId(),
                                                                                   menuItemId);
        if (optionalOrderLine.isPresent()) {
            orderLineRepository.delete(optionalOrderLine.get());
            return ResponseEntity.ok("Removed from cart");
        } else {
            throw new OrderLineNotFoundException(restaurantId, menuItemId);
        }
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


    public ResponseEntity<String> clearCart() {
        orderLineRepository.deleteAllByPurchaseStatusAndUserId(PurchaseStatus.CART, getLoggedInUser().getId());
        return ResponseEntity.ok("Cart cleared");
    }

    private User getLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }


}
