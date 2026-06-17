package com.appshopbanhang.admin.controller.api;

import com.appshopbanhang.admin.dto.AccountRequest;
import com.appshopbanhang.admin.dto.AccountResponse;
import com.appshopbanhang.admin.dto.CategoryResponse;
import com.appshopbanhang.admin.dto.OrderResponse;
import com.appshopbanhang.admin.dto.OrderStatusRequest;
import com.appshopbanhang.admin.dto.ProductResponse;
import com.appshopbanhang.admin.entity.TaiKhoan;
import com.appshopbanhang.admin.repository.TaiKhoanRepository;
import com.appshopbanhang.admin.service.CatalogService;
import com.appshopbanhang.admin.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    private final CatalogService catalogService;
    private final OrderService orderService;
    private final TaiKhoanRepository accountRepository;

    public AdminApiController(
            CatalogService catalogService,
            OrderService orderService,
            TaiKhoanRepository accountRepository
    ) {
        this.catalogService = catalogService;
        this.orderService = orderService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/products")
    public List<ProductResponse> products() {
        return catalogService.searchProducts(null, null);
    }

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse createProduct(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String note,
            @RequestParam Integer stock,
            @RequestParam(required = false) Integer categoryId,
            @RequestPart(required = false) MultipartFile image
    ) throws IOException {
        return catalogService.toProductResponse(
                catalogService.saveProduct(null, name, price, description, note, stock, categoryId, image)
        );
    }

    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse updateProduct(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String note,
            @RequestParam Integer stock,
            @RequestParam(required = false) Integer categoryId,
            @RequestPart(required = false) MultipartFile image
    ) throws IOException {
        return catalogService.toProductResponse(
                catalogService.saveProduct(id, name, price, description, note, stock, categoryId, image)
        );
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        catalogService.deleteProduct(id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return catalogService.getCategories();
    }

    @PostMapping(value = "/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryResponse createCategory(
            @RequestParam String name,
            @RequestPart(required = false) MultipartFile image
    ) throws IOException {
        return toCategoryResponse(catalogService.saveCategory(null, name, image));
    }

    @PutMapping(value = "/categories/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryResponse updateCategory(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestPart(required = false) MultipartFile image
    ) throws IOException {
        return toCategoryResponse(catalogService.saveCategory(id, name, image));
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Integer id) {
        catalogService.deleteCategory(id);
    }

    @GetMapping("/accounts")
    public List<AccountResponse> accounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::toAccountResponse)
                .toList();
    }

    @PostMapping("/accounts")
    public AccountResponse createAccount(@Valid @RequestBody AccountRequest request) {
        return saveAccount(request);
    }

    @PutMapping("/accounts/{username}")
    public AccountResponse updateAccount(
            @PathVariable String username,
            @Valid @RequestBody AccountRequest request
    ) {
        AccountRequest normalizedRequest = new AccountRequest(username, request.password(), request.role());
        return saveAccount(normalizedRequest);
    }

    @DeleteMapping("/accounts/{username}")
    public void deleteAccount(@PathVariable String username) {
        accountRepository.deleteById(username);
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders() {
        return orderService.getOrderEntities()
                .stream()
                .map(orderService::toOrderResponse)
                .toList();
    }

    @PutMapping("/orders/{id}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Integer id,
            @Valid @RequestBody OrderStatusRequest request
    ) {
        orderService.updateStatus(id, request.status());
        return orderService.toOrderResponse(orderService.getOrderEntity(id));
    }

    private AccountResponse saveAccount(AccountRequest request) {
        TaiKhoan account = new TaiKhoan(
                request.username().trim(),
                request.password(),
                request.role().trim()
        );
        return toAccountResponse(accountRepository.save(account));
    }

    private AccountResponse toAccountResponse(TaiKhoan account) {
        return new AccountResponse(account.getUsername(), account.getPassword(), account.getRole());
    }

    private CategoryResponse toCategoryResponse(com.appshopbanhang.admin.entity.NhomSanPham category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getImageUrl());
    }
}
