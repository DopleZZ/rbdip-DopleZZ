package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderLineResolver {

    private static final int DEFAULT_QUANTITY = 1;

    private final ProductRepository productRepository;

    public OrderLineResolver(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<OrderLine> resolve(List<CreateOrderRequest.Item> items) {
        return items.stream().map(this::resolveLine).toList();
    }

    private OrderLine resolveLine(CreateOrderRequest.Item item) {
        Product product = productRepository
                .findById(item.productId())
                .orElseThrow(() -> new IllegalArgumentException("product " + item.productId() + " not found"));
        return new OrderLine(product, resolveQuantity(item.quantity()));
    }

    private int resolveQuantity(Integer quantity) {
        int resolved = quantity == null ? DEFAULT_QUANTITY : quantity;
        if (resolved <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        return resolved;
    }
}
