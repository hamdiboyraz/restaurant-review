package com.bh.restaurant.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateUpdateRequest {
    private String content; // The text content of the review
    private Integer rating; // Rating from 1-5
    private List<String> photoIds;// References to uploaded photos
}