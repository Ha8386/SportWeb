//package com.sportshop.sportshop.utils.upload;
//
//import com.sportshop.sportshop.entity.BrandEntity;
//import com.sportshop.sportshop.entity.CategoryEntity;
//import com.sportshop.sportshop.entity.ProductEntity;
//import com.sportshop.sportshop.repository.BrandRepository;
//import com.sportshop.sportshop.repository.CategoryRepository;
//import com.sportshop.sportshop.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class ProductExcelService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private BrandRepository brandRepository;
//
//    public void importProductsFromExcel(MultipartFile file) {
//        try {
//            List<ProductEntity> products = ExcelProductHelper.excelToProducts(file.getInputStream());
//
//            for (ProductEntity product : products) {
//                // Xử lý danh mục
//                CategoryEntity category = categoryRepository.findByName(product.getCategory().getName())
//                        .orElseGet(() -> {
//                            CategoryEntity newCategory = new CategoryEntity();
//                            newCategory.setName(product.getCategory().getName());
//                            return categoryRepository.save(newCategory);
//                        });
//                product.setCategory(category);
//
//                // Xử lý thương hiệu
//                BrandEntity brand = brandRepository.findByName(product.getBrand().getName())
//                        .orElseGet(() -> {
//                            BrandEntity newBrand = new BrandEntity();
//                            newBrand.setName(product.getBrand().getName());
//                            return brandRepository.save(newBrand);
//                        });
//                product.setBrand(brand);
//            }
//
//            productRepository.saveAll(products);
//        } catch (IOException e) {
//            throw new RuntimeException("Lỗi khi lưu dữ liệu từ Excel: " + e.getMessage());
//        }
//    }
//}
