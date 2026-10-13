package hotel_ordering.service;

import hotel_ordering.entity.Order;
import hotel_ordering.entity.OrderItem;
import hotel_ordering.entity.Food;

import hotel_ordering.repository.OrderRepository;
import hotel_ordering.repository.OrderItemRepository;
import hotel_ordering.repository.FoodRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FoodRepository foodRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            FoodRepository foodRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.foodRepository = foodRepository;
    }

    // CREATE ORDER
    @Transactional
    public Order createOrder(Order order, List<OrderItem> items) {

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Order must contain at least one food item");
        }

        double total = 0;

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItem item : items) {

            if (item.getFoodId() == null ||
                item.getQuantity() == null ||
                item.getQuantity() <= 0) {

                throw new RuntimeException("Invalid food item or quantity");
            }

            Food food = foodRepository.findById(item.getFoodId())
                    .orElseThrow(() ->
                            new RuntimeException("Food not found: " + item.getFoodId()));

            if (!Boolean.TRUE.equals(food.getAvailable())) {
                throw new RuntimeException("Food is currently unavailable: " + food.getName());
            }

            // Use the current database price
            double price = food.getPrice();

            item.setPrice(price);
            total += price * item.getQuantity();

            orderItems.add(item);
        }

        // Set order information
        order.setHotelId(1L);
        order.setStatus("PENDING");
        order.setTotalAmount(total);
        order.setCreatedAt(LocalDateTime.now());

        // Save the main order first
        Order savedOrder = orderRepository.save(order);

        // Attach the generated order ID to every item
        for (OrderItem item : orderItems) {
            item.setOrderId(savedOrder.getId());
        }

        // Save all food items
        orderItemRepository.saveAll(orderItems);

        return savedOrder;
    }

    // READ ALL ORDERS
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // READ ONE ORDER'S ITEMS
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    // UPDATE ORDER STATUS
    public Order updateOrderStatus(Long orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);

        return orderRepository.save(order);
    }
}