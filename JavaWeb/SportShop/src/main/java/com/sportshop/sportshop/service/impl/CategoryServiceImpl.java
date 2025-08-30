package com.sportshop.sportshop.service.impl;

import com.sportshop.sportshop.dto.request.CategoryRequest;
import com.sportshop.sportshop.dto.response.CategoryResponse;
import com.sportshop.sportshop.entity.CategoryEntity;
import com.sportshop.sportshop.entity.ProductEntity;
import com.sportshop.sportshop.enums.StatusEnum;
import com.sportshop.sportshop.exception.AppException;
import com.sportshop.sportshop.exception.ErrorCode;
import com.sportshop.sportshop.mapper.CategoryMapper;
import com.sportshop.sportshop.repository.CategoryRepository;
import com.sportshop.sportshop.repository.ProductRepository;
import com.sportshop.sportshop.service.CategoryService;
import com.sportshop.sportshop.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    ImageService imageService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public Page<CategoryResponse> getAllCategoriesPaginated(int page, int size, String sortField, String sortDir, String name, StatusEnum status) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CategoryEntity> entities;

        if (name != null && !name.trim().isEmpty()) {
            // Có name -> dùng method tìm theo name và status
            entities = categoryRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable);
        } else {
            entities = categoryRepository.findByStatus(status, pageable);
        }
        return entities.map(categoryMapper::toCategoryResponse);
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        return categoryMapper.toCategoryResponse(categoryRepository.findByIdAndStatus(categoryId, StatusEnum.Active));
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest,  MultipartFile file) {
        if(categoryRepository.existsByNameAndStatus(categoryRequest.getName(), StatusEnum.Active)){
            throw new AppException(ErrorCode.ENTITY_EXIST);
        }

        CategoryEntity newCategory = categoryMapper.toEntity(categoryRequest);
        newCategory.setStatus(StatusEnum.Active);
        newCategory.setImage(imageService.createImage(file));

        return categoryMapper.toCategoryResponse(categoryRepository.save(newCategory));
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest, MultipartFile file) {
        if(categoryRepository.existsByNameAndStatus(categoryRequest.getName(), StatusEnum.Active)){
            throw new AppException(ErrorCode.ENTITY_EXIST);
        }

        CategoryEntity updatedCategory = categoryRepository.findByIdAndStatus(categoryId, StatusEnum.Active);

        if(categoryRequest.getName() != null && !categoryRequest.getName().isEmpty()){
            updatedCategory.setName(categoryRequest.getName());
        }

        if(file != null && !file.isEmpty()){
            updatedCategory.setImage(imageService.createImage(file));
        }

        categoryRepository.save(updatedCategory);

        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    @Override
    public void softDeleteCategory(Long categoryId) {
        CategoryEntity category = categoryRepository.findByIdAndStatus(categoryId, StatusEnum.Active);

        category.setStatus(StatusEnum.Closed);

        categoryRepository.save(category);
    }

    @Override
    public void restoreCategory(Long categoryId) {
        CategoryEntity category = categoryRepository.findByIdAndStatus(categoryId, StatusEnum.Closed);

        category.setStatus(StatusEnum.Active);

        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        List<ProductEntity> productEntities = productRepository.findByCategoryId(categoryId)
                .stream()
                .peek(od -> od.setCategory(null))
                .toList();

        productRepository.saveAll(productEntities);

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public void updateImage(Long categoryId, String image) {
        CategoryEntity category = categoryRepository.findByIdAndStatus(categoryId, StatusEnum.Active);

        category.setImage(image);

        categoryRepository.save(category);
    }

    @Override
    public void deleteImage(Long categoryId) {
        CategoryEntity category = categoryRepository.findByIdAndStatus(categoryId, StatusEnum.Active);

        category.setImage(null);

        categoryRepository.save(category);
    }
}
