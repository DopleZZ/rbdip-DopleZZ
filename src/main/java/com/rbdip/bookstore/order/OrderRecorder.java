package com.rbdip.bookstore.order;

import com.rbdip.bookstore.customer.Customer;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderRecorder {

    private static final String STATUS_NEW = "new";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderRecorder(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order record(Customer customer, List<OrderLine> lines) {
        Order order = orderRepository.save(new Order(customer, STATUS_NEW));
        lines.forEach(line -> orderItemRepository.save(new OrderItem(order.getId(), line.product(), line.quantity())));
        return order;
    }
}
