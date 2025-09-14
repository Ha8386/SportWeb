package com.sportshop.sportshop.mapper;

import com.sportshop.sportshop.dto.response.ImageResponse;
import com.sportshop.sportshop.entity.ImageEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-11T09:45:01+0700",
    comments = "version: 1.6.0, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class ImageMapperImpl implements ImageMapper {

    @Override
    public ImageResponse toImageResponse(ImageEntity imageEntity) {
        if ( imageEntity == null ) {
            return null;
        }

        ImageResponse.ImageResponseBuilder imageResponse = ImageResponse.builder();

        imageResponse.id( imageEntity.getId() );
        imageResponse.imageLink( imageEntity.getImageLink() );
        imageResponse.product( imageEntity.getProduct() );

        return imageResponse.build();
    }
}
