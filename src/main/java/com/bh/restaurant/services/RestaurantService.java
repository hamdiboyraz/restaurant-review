package com.bh.restaurant.services;

import com.bh.restaurant.domain.RestaurantCreateUpdateRequest;
import com.bh.restaurant.domain.entities.Restaurant;

public interface RestaurantService {
    Restaurant createRestaurant(RestaurantCreateUpdateRequest restaurant);
}
