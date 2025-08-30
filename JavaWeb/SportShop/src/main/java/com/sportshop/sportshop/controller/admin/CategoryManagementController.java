package com.sportshop.sportshop.controller.admin;


import com.sportshop.sportshop.dto.request.CategoryRequest;
import com.sportshop.sportshop.dto.response.CategoryResponse;
import com.sportshop.sportshop.enums.StatusEnum;
import com.sportshop.sportshop.service.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/admin/category")
public class CategoryManagementController {
    private final CategoryService categoryService;
    private final ImageService imageService;

    public CategoryManagementController(
            CategoryService categoryService, ImageService imageService
    ) {
        this.categoryService = categoryService;
        this.imageService = imageService;
    }

    // View all category
    @GetMapping
    public ModelAndView getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name

    ) {

        Page<CategoryResponse> categoryPage = categoryService.getAllCategoriesPaginated(page, size, sortField, sortDir, name, StatusEnum.Active);
        Page<CategoryResponse> categoriesDeletedPage = categoryService.getAllCategoriesPaginated(page, size, sortField, sortDir, name, StatusEnum.Closed);
        ModelAndView mav = new ModelAndView("/admin/category/management");

        mav.addObject("categoryPage", categoryPage);
        mav.addObject("categories", categoryPage.getContent());
        mav.addObject("categoryDeletedPage", categoriesDeletedPage);
        mav.addObject("categoriesDeleted", categoriesDeletedPage.getContent());

        mav.addObject("sortField", sortField);
        mav.addObject("sortDir", sortDir);
        mav.addObject("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        mav.addObject("newCategory",  new CategoryRequest());
        mav.addObject("updateCategory",  new CategoryRequest());

        return mav;
    }

    @PostMapping("/create")
    public ModelAndView createCategory(@ModelAttribute("newCategory") CategoryRequest request,
                                      @RequestParam("file") MultipartFile file
            ,Model model){
        try {
            CategoryResponse category = categoryService.createCategory(request, file);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PostMapping("/update/{categoryId}")
    public ModelAndView updateCategory(
            @PathVariable Long categoryId,
            @ModelAttribute("updateCategory") CategoryRequest request,
            @RequestParam("updateFile") MultipartFile file,
            Model model){
        try{
            categoryService.updateCategory(categoryId, request, file);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PostMapping("/restore/{categoryId}")
    public ModelAndView restoreCategory(@PathVariable Long categoryId, Model model){
        try{
            categoryService.restoreCategory(categoryId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PutMapping("/delete/{categoryId}")
    public ModelAndView softDeleteCategory(@PathVariable Long categoryId, Model model){
        try{
            categoryService.softDeleteCategory(categoryId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @DeleteMapping("/delete/{categoryId}")
    public ModelAndView deleteCategory(@PathVariable Long categoryId, Model model){
        try{
            categoryService.deleteCategory(categoryId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PostMapping("/add-image/{categoryId}")
    public String addImage(@PathVariable Long categoryId,
                           @RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            String imageLink = imageService.createImage(file);
            categoryService.updateImage(categoryId, imageLink);
            return "redirect:/admin/category/" + categoryId;
        }
        return "redirect:/admin/admin";
    }

    @DeleteMapping("/image/{categoryId}")
    public String deleteImageCategory(@PathVariable Long categoryId) {
        categoryService.deleteImage(categoryId);
        return "redirect:/admin/category/" + categoryId;
    }
}
