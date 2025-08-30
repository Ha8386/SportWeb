package com.sportshop.sportshop.mapper;

import com.sportshop.sportshop.dto.request.ProductRequest;
import com.sportshop.sportshop.dto.response.ProductResponse;
import com.sportshop.sportshop.entity.ImageEntity;
import com.sportshop.sportshop.entity.ProductEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-30T13:38:42+0700",
    comments = "version: 1.6.0, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductResponse toProductResponse(ProductEntity productEntity) {
        if ( productEntity == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.id( productEntity.getId() );
        productResponse.name( productEntity.getName() );
        productResponse.price( productEntity.getPrice() );
        productResponse.discount( productEntity.getDiscount() );
        productResponse.description( productEntity.getDescription() );
        productResponse.color( productEntity.getColor() );
        productResponse.quantity( productEntity.getQuantity() );
        productResponse.createDate( productEntity.getCreateDate() );
        productResponse.updateDate( productEntity.getUpdateDate() );
        productResponse.category( productEntity.getCategory() );
        productResponse.brand( productEntity.getBrand() );
        List<ImageEntity> list = productEntity.getImages();
        if ( list != null ) {
            productResponse.images( new ArrayList<ImageEntity>( list ) );
        }

        return productResponse.build();
    }

    @Override
    public ProductEntity toProductEntity(ProductRequest productRequest) {
        if ( productRequest == null ) {
            return null;
        }

        ProductEntity.ProductEntityBuilder productEntity = ProductEntity.builder();

        productEntity.id( productRequest.getId() );
        productEntity.name( productRequest.getName() );
        productEntity.price( productRequest.getPrice() );
        productEntity.discount( productRequest.getDiscount() );
        productEntity.description( productRequest.getDescription() );
        productEntity.color( productRequest.getColor() );
        productEntity.quantity( productRequest.getQuantity() );
        productEntity.createDate( productRequest.getCreateDate() );
        productEntity.updateDate( productRequest.getUpdateDate() );

        return productEntity.build();
    }

    @Override
    public void updateProductEntity(ProductEntity productEntity, ProductRequest productRequest) {
        if ( productRequest == null ) {
            return;
        }

        productEntity.setId( productRequest.getId() );
        productEntity.setName( productRequest.getName() );
        productEntity.setPrice( productRequest.getPrice() );
        productEntity.setDiscount( productRequest.getDiscount() );
        productEntity.setDescription( productRequest.getDescription() );
        productEntity.setColor( productRequest.getColor() );
        productEntity.setQuantity( productRequest.getQuantity() );
        productEntity.setCreateDate( productRequest.getCreateDate() );
        productEntity.setUpdateDate( productRequest.getUpdateDate() );
    }
}
