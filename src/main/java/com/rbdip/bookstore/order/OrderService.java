package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final String STATUS_NEW = "new";

    private final CreateOrderValidator createOrderValidator;
    private final OrderLineResolver orderLineResolver;
    private final PricingCalculator pricingCalculator;
    private final OrderConfirmationMailer orderConfirmationMailer;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(
            CreateOrderValidator createOrderValidator,
            OrderLineResolver orderLineResolver,
            PricingCalculator pricingCalculator,
            OrderConfirmationMailer orderConfirmationMailer,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.createOrderValidator = createOrderValidator;
        this.orderLineResolver = orderLineResolver;
        this.pricingCalculator = pricingCalculator;
        this.orderConfirmationMailer = orderConfirmationMailer;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        createOrderValidator.validate(request);
        List<OrderLine> lines = orderLineResolver.resolve(request.items());
        BigDecimal total = pricingCalculator.calculateOrderTotal(
                toPricingLines(lines), request.customerType(), request.couponCode());
        Order order = orderRepository.save(
                new Order(request.customerFullName(), request.customerAddress(), request.customerPhone(), STATUS_NEW));
        lines.forEach(line -> orderItemRepository.save(
                new OrderItem(order.getId(), line.productName(), line.price(), line.quantity())));
        orderConfirmationMailer.sendOrderConfirmation(request.customerFullName(), order.getId(), total);
        return order;
    }

    private List<PricingCalculator.LineItem> toPricingLines(List<OrderLine> lines) {
        return lines.stream()
                .map(line -> new PricingCalculator.LineItem(line.price(), line.quantity()))
                .toList();
    }
}
