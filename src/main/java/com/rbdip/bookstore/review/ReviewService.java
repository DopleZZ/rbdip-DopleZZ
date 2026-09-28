package com.rbdip.bookstore.review;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final VerifiedPurchasePort verifiedPurchasePort;

    public ReviewService(ReviewRepository reviewRepository, VerifiedPurchasePort verifiedPurchasePort) {
        this.reviewRepository = reviewRepository;
        this.verifiedPurchasePort = verifiedPurchasePort;
    }

    public Review addReview(Long productId, String authorName, Integer rating, String comment) {
        boolean verifiedPurchase = verifiedPurchasePort.hasVerifiedPurchase();
        Review review = new Review(productId, authorName == null ? "anonymous" : authorName, rating, comment);
        return reviewRepository.save(review);
    }

    public List<Review> listReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
