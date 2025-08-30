package com.sportshop.sportshop.service.impl;

import com.sportshop.sportshop.dto.request.ProductRequest;
import com.sportshop.sportshop.dto.request.SearchRequest;
import com.sportshop.sportshop.dto.response.ProductResponse;
import com.sportshop.sportshop.entity.BrandEntity;
import com.sportshop.sportshop.entity.CategoryEntity;
import com.sportshop.sportshop.entity.OrderDetailEntity;
import com.sportshop.sportshop.entity.ProductEntity;
import com.sportshop.sportshop.enums.StatusEnum;
import com.sportshop.sportshop.exception.AppException;
import com.sportshop.sportshop.exception.ErrorCode;
import com.sportshop.sportshop.mapper.ProductMapper;
import com.sportshop.sportshop.repository.BrandRepository;
import com.sportshop.sportshop.repository.CategoryRepository;
import com.sportshop.sportshop.repository.OrderDetailRepository;
import com.sportshop.sportshop.repository.ProductRepository;
import com.sportshop.sportshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, BrandRepository brandRepository, CategoryRepository categoryRepository, OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.orderDetailRepository = orderDetailRepository;
    }


    @Override
    public int countProduct(){
        return productRepository.findByStatus(StatusEnum.Active).size();
    }

    @Override
    public Page<ProductResponse> getAllProductsPaginated(
            int page, int size,
            String sortField, String sortDir,
            String name, StatusEnum status
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductEntity> entities;

        // Kiểm tra name có giá trị không
        if (name != null && !name.trim().isEmpty()) {
            // Có name -> dùng method tìm theo name và status
            entities = productRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable);
        } else {
            // Không có name -> chỉ tìm theo status
            entities = productRepository.findByStatus(status, pageable);
        }
        return entities.map(productMapper::toProductResponse);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return productMapper.toProductResponse(productRepository.findByIdAndStatus(productId, StatusEnum.Active));
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        if(productRepository.existsByNameAndStatus(productRequest.getName(), StatusEnum.Active)){
            throw new  AppException(ErrorCode.ENTITY_EXIST);
        }

        ProductEntity newProduct = productMapper.toProductEntity(productRequest);
        newProduct.setCreateDate(new Date());
        newProduct.setStatus(StatusEnum.Active);

        BrandEntity brand = brandRepository.findByIdAndStatus(productRequest.getBrandId(), StatusEnum.Active);
        newProduct.setBrand(brand);

        CategoryEntity category = categoryRepository.findByIdAndStatus(productRequest.getCategoryId(), StatusEnum.Active);
        newProduct.setCategory(category);

        return productMapper.toProductResponse(productRepository.save(newProduct));
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        if(productRepository.existsByNameAndStatus(request.getName(), StatusEnum.Active)){
            throw new  AppException(ErrorCode.ENTITY_EXIST);
        }

        ProductEntity updatedProduct = productRepository.findByIdAndStatus(productId, StatusEnum.Active);

        if(request.getName() != null && !request.getName().isEmpty()){
            updatedProduct.setName(request.getName());
        }
        if(request.getPrice() != null){
            updatedProduct.setPrice(request.getPrice());
        }
        if(request.getDiscount() != null){
            updatedProduct.setDiscount(request.getDiscount());
        }
        if(request.getColor() != null && !request.getColor().isEmpty()){
            updatedProduct.setColor(request.getColor());
        }
        if(request.getQuantity() != null){
            updatedProduct.setQuantity(request.getQuantity());
        }
        if(request.getDescription() != null && !request.getDescription().isEmpty()){
            updatedProduct.setDescription(request.getDescription());
        }

        updatedProduct.setUpdateDate(new Date());

        productRepository.save(updatedProduct);

        return productMapper.toProductResponse(updatedProduct);
    }

    @Override
    public void restoreProduct(Long productId) {
        ProductEntity product = productRepository.findByIdAndStatus(productId, StatusEnum.Closed);

        product.setStatus(StatusEnum.Active);

        productRepository.save(product);
    }

    @Override
    public void softDeleteProduct(Long productId) {
        ProductEntity product = productRepository.findByIdAndStatus(productId, StatusEnum.Active);
        
        product.setStatus(StatusEnum.Closed);

        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        List<OrderDetailEntity> orderDetails = orderDetailRepository.findByProductId(productId)
                .stream()
                .peek(od -> od.setProduct(null))
                .toList();

        orderDetailRepository.saveAll(orderDetails);

        productRepository.deleteById(productId);
    }

    @Override
    public Page<ProductResponse> searchProduct(SearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> entities = productRepository.search(request, pageable);
        return entities.map(productMapper::toProductResponse);
    }

    @Override
    public List<ProductResponse> getProductSale() {
        List<ProductResponse> products = productRepository.findByStatus(StatusEnum.Active).stream()
                .map(productMapper::toProductResponse)
                .sorted(Comparator.comparing(ProductResponse::getDiscount).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return products;
    }

    @Override
    public List<ProductResponse> getProductNewest() {
        List<ProductResponse> products = productRepository.findByStatus(StatusEnum.Active).stream()
                .map(productMapper::toProductResponse)
                .sorted(Comparator.comparing(ProductResponse::getCreateDate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return products;
    }

}
