package com.bh.restaurant.domain;

import com.bh.restaurant.domain.entities.Address;
import com.bh.restaurant.domain.entities.OperatingHours;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantCreateUpdateRequest {
    private String name; // Restaurant's name
    private String cuisineType; // Type of cuisine served
    private String contactInformation; // Contact details
    private Address address; // Physical location
    private OperatingHours operatingHours; // Opening hours
    private List<String> photoIds; // References to uploaded photos
}
