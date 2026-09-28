package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PricingCalculator {

    private static final int BULK_DISCOUNT_QUANTITY_THRESHOLD = 10;
    private static final BigDecimal BULK_DISCOUNT_RATE = new BigDecimal("0.95");
    private static final String CUSTOMER_TYPE_VIP = "vip";
    private static final BigDecimal VIP_DISCOUNT_RATE = new BigDecimal("0.9");
    private static final String CUSTOMER_TYPE_WHOLESALE = "wholesale";
    private static final BigDecimal WHOLESALE_DISCOUNT_RATE = new BigDecimal("0.85");
    private static final String COUPON_FIXED_CODE = "SAVE10";
    private static final BigDecimal COUPON_FIXED_AMOUNT = BigDecimal.TEN;
    private static final String COUPON_PERCENT_CODE = "SAVE20PERCENT";
    private static final BigDecimal COUPON_PERCENT_RATE = new BigDecimal("0.8");
    private static final BigDecimal LARGE_ORDER_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal LARGE_ORDER_DISCOUNT_RATE = new BigDecimal("0.98");
    private static final int MONEY_SCALE = 2;

    public record LineItem(BigDecimal price, int quantity) {
    }

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = subtotal(items);
        total = applyCustomerDiscount(total, customerType);
        total = applyCoupon(total, couponCode);
        total = total.max(BigDecimal.ZERO);
        total = applyLargeOrderDiscount(total);
        return total.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal subtotal(List<LineItem> items) {
        return items.stream().map(this::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal lineTotal(LineItem item) {
        BigDecimal total = item.price().multiply(BigDecimal.valueOf(item.quantity()));
        return item.quantity() > BULK_DISCOUNT_QUANTITY_THRESHOLD ? total.multiply(BULK_DISCOUNT_RATE) : total;
    }

    private BigDecimal applyCustomerDiscount(BigDecimal total, String customerType) {
        if (CUSTOMER_TYPE_VIP.equals(customerType)) {
            return total.multiply(VIP_DISCOUNT_RATE);
        }
        if (CUSTOMER_TYPE_WHOLESALE.equals(customerType)) {
            return total.multiply(WHOLESALE_DISCOUNT_RATE);
        }
        return total;
    }

    private BigDecimal applyCoupon(BigDecimal total, String couponCode) {
        if (COUPON_FIXED_CODE.equals(couponCode)) {
            return total.subtract(COUPON_FIXED_AMOUNT);
        }
        if (COUPON_PERCENT_CODE.equals(couponCode)) {
            return total.multiply(COUPON_PERCENT_RATE);
        }
        return total;
    }

    private BigDecimal applyLargeOrderDiscount(BigDecimal total) {
        return total.compareTo(LARGE_ORDER_THRESHOLD) > 0 ? total.multiply(LARGE_ORDER_DISCOUNT_RATE) : total;
    }
}
