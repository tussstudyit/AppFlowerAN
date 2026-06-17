package com.appshopbanhang.admin.controller.api;

import com.appshopbanhang.admin.entity.ChiTietDonHang;
import com.appshopbanhang.admin.repository.ChiTietDonHangRepository;
import com.appshopbanhang.admin.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class ImageApiController {
    private final CatalogService catalogService;
    private final ChiTietDonHangRepository orderItemRepository;

    public ImageApiController(CatalogService catalogService, ChiTietDonHangRepository orderItemRepository) {
        this.catalogService = catalogService;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/categories/{id}/image")
    public ResponseEntity<Void> categoryImage(@PathVariable Integer id) {
        return redirect(catalogService.getCategoryImageUrl(id));
    }

    @GetMapping("/products/{id}/image")
    public ResponseEntity<Void> productImage(@PathVariable Integer id) {
        return redirect(catalogService.getProductImageUrl(id));
    }

    @GetMapping("/order-items/{id}/image")
    public ResponseEntity<Void> orderItemImage(@PathVariable Integer id) {
        return orderItemRepository.findById(id)
                .map(ChiTietDonHang::getImageUrl)
                .map(this::redirect)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Void> redirect(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(imageUrl))
                .build();
    }
}
