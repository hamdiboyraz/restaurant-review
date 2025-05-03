package com.bh.restaurant.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperatingHours {

    // Nested -> Elasticsearch to treat each TimeRange as a nested object, maintaining the relationship between opening and closing times.
    // This allows us to query operating hours as complete units rather than independent fields.
    @Field(type = FieldType.Nested)
    private TimeRange monday; // TimeRange for Monday

    @Field(type = FieldType.Nested)
    private TimeRange tuesday; // TimeRange for Tuesday

    @Field(type = FieldType.Nested)
    private TimeRange wednesday; // TimeRange for Wednesday

    @Field(type = FieldType.Nested)
    private TimeRange thursday; // TimeRange for Thursday

    @Field(type = FieldType.Nested)
    private TimeRange friday; // TimeRange for Friday

    @Field(type = FieldType.Nested)
    private TimeRange saturday; // TimeRange for Saturday

    @Field(type = FieldType.Nested)
    private TimeRange sunday; // TimeRange for Sunday
}
