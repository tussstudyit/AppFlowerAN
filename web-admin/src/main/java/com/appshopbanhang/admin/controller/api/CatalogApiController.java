package com.appshopbanhang.admin.controller.api;

import com.appshopbanhang.admin.dto.CategoryResponse;
import com.appshopbanhang.admin.dto.ProductResponse;
import com.appshopbanhang.admin.service.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogApiController {
    private final CatalogService catalogService;

    public CatalogApiController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return catalogService.getCategories();
    }

    @GetMapping("/products")
    public List<ProductResponse> products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId
    ) {
        return catalogService.searchProducts(keyword, categoryId);
    }

    @GetMapping("/products/{id}")
    public ProductResponse product(@PathVariable Integer id) {
        return catalogService.toProductResponse(catalogService.getProduct(id));
    }
}
