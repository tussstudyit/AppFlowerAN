package com.example.appshopbanhang.ui.product;


import com.example.appshopbanhang.data.model.SanPham;
import com.example.appshopbanhang.data.repository.SanPhamRepository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DanhSachSanPhamViewModel extends AndroidViewModel {
    private final SanPhamRepository productRepository;
    private final MutableLiveData<ArrayList<SanPham>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DanhSachSanPhamViewModel(@NonNull Application application) {
        super(application);
        productRepository = new SanPhamRepository(application);
    }

    public LiveData<ArrayList<SanPham>> getProducts() {
        return products;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void loadAllProducts() {
        executor.execute(() -> load(() -> productRepository.getAllProducts(), "Khong co san pham"));
    }

    public void loadProductsByCategory(String categoryId) {
        executor.execute(() -> load(() -> productRepository.getProductsByCategory(categoryId), "Khong tim thay san pham nao trong nhom nay"));
    }

    public void searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            message.setValue("Vui long nhap ten san pham");
            products.setValue(new ArrayList<>());
            return;
        }

        executor.execute(() -> load(() -> productRepository.searchProductsByName(keyword), "Khong tim thay san pham"));
    }

    private void load(ProductLoader loader, String emptyMessage) {
        try {
            ArrayList<SanPham> result = loader.load();
            products.postValue(result);
            if (result.isEmpty()) {
                message.postValue(emptyMessage);
            }
        } catch (Exception exception) {
            products.postValue(new ArrayList<>());
            message.postValue("Khong ket noi duoc backend");
        }
    }

    private interface ProductLoader {
        ArrayList<SanPham> load() throws Exception;
    }
}

