package com.bh.restaurant.services.impl;

import com.bh.restaurant.domain.ReviewCreateUpdateRequest;
import com.bh.restaurant.domain.entities.Photo;
import com.bh.restaurant.domain.entities.Restaurant;
import com.bh.restaurant.domain.entities.Review;
import com.bh.restaurant.domain.entities.User;
import com.bh.restaurant.exceptions.RestaurantNotFoundException;
import com.bh.restaurant.exceptions.ReviewNotAllowedException;
import com.bh.restaurant.repositories.RestaurantRepository;
import com.bh.restaurant.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Final and non-final fields will be initialized in the constructor
public class ReviewServiceImpl implements ReviewService {

    private final RestaurantRepository restaurantRepository;
    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest createReview) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        // Check if user has already reviewed this restaurant
        boolean hasExistingReview = restaurant.getReviews().stream()
                .anyMatch(r -> r.getWrittenBy().getId().equals(author.getId()));

        if (hasExistingReview) {
            throw new ReviewNotAllowedException("User has already reviewed this restaurant");
        }

        LocalDateTime now = LocalDateTime.now();

        // Create photos
        List<Photo> photos = createReview.getPhotoIds().stream().map(url -> {
            Photo photo = new Photo();
            photo.setUrl(url);
            photo.setUploadDate(now);
            return photo;
        }).collect(Collectors.toList());

        // Create review
        Review review = Review.builder()
                .id(UUID.randomUUID().toString())
                .content(createReview.getContent())
                .rating(createReview.getRating())
                .photos(photos)
                .datePosted(now)
                .lastEdited(now)
                .writtenBy(author)
                .build();

        // Add review to restaurant
        restaurant.getReviews().add(review);

        // Update restaurant's average rating
        updateRestaurantAverageRating(restaurant);

        // Save restaurant with new review
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        // Return the newly created review
        return updatedRestaurant.getReviews().stream()
                .filter(r -> r.getDatePosted().equals(review.getDatePosted()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error retrieving created review"));
    }

    private void updateRestaurantAverageRating(Restaurant restaurant) {
        double average = restaurant.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        restaurant.setAverageRating((float) average);
    }

    private Restaurant getRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + restaurantId));
    }
}
