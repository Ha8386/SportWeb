package com.sportshop.sportshop.controller.admin;

import com.sportshop.sportshop.dto.request.BrandRequest;
import com.sportshop.sportshop.dto.response.BrandResponse;
import com.sportshop.sportshop.entity.BrandEntity;
import com.sportshop.sportshop.enums.StatusEnum;
import com.sportshop.sportshop.service.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/brand")
public class BrandManagementController {
    private final BrandService brandService;
    private final ImageService imageService;

    public BrandManagementController(
           BrandService brandService,
           ImageService imageService
    ) {
        this.brandService = brandService;
        this.imageService = imageService;
    }

    // View all brand
    @GetMapping
    public ModelAndView getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name

    ) {

        Page<BrandResponse> brandPage = brandService.getAllPaginated(page, size, sortField, sortDir, name, StatusEnum.Active);
        Page<BrandResponse> brandsDeletedPage = brandService.getAllPaginated(page, size, sortField, sortDir, name, StatusEnum.Closed);
        ModelAndView mav = new ModelAndView("/admin/brand/management");

        mav.addObject("brandPage", brandPage);
        mav.addObject("brands", brandPage.getContent());
        mav.addObject("brandDeletedPage", brandsDeletedPage);
        mav.addObject("brandsDeleted", brandsDeletedPage.getContent());

        mav.addObject("sortField", sortField);
        mav.addObject("sortDir", sortDir);
        mav.addObject("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        mav.addObject("newBrand",  new BrandEntity());
        mav.addObject("updateBrand",  new BrandEntity());

        return mav;
    }

    @PostMapping("/create")
    public ModelAndView createBrand(
            @ModelAttribute("newBrand") BrandRequest request,
            @RequestParam("file") MultipartFile file
            ,Model model
    ){
        try {
            BrandResponse brand = brandService.createBrand(request, file);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PostMapping("/update/{brandId}")
    public ModelAndView updateBrand(
            @PathVariable Long brandId,
            @ModelAttribute("updateBrand") BrandRequest request,
            @RequestParam("updateFile") MultipartFile file,
            Model model
    ){
        try{
            brandService.updateBrand(brandId, request, file);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PostMapping("/restore/{brandId}")
    public ModelAndView restoreBrand(@PathVariable Long brandId, Model model){
        try{
            brandService.restoreBrand(brandId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PutMapping("/delete/{brandId}")
    public ModelAndView softDeleteBrand(@PathVariable Long brandId, Model model){
        try{
            brandService.softDeleteBrand(brandId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @DeleteMapping("/delete/{brandId}")
    public ModelAndView deleteBrand(@PathVariable Long brandId, Model model){
        try{
            brandService.deleteBrand(brandId);
            model.addAttribute("notification", "Success");
        } catch (Exception e) {
            model.addAttribute("notification", "Fail");
            model.addAttribute("message", e.getMessage());
        }
        return new ModelAndView("/admin/notification");
    }

    @PostMapping("/add-image/{brandId}")
    public String addImage(@PathVariable Long brandId,
                           @RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            String imageLink = imageService.createImage(file);
            brandService.updateImage(brandId, imageLink);
            return "redirect:/admin/brand/" + brandId;
        }
        return "redirect:/admin/admin";
    }

    @DeleteMapping("/image/{brandId}")
    public String deleteImageBrand(@PathVariable Long brandId) {
        brandService.deleteImage(brandId);
        return "redirect:/admin/brand/" + brandId;
    }
}
