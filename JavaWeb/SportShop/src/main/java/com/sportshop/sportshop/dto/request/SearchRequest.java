package com.sportshop.sportshop.dto.request;

import java.util.List;

import com.sportshop.sportshop.enums.SortEnum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchRequest {
    String name;
    Long minPrice;
    Long maxPrice;
    Integer page;
    Integer size;
    List<String> brands;
    List<String> categories;
    SortEnum typeSort;
}
