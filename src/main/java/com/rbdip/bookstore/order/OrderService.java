package com.rbdip.bookstore.order;

import com.rbdip.bookstore.customer.Customer;
import com.rbdip.bookstore.customer.CustomerService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final CreateOrderValidator createOrderValidator;
    private final OrderLineResolver orderLineResolver;
    private final CustomerService customerService;
    private final PricingCalculator pricingCalculator;
    private final OrderConfirmationMailer orderConfirmationMailer;
    private final OrderRecorder orderRecorder;

    public OrderService(
            CreateOrderValidator createOrderValidator,
            OrderLineResolver orderLineResolver,
            CustomerService customerService,
            PricingCalculator pricingCalculator,
            OrderConfirmationMailer orderConfirmationMailer,
            OrderRecorder orderRecorder) {
        this.createOrderValidator = createOrderValidator;
        this.orderLineResolver = orderLineResolver;
        this.customerService = customerService;
        this.pricingCalculator = pricingCalculator;
        this.orderConfirmationMailer = orderConfirmationMailer;
        this.orderRecorder = orderRecorder;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        createOrderValidator.validate(request);
        List<OrderLine> lines = orderLineResolver.resolve(request.items());
        Customer customer = customerService.findOrCreate(
                request.customerFullName(), request.customerAddress(), request.customerPhone());
        BigDecimal total = pricingCalculator.calculateOrderTotal(
                toPricingLines(lines), request.customerType(), request.couponCode());
        Order order = orderRecorder.record(customer, lines);
        orderConfirmationMailer.sendOrderConfirmation(request.customerFullName(), order.getId(), total);
        return order;
    }

    private List<PricingCalculator.LineItem> toPricingLines(List<OrderLine> lines) {
        return lines.stream()
                .map(line -> new PricingCalculator.LineItem(line.product().getPrice(), line.quantity()))
                .toList();
    }
}
