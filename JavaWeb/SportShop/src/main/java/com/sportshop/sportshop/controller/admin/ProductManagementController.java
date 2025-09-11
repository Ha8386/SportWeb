package com.sportshop.sportshop.controller.admin;

import com.sportshop.sportshop.dto.request.ImageRequest;
import com.sportshop.sportshop.dto.request.ProductRequest;
import com.sportshop.sportshop.dto.response.ProductResponse;
import com.sportshop.sportshop.enums.StatusEnum;
import com.sportshop.sportshop.service.*;
//import com.sportshop.sportshop.utils.upload.ExcelProductHelper;
//import com.sportshop.sportshop.utils.upload.ProductExcelService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin/product")
public class ProductManagementController {
    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final ImageService imageService;
//    private final ProductExcelService excelService;

    public ProductManagementController(ProductService productService, BrandService brandService, CategoryService categoryService, ImageService imageService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.imageService = imageService;
//        this.excelService = excelService;
    }

    // View all product
    @GetMapping
    public ModelAndView getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name

    ) {

        Page<ProductResponse> productPage = productService.getAllProductsPaginated(page, size, sortField, sortDir, name, StatusEnum.Active);
        Page<ProductResponse> productsDeletedPage = productService.getAllProductsPaginated(page, size, sortField, sortDir, name, StatusEnum.Closed);
        ModelAndView mav = new ModelAndView("/admin/product/management");

        mav.addObject("productPage", productPage);
        mav.addObject("products", productPage.getContent());
        mav.addObject("productDeletedPage", productsDeletedPage);
        mav.addObject("productsDeleted", productsDeletedPage.getContent());

        mav.addObject("sortField", sortField);
        mav.addObject("sortDir", sortDir);
        mav.addObject("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        mav.addObject("newProduct",  new ProductRequest());
        mav.addObject("categories", categoryService.getAllCategoriesPaginated(0,8, "id", "asc",null, StatusEnum.Active));
        mav.addObject("brands", brandService.getAllPaginated(0,8, "id", "asc",null, StatusEnum.Active));

        return mav;
    }

    // View product by ID
    @GetMapping("/{productId}")
    public ModelAndView getProduct(@PathVariable Long productId){
        ProductResponse product = productService.getProductById(productId);
        return new ModelAndView("/admin/product/view")
                .addObject("product", product)
                .addObject("images", imageService.getImageByProductId(productId))
                .addObject("brand", product.getBrand())
                .addObject("category", product.getCategory())
                .addObject("newImage", new ImageRequest());
    }

    @PostMapping("/create")
    public ModelAndView createProduct(@ModelAttribute("newProduct") ProductRequest request,
                                @RequestParam("files") List<MultipartFile> files
                                ,Model model){
        try {
            ProductResponse product = productService.createProduct(request);
            imageService.createImage(files, product.getId());
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @GetMapping("/update/{productId}")
    public ModelAndView updateProduct(@PathVariable Long productId){
        return new ModelAndView("/admin/product/update")
                .addObject("product", productService.getProductById(productId))
                .addObject("updateProduct", new ProductRequest())
                .addObject("categories", categoryService.getAllCategoriesPaginated(0,8, "id", "asc",null, StatusEnum.Active))
                .addObject("brands", brandService.getAllPaginated(0,8, "id", "asc",null, StatusEnum.Active));
    }

    @PostMapping("/update/{productId}")
    public ModelAndView updateProduct(@PathVariable Long productId, @ModelAttribute("updateProduct") ProductRequest request, Model model){
        try{
            productService.updateProduct(productId, request);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PostMapping("/restore/{productId}")
    public ModelAndView restoreProduct(@PathVariable Long productId, Model model){
        try{
            productService.restoreProduct(productId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PutMapping("/delete/{productId}")
    public ModelAndView softDeleteProduct(@PathVariable Long productId, Model model){
        try{
            productService.softDeleteProduct(productId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @DeleteMapping("/delete/{productId}")
    public ModelAndView deleteProduct(@PathVariable Long productId, Model model){
        try{
            productService.deleteProduct(productId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PostMapping("/add-image/{productId}")
    public String addImage(@PathVariable Long productId,
                           @RequestParam("file") MultipartFile file){
        try {
            imageService.createImage(file, productId);
            return "redirect:/admin/product/" + productId;
        } catch (Exception e) {
            return "redirect:/admin/admin";
        }

    }

    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<Void> deleteImageProduct(@PathVariable Long imageId) {
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PostMapping("/add/upload")
//    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file) {
//        if (!ExcelProductHelper.hasExcelFormat(file)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng tải lên file Excel hợp lệ (.xlsx)");
//        }
//
//        try {
//            excelService.importProductsFromExcel(file);
//            return ResponseEntity.ok("Tải lên thành công!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Tải lên thất bại: " + e.getMessage());
//        }
//    }



}
