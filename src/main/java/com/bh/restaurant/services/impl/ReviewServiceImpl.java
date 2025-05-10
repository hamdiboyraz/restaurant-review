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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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

    @Override
    public Page<Review> listReviews(String restaurantId, Pageable pageable) {
        // Get the restaurant or throw an exception if not found
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        // Create a list of reviews
        List<Review> reviews = new ArrayList<>(restaurant.getReviews());

        // Get sorting information from the pageable object
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            // Get the first sort rule (e.g., "rating", ASC/DESC)
            Sort.Order order = sort.iterator().next();
            String property = order.getProperty();
            boolean isAscending = order.getDirection().isAscending();

            // Choose comparator based on the property
            Comparator<Review> comparator = switch (property) {
                case "datePosted" -> Comparator.comparing(Review::getDatePosted);
                case "rating" -> Comparator.comparing(Review::getRating);
                default -> Comparator.comparing(Review::getDatePosted);
            };

            // Sort the list in ascending or descending order
            reviews.sort(isAscending ? comparator : comparator.reversed());
        } else {
            // Default sort: by datePosted in descending order (most recent first)
            reviews.sort(Comparator.comparing(Review::getDatePosted).reversed());
        }

        // Calculate the start index for pagination
        int start = (int) pageable.getOffset();

        // If the start index exceeds list size, return an empty page
        if (start >= reviews.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, reviews.size());
        }

        // Calculate the end index (start + page size, capped at total size)
        int end = Math.min(start + pageable.getPageSize(), reviews.size());

        // Return the sublist as a page
        return new PageImpl<>(reviews.subList(start, end), pageable, reviews.size());
    }

    @Override
    public Optional<Review> getRestaurantReview(String restaurantId, String reviewId) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        return restaurant.getReviews().stream()
                .filter(r -> reviewId.equals(r.getId()))
                .findFirst();
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
