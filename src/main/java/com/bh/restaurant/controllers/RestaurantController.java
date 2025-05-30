package com.bh.restaurant.controllers;

import com.bh.restaurant.domain.RestaurantCreateUpdateRequest;
import com.bh.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.bh.restaurant.domain.dtos.RestaurantDto;
import com.bh.restaurant.domain.dtos.RestaurantSummaryDto;
import com.bh.restaurant.domain.entities.Restaurant;
import com.bh.restaurant.mappers.RestaurantMapper;
import com.bh.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto) {
        RestaurantCreateUpdateRequest request = restaurantMapper
                .toRestaurantCreateUpdateRequest(requestDto);
        Restaurant restaurant = restaurantService.createRestaurant(request);
        RestaurantDto createdRestaurantDto = restaurantMapper.toRestaurantDto(restaurant);
        return ResponseEntity.ok(createdRestaurantDto);
    }

    @GetMapping
    public Page<RestaurantSummaryDto> searchRestaurants(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false) Float latitude,
            @RequestParam(required = false) Float longitude,
            @RequestParam(required = false) Float radius,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Restaurant> searchResult = restaurantService.searchRestaurants(
                q,
                minRating,
                latitude,
                longitude,
                radius,
                PageRequest.of(page - 1, size)
        );
        return searchResult.map(restaurantMapper::toSummaryDto);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable("restaurantId") String restaurantId) {
        return restaurantService.getRestaurant(restaurantId)
                .map(restaurant -> ResponseEntity.ok(restaurantMapper.toRestaurantDto(restaurant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDto> updateRestaurant(
            @PathVariable String restaurantId,
            @Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto) {

        // Convert the DTO to domain object
        RestaurantCreateUpdateRequest request =
                restaurantMapper.toRestaurantCreateUpdateRequest(requestDto);

        // Update the restaurant
        Restaurant updated = restaurantService.updateRestaurant(restaurantId, request);

        // Convert and return the updated restaurant
        return ResponseEntity.ok(restaurantMapper.toRestaurantDto(updated));
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
}
