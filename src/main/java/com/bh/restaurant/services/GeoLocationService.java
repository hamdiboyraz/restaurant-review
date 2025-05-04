package com.bh.restaurant.services;

import com.bh.restaurant.domain.GeoLocation;
import com.bh.restaurant.domain.entities.Address;

public interface GeoLocationService {
    GeoLocation geoLocate(Address address);
}
