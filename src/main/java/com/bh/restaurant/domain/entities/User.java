package com.bh.restaurant.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data // Generates getters, setters, toString, equals, and hashCode
@AllArgsConstructor // Generates a constructor with all arguments
@NoArgsConstructor // Generates a no-args constructor
@Builder // Enables the builder pattern
public class User {

    // The @Field annotation is used to specify the field type in Elasticsearch
    // FieldType.Keyword for the id field - this type is used for exact matches and aggregations
    @Field(type = FieldType.Keyword)
    private String id;

    // FieldType.Text for name fields - this type is better for full-text search and partial matches
    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String givenName;

    @Field(type = FieldType.Text)
    private String familyName;
}
