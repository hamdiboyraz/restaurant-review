package com.bh.restaurant.mappers;

import com.bh.restaurant.domain.ReviewCreateUpdateRequest;
import com.bh.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.bh.restaurant.domain.dtos.ReviewDto;
import com.bh.restaurant.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
// Component model is set to "spring" to allow Spring to manage the lifecycle of the mapper bean.
public interface ReviewMapper {
    ReviewCreateUpdateRequest toReviewCreateUpdateRequest(ReviewCreateUpdateRequestDto dto);
    ReviewDto toDto(Review review);
}
