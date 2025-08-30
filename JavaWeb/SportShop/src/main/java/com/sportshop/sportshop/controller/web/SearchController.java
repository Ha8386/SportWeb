package com.sportshop.sportshop.controller.web;

import java.util.ArrayList;
import java.util.List;

import com.sportshop.sportshop.enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sportshop.sportshop.dto.request.SearchRequest;
import com.sportshop.sportshop.dto.response.ProductResponse;
import com.sportshop.sportshop.entity.UserEntity;
import com.sportshop.sportshop.enums.SortEnum;
import com.sportshop.sportshop.service.BrandService;
import com.sportshop.sportshop.service.CartService;
import com.sportshop.sportshop.service.CategoryService;
import com.sportshop.sportshop.service.ProductService;
import com.sportshop.sportshop.utils.GetUserAuthentication;

@Controller
public class SearchController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CartService cartService;

    @Autowired
    private GetUserAuthentication getUserAuthentication;
    
    @GetMapping("/home/search")
    public ModelAndView searchProduct() {
        ModelAndView mav = new ModelAndView("/web/search")
                .addObject("newSearch", new SearchRequest())
                .addObject("products", productService.getAllProductsPaginated(0,8, "id", "asc",null, StatusEnum.Active))
                .addObject("categories", categoryService.getAllCategoriesPaginated(0,8, "id", "asc",null, StatusEnum.Active))
                .addObject("brands", brandService.getAllPaginated(0,8, "id", "asc",null, StatusEnum.Active))
                .addObject("sorts", SortEnum.values());
        
        UserEntity user = getUserAuthentication.getUser();
        if (user != null) {
            mav.addObject("user", user);
            mav.addObject("count", cartService.getCountByUserId(user.getId()));
        }

        return mav;
    }

    @PostMapping("/home/search")
    public ModelAndView search(@ModelAttribute("newSearch") SearchRequest request,
                            @RequestParam(defaultValue = "0") int page) {
        int size = 8;
        ModelAndView mav = new ModelAndView("/web/search");
        Page<ProductResponse> productPage = productService.searchProduct(request, page, size);

        mav.addObject("products", productPage.getContent());
        mav.addObject("productPage", productPage);
        mav.addObject("newSearch", request);
        mav.addObject("categories", categoryService.getAllCategoriesPaginated(0,8, "id", "asc",null, StatusEnum.Active));
        mav.addObject("brands", brandService.getAllPaginated(0,8, "id", "asc",null, StatusEnum.Active));
        mav.addObject("sorts", SortEnum.values());

        UserEntity user = getUserAuthentication.getUser();
        if (user != null) {
            mav.addObject("user", user);
            mav.addObject("count", cartService.getCountByUserId(user.getId()));
        }

        return mav;
    }
   
    @PostMapping("/home/search-name")
        public ModelAndView searchProductByName(@RequestParam(required = false) String name) {
            SearchRequest newSearch = new SearchRequest();
            newSearch.setName(name);
            newSearch.setTypeSort(SortEnum.NOT_SORT);

            Page<ProductResponse> productPage = productService.searchProduct(newSearch, 0, 8);
        
            ModelAndView mav = new ModelAndView("/web/search")
                    .addObject("newSearch", new SearchRequest())
                    .addObject("productPage", productPage)
                    .addObject("products", productPage.getContent())
                    .addObject("categories", categoryService.getAllCategoriesPaginated(0,8, "id", "asc",null, StatusEnum.Active))
                    .addObject("brands", brandService.getAllPaginated(0,8, "id", "asc",null, StatusEnum.Active))
                    .addObject("sorts", SortEnum.values());
            
            UserEntity user = getUserAuthentication.getUser();
            if (user != null) {
                mav.addObject("user", user);
                mav.addObject("count", cartService.getCountByUserId(user.getId()));
            }

            return mav;
        }

    @GetMapping("/home/search/{brandId}")
    public ModelAndView searchProduct(@PathVariable("brandId") String brandId) {
        SearchRequest newSearch = new SearchRequest();
        List<String> brands = new ArrayList<>();
        brands.add(brandId);
        newSearch.setBrands(brands);
        newSearch.setTypeSort(SortEnum.NOT_SORT);

        Page<ProductResponse> productPage = productService.searchProduct(newSearch, 0, 8);
       
        ModelAndView mav = new ModelAndView("/web/search")
                .addObject("newSearch", new SearchRequest())
                .addObject("productPage", productPage)
                .addObject("products", productPage.getContent())
                .addObject("categories", categoryService.getAllCategoriesPaginated(0,8, "id", "asc",null, StatusEnum.Active))
                .addObject("brands", brandService.getAllPaginated(0,8, "id", "asc",null, StatusEnum.Active))
                .addObject("sorts", SortEnum.values());
        
        UserEntity user = getUserAuthentication.getUser();
        if (user != null) {
            mav.addObject("user", user);
            mav.addObject("count", cartService.getCountByUserId(user.getId()));
        }

        return mav;
    }
}
