package com.rbdip.bookstore.order;

import java.math.BigDecimal;

public record OrderLine(String productName, BigDecimal price, int quantity) {
}
