package com.sportshop.sportshop.service.impl;

import com.sportshop.sportshop.dto.request.BrandRequest;
import com.sportshop.sportshop.dto.response.BrandResponse;
import com.sportshop.sportshop.entity.BrandEntity;
import com.sportshop.sportshop.entity.ProductEntity;
import com.sportshop.sportshop.enums.StatusEnum;
import com.sportshop.sportshop.exception.AppException;
import com.sportshop.sportshop.exception.ErrorCode;
import com.sportshop.sportshop.mapper.BrandMapper;
import com.sportshop.sportshop.repository.BrandRepository;
import com.sportshop.sportshop.repository.ProductRepository;
import com.sportshop.sportshop.service.BrandService;
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
public class BrandServiceImpl implements BrandService {
    @Autowired
    ImageService imageService;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BrandMapper brandMapper;

    @Override
    public Page<BrandResponse> getAllPaginated(int page, int size, String sortField, String sortDir, String name, StatusEnum status) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BrandEntity> entities;

        if (name != null && !name.trim().isEmpty()) {
            // Có name -> dùng method tìm theo name và status
            entities = brandRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable);
        } else {
            entities = brandRepository.findByStatus(status, pageable);
        }
        return entities.map(brandMapper::toBrandResponse);
    }

    @Override
    public BrandResponse getBrandById(Long brandId) {
        return brandMapper.toBrandResponse(brandRepository.findByIdAndStatus(brandId, StatusEnum.Active));
    }

    @Override
    public BrandResponse createBrand(BrandRequest brandRequest, MultipartFile file) {
        if(brandRepository.existsByNameAndStatus(brandRequest.getName(), StatusEnum.Active)){
            throw new AppException(ErrorCode.ENTITY_EXIST);
        }

        BrandEntity newBrand = brandMapper.toEntity(brandRequest);
        newBrand.setStatus(StatusEnum.Active);
        newBrand.setImage(imageService.createImage(file));

        return brandMapper.toBrandResponse(brandRepository.save(newBrand));
    }

    @Override
    public BrandResponse updateBrand(Long brandId, BrandRequest brandRequest, MultipartFile file) {
        if(brandRepository.existsByNameAndStatus(brandRequest.getName(), StatusEnum.Active)){
            throw new AppException(ErrorCode.ENTITY_EXIST);
        }

        BrandEntity updatedBrand = brandRepository.findByIdAndStatus(brandId, StatusEnum.Active);

        if(brandRequest.getName() != null && !brandRequest.getName().isEmpty()){
            updatedBrand.setName(brandRequest.getName());
        }

        if(file != null && !file.isEmpty()){
            updatedBrand.setImage(imageService.createImage(file));
        }

        brandRepository.save(updatedBrand);

        return brandMapper.toBrandResponse(updatedBrand);
    }

    @Override
    public void softDeleteBrand(Long brandId) {
        BrandEntity brand = brandRepository.findByIdAndStatus(brandId, StatusEnum.Active);

        brand.setStatus(StatusEnum.Closed);

        brandRepository.save(brand);
    }

    @Override
    public void restoreBrand(Long brandId) {
        BrandEntity brand = brandRepository.findByIdAndStatus(brandId, StatusEnum.Closed);

        brand.setStatus(StatusEnum.Active);

        brandRepository.save(brand);
    }

    @Override
    public void deleteBrand(Long brandId) {
        List<ProductEntity> productEntities = productRepository.findByBrandId(brandId)
                .stream()
                .peek(od -> od.setBrand(null))
                .toList();

        productRepository.saveAll(productEntities);

        brandRepository.deleteById(brandId);
    }

    @Override
    public void updateImage(Long brandId, String image) {
        BrandEntity brand = brandRepository.findByIdAndStatus(brandId, StatusEnum.Active);

        brand.setImage(image);

        brandRepository.save(brand);
    }

    @Override
    public void deleteImage(Long brandId) {
        BrandEntity brand = brandRepository.findByIdAndStatus(brandId, StatusEnum.Active);

        brand.setImage(null);

        brandRepository.save(brand);
    }
}
