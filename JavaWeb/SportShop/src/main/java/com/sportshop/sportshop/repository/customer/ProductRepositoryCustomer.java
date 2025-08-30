package com.sportshop.sportshop.repository.customer; 

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sportshop.sportshop.dto.request.SearchRequest;
import com.sportshop.sportshop.entity.ProductEntity;

@Repository
public interface ProductRepositoryCustomer {
    Page<ProductEntity> search(SearchRequest request, Pageable pageable);
}
