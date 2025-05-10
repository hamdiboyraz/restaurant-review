package com.bh.restaurant.services;

import com.bh.restaurant.domain.ReviewCreateUpdateRequest;
import com.bh.restaurant.domain.entities.Review;
import com.bh.restaurant.domain.entities.User;

public interface ReviewService {
    Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest review);
}