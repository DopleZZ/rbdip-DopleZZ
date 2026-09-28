package com.rbdip.bookstore.order;

import com.rbdip.bookstore.review.VerifiedPurchasePort;
import org.springframework.stereotype.Component;

@Component
public class OrderVerifiedPurchaseAdapter implements VerifiedPurchasePort {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderVerifiedPurchaseAdapter(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean hasVerifiedPurchase() {
        return !orderRepository.findAll().isEmpty() && !orderItemRepository.findAll().isEmpty();
    }
}
