package com.bh.restaurant.controllers;

import com.bh.restaurant.domain.ReviewCreateUpdateRequest;
import com.bh.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.bh.restaurant.domain.dtos.ReviewDto;
import com.bh.restaurant.domain.entities.Review;
import com.bh.restaurant.domain.entities.User;
import com.bh.restaurant.mappers.ReviewMapper;
import com.bh.restaurant.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable("restaurantId") String restaurantId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto review,
            @AuthenticationPrincipal Jwt jwt) {
        // Convert the review DTO to a domain object
        ReviewCreateUpdateRequest ReviewCreateUpdateRequest =
                reviewMapper.toReviewCreateUpdateRequest(review);
        // Extract user details from JWT
        User user = jwtToUser(jwt);
        // Create the review
        Review createdReview = reviewService.createReview(
                user, restaurantId, ReviewCreateUpdateRequest);
        // Return the created review as DTO
        return ResponseEntity.ok(reviewMapper.toDto(createdReview));
    }

    @GetMapping
    public Page<ReviewDto> listReviews(
            @PathVariable String restaurantId,
            @PageableDefault(
                    size = 20,
                    page = 0,
                    sort = "datePosted",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return reviewService
                .listReviews(restaurantId, pageable)
                .map(reviewMapper::toDto);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getRestaurantReview(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("reviewId") String reviewId) {
        return reviewService
                .getReview(restaurantId, reviewId)
                .map(reviewMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("reviewId") String reviewId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto review,
            @AuthenticationPrincipal Jwt jwt) {
        // Convert the DTO to domain object
        ReviewCreateUpdateRequest reviewCreateUpdateRequest =
                reviewMapper.toReviewCreateUpdateRequest(review);
        // Extract user information from JWT
        User user = jwtToUser(jwt);
        // Call service to perform update
        Review updatedReview = reviewService.updateReview(
                user,
                restaurantId,
                reviewId,
                reviewCreateUpdateRequest);
        // Return updated review
        return ResponseEntity.ok(reviewMapper.toDto(updatedReview));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("reviewId") String reviewId,
            @AuthenticationPrincipal Jwt jwt) {

        User user = jwtToUser(jwt);
        reviewService.deleteReview(user, restaurantId, reviewId);
        return ResponseEntity.noContent().build();
    }

    private User jwtToUser(Jwt jwt) {
        return new User(
                jwt.getSubject(), // User's unique ID
                jwt.getClaimAsString("preferred_username"), // Username
                jwt.getClaimAsString("given_name"), // First name
                jwt.getClaimAsString("family_name") // Last name
        );
    }
}
