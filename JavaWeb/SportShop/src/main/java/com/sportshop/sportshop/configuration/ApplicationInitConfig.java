package com.sportshop.sportshop.configuration;

import com.sportshop.sportshop.entity.BrandEntity;
import com.sportshop.sportshop.entity.CategoryEntity;
import com.sportshop.sportshop.entity.UserEntity;
import com.sportshop.sportshop.enums.RoleEnum;
import com.sportshop.sportshop.enums.StatusEnum;
import com.sportshop.sportshop.repository.BrandRepository;
import com.sportshop.sportshop.repository.CategoryRepository;
import com.sportshop.sportshop.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationInitConfig {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, 
                                        BrandRepository brandRepository,
                                        CategoryRepository categoryRepository) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {

                UserEntity user = UserEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(RoleEnum.ADMIN)
                        .status(StatusEnum.Active)
                        .build();
                userRepository.save(user);
            }

            if(brandRepository.count() == 0){
                List<BrandEntity> brands = new ArrayList<>();

                brands.add(BrandEntity.builder()
                        .name("Nike")
                        .image("https://cdn.shopify.com/s/files/1/0456/5070/6581/files/ALLBRANDS_NIKE.jpg?v=1715164070")
                        .build());
                
                brands.add(BrandEntity.builder()
                        .name("Adidas")
                        .image("https://cdn.shopify.com/s/files/1/0456/5070/6581/files/ALLBRANDS_DAS.jpg?v=1715164070")
                        .build());

                brands.add(BrandEntity.builder()
                        .name("Hoka")
                        .image("https://cdn.shopify.com/s/files/1/0456/5070/6581/files/HOKA_30296b60-fa58-4266-87ef-6d6916626d47.jpg?v=1715164070")
                        .build());

                brands.add(BrandEntity.builder()
                        .name("Fila")
                        .image("https://cdn.shopify.com/s/files/1/0456/5070/6581/files/ALLBRANDS_FILA.jpg?v=1715164070")
                        .build());
        
                brandRepository.saveAll(brands);
            }

            if(categoryRepository.count() == 0){
                List<CategoryEntity> categories = new ArrayList<>();

                categories.add(new CategoryEntity("QUAN_AO"));
                categories.add(new CategoryEntity("GIAY_DEP"));
                categories.add(new CategoryEntity("BALO"));
                categories.add(new CategoryEntity("TUI"));
        
                categoryRepository.saveAll(categories);
            }
        };
    }
}

