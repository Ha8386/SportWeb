package com.sportshop.sportshop.mapper;

import com.sportshop.sportshop.dto.request.BrandRequest;
import com.sportshop.sportshop.dto.response.BrandResponse;
import com.sportshop.sportshop.entity.BrandEntity;
import com.sportshop.sportshop.entity.ProductEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-11T09:45:01+0700",
    comments = "version: 1.6.0, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class BrandMapperImpl implements BrandMapper {

    @Override
    public BrandResponse toBrandResponse(BrandEntity brandEntity) {
        if ( brandEntity == null ) {
            return null;
        }

        BrandResponse.BrandResponseBuilder brandResponse = BrandResponse.builder();

        brandResponse.id( brandEntity.getId() );
        brandResponse.name( brandEntity.getName() );
        brandResponse.image( brandEntity.getImage() );
        List<ProductEntity> list = brandEntity.getProducts();
        if ( list != null ) {
            brandResponse.products( new ArrayList<ProductEntity>( list ) );
        }

        return brandResponse.build();
    }

    @Override
    public BrandEntity toEntity(BrandRequest brandRequest) {
        if ( brandRequest == null ) {
            return null;
        }

        BrandEntity.BrandEntityBuilder brandEntity = BrandEntity.builder();

        brandEntity.name( brandRequest.getName() );
        brandEntity.image( brandRequest.getImage() );

        return brandEntity.build();
    }
}
