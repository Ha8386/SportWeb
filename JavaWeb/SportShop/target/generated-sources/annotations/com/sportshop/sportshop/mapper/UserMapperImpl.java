package com.sportshop.sportshop.mapper;

import com.sportshop.sportshop.dto.request.CreateUserRequest;
import com.sportshop.sportshop.dto.request.UpdateUserRequest;
import com.sportshop.sportshop.dto.response.UserResponse;
import com.sportshop.sportshop.entity.AddressEntity;
import com.sportshop.sportshop.entity.CartEntity;
import com.sportshop.sportshop.entity.UserEntity;
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
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toUserResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( userEntity.getId() );
        userResponse.username( userEntity.getUsername() );
        userResponse.password( userEntity.getPassword() );
        userResponse.fullName( userEntity.getFullName() );
        userResponse.gender( userEntity.getGender() );
        userResponse.phone( userEntity.getPhone() );
        userResponse.email( userEntity.getEmail() );
        userResponse.avatar( userEntity.getAvatar() );
        userResponse.createDate( userEntity.getCreateDate() );
        userResponse.updateDate( userEntity.getUpdateDate() );
        if ( userEntity.getRoles() != null ) {
            userResponse.roles( userEntity.getRoles().name() );
        }
        userResponse.status( userEntity.getStatus() );
        List<CartEntity> list = userEntity.getCarts();
        if ( list != null ) {
            userResponse.carts( new ArrayList<CartEntity>( list ) );
        }
        List<AddressEntity> list1 = userEntity.getAddresses();
        if ( list1 != null ) {
            userResponse.addresses( new ArrayList<AddressEntity>( list1 ) );
        }

        return userResponse.build();
    }

    @Override
    public UserEntity toUserEntity(CreateUserRequest newUser) {
        if ( newUser == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.username( newUser.getUsername() );
        userEntity.password( newUser.getPassword() );
        userEntity.fullName( newUser.getFullName() );
        userEntity.gender( newUser.getGender() );
        userEntity.phone( newUser.getPhone() );
        userEntity.email( newUser.getEmail() );
        userEntity.avatar( newUser.getAvatar() );

        return userEntity.build();
    }

    @Override
    public void updateUserEntity(UserEntity userEntity, UpdateUserRequest updateUserRequest) {
        if ( updateUserRequest == null ) {
            return;
        }

        userEntity.setPassword( updateUserRequest.getPassword() );
        userEntity.setFullName( updateUserRequest.getFullName() );
        userEntity.setGender( updateUserRequest.getGender() );
        userEntity.setPhone( updateUserRequest.getPhone() );
        userEntity.setEmail( updateUserRequest.getEmail() );
        userEntity.setAvatar( updateUserRequest.getAvatar() );
    }
}
