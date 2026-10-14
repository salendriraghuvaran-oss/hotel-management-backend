package hotel_ordering.controller;

import hotel_ordering.entity.Order;
import hotel_ordering.entity.OrderItem;
import hotel_ordering.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175",
    "https://hotel-management-frontend-h6ob.vercel.app"
})
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // CREATE ORDER
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequest request) {

        try {
            Order order = new Order();

            order.setTableId(request.tableId);
            order.setUserId(null);

            Order savedOrder = orderService.createOrder(
                    order,
                    request.items
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Order placed successfully",
                            "orderId", savedOrder.getId(),
                            "status", savedOrder.getStatus(),
                            "totalAmount", savedOrder.getTotalAmount()
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // READ ALL ORDERS
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // READ ITEMS FOR AN ORDER
    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(
            @PathVariable Long orderId) {

        return orderService.getOrderItems(orderId);
    }

    // UPDATE ORDER STATUS
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {

        try {
            String status = request.get("status");

            if (status == null || status.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status is required"));
            }

            Order updatedOrder = orderService.updateOrderStatus(
                    orderId,
                    status
            );

            return ResponseEntity.ok(updatedOrder);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // REQUEST FORMAT FROM REACT
    public static class OrderRequest {

        public Long tableId;

        public List<OrderItem> items;
    }
}