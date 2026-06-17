package com.appshopbanhang.admin.service;

import com.appshopbanhang.admin.dto.CategoryResponse;
import com.appshopbanhang.admin.dto.ProductResponse;
import com.appshopbanhang.admin.entity.NhomSanPham;
import com.appshopbanhang.admin.entity.SanPham;
import com.appshopbanhang.admin.repository.NhomSanPhamRepository;
import com.appshopbanhang.admin.repository.SanPhamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CatalogService {
    private final NhomSanPhamRepository categoryRepository;
    private final SanPhamRepository productRepository;
    private final ImageStorageService imageStorageService;

    public CatalogService(
            NhomSanPhamRepository categoryRepository,
            SanPhamRepository productRepository,
            ImageStorageService imageStorageService
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.imageStorageService = imageStorageService;
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    public List<NhomSanPham> getCategoryEntities() {
        return categoryRepository.findAllByOrderByIdAsc();
    }

    public NhomSanPham getCategory(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Khong tim thay danh muc"));
    }

    public String getCategoryImageUrl(Integer id) {
        return getCategory(id).getImageUrl();
    }

    @Transactional
    public NhomSanPham saveCategory(Integer id, String name, MultipartFile image) throws IOException {
        NhomSanPham category = id == null ? new NhomSanPham() : getCategory(id);
        category.setName(name);
        if (image != null && !image.isEmpty()) {
            category.setImageUrl(imageStorageService.save("categories", image));
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }

    public List<ProductResponse> searchProducts(String keyword, Integer categoryId) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        boolean hasKeyword = StringUtils.hasText(normalizedKeyword);
        boolean hasCategory = categoryId != null;

        List<SanPham> products;
        if (hasCategory && hasKeyword) {
            products = productRepository.findByCategoryAndNameContainingIgnoreCaseOrderByIdDesc(getCategory(categoryId), normalizedKeyword);
        } else if (hasCategory) {
            products = productRepository.findByCategoryOrderByIdDesc(getCategory(categoryId));
        } else if (hasKeyword) {
            products = productRepository.findByNameContainingIgnoreCaseOrderByIdDesc(normalizedKeyword);
        } else {
            products = productRepository.findAllByOrderByIdDesc();
        }

        return products.stream().map(this::toProductResponse).toList();
    }

    public List<SanPham> getProductEntities() {
        return productRepository.findAllByOrderByIdDesc();
    }

    public SanPham getProduct(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Khong tim thay san pham"));
    }

    public String getProductImageUrl(Integer id) {
        return getProduct(id).getImageUrl();
    }

    @Transactional
    public SanPham saveProduct(
            Integer id,
            String name,
            BigDecimal price,
            String description,
            String note,
            Integer stock,
            Integer categoryId,
            MultipartFile image
    ) throws IOException {
        SanPham product = id == null ? new SanPham() : getProduct(id);
        product.setName(name);
        product.setPrice(normalizeVndPrice(price));
        product.setDescription(description);
        product.setNote(note);
        product.setStock(stock);
        product.setCategory(categoryId == null ? null : getCategory(categoryId));
        if (image != null && !image.isEmpty()) {
            product.setImageUrl(imageStorageService.save("products", image));
        }
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public ProductResponse toProductResponse(SanPham product) {
        NhomSanPham category = product.getCategory();
        Integer categoryId = category == null ? null : category.getId();
        String categoryName = category == null ? null : category.getName();
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getNote(),
                product.getStock(),
                categoryId,
                categoryName,
                product.getImageUrl()
        );
    }

    private CategoryResponse toCategoryResponse(NhomSanPham category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getImageUrl()
        );
    }

    private BigDecimal normalizeVndPrice(BigDecimal price) {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal normalized = price.setScale(0, RoundingMode.HALF_UP);
        if (normalized.compareTo(BigDecimal.ZERO) > 0 && normalized.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return normalized.multiply(BigDecimal.valueOf(1000));
        }
        return normalized;
    }
}
