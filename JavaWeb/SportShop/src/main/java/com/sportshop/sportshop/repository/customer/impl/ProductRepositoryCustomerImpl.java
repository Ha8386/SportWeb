package com.sportshop.sportshop.repository.customer.impl;

import com.sportshop.sportshop.dto.request.SearchRequest;
import com.sportshop.sportshop.entity.ProductEntity;
import com.sportshop.sportshop.repository.customer.ProductRepositoryCustomer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryCustomerImpl implements ProductRepositoryCustomer {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ProductEntity> search(SearchRequest request, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE status_product = 'Active' ");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM product WHERE status_product = 'Active' ");

        if (request.getName() != null && !request.getName().isBlank()) {
            sql.append(" AND name LIKE '%").append(request.getName()).append("%'");
            countSql.append(" AND name LIKE '%").append(request.getName()).append("%'");
        }

        if(request.getMinPrice() != null){
            sql.append(" AND price >= ").append(request.getMinPrice());
            countSql.append(" AND price >= ").append(request.getMinPrice());
        }

        if(request.getMaxPrice() != null){
            sql.append(" AND price <= ").append(request.getMaxPrice());
            countSql.append(" AND price <= ").append(request.getMaxPrice());
        }

        if(request.getCategories() != null && !request.getCategories().isEmpty()){
            String categories = request.getCategories().stream()
                    .map(c -> "'" + c + "'").collect(Collectors.joining(","));
            sql.append(" AND category_id IN (").append(categories).append(")");
            countSql.append(" AND category_id IN (").append(categories).append(")");
        }

        if(request.getBrands() != null && !request.getBrands().isEmpty()){
            String brands = request.getBrands().stream()
                    .map(b -> "'" + b + "'").collect(Collectors.joining(","));
            sql.append(" AND brand_id IN (").append(brands).append(")");
            countSql.append(" AND brand_id IN (").append(brands).append(")");
        }

        switch (request.getTypeSort()) {
            case DECS_PRICE -> sql.append(" ORDER BY price DESC ");
            case ORDER_PRICE -> sql.append(" ORDER BY price ASC ");
            case NEW -> sql.append(" ORDER BY create_date DESC ");
            case SALE -> sql.append(" ORDER BY discount DESC ");
        }

        // Pagination
        sql.append(" LIMIT ").append(pageable.getPageSize()).append(" OFFSET ").append(pageable.getOffset());

        List<ProductEntity> products = entityManager.createNativeQuery(sql.toString(), ProductEntity.class).getResultList();

        Long total = ((Number) entityManager.createNativeQuery(countSql.toString()).getSingleResult()).longValue();

        return new PageImpl<>(products, pageable, total);
    }

}
