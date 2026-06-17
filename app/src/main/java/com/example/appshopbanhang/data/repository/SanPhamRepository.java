package com.example.appshopbanhang.data.repository;

import android.content.Context;

import com.example.appshopbanhang.core.network.ApiConfig;
import com.example.appshopbanhang.core.network.ApiClient;
import com.example.appshopbanhang.data.model.NhomSanPham;
import com.example.appshopbanhang.data.model.SanPham;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SanPhamRepository {
    private final ApiClient apiClient = new ApiClient();

    public SanPhamRepository(Context context) {
        ApiConfig.load(context);
    }

    public ArrayList<SanPham> getAllProducts() throws Exception {
        return mapProducts(apiClient.getArray("/api/products"));
    }

    public ArrayList<SanPham> searchProductsByName(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put("keyword", keyword.trim());
        String query = ApiClient.query(params);
        return mapProducts(apiClient.getArray("/api/products?" + query));
    }

    public ArrayList<SanPham> getProductsByCategory(String categoryId) throws Exception {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put("categoryId", categoryId.trim());
        String query = ApiClient.query(params);
        return mapProducts(apiClient.getArray("/api/products?" + query));
    }

    public ArrayList<NhomSanPham> getAllCategories() throws Exception {
        return mapCategories(apiClient.getArray("/api/categories"));
    }

    public ArrayList<NhomSanPham> getRandomCategories(int limit) throws Exception {
        ArrayList<NhomSanPham> categories = getAllCategories();
        Collections.shuffle(categories);
        if (limit > 0 && categories.size() > limit) {
            return new ArrayList<>(categories.subList(0, limit));
        }
        return categories;
    }

    public SanPham saveProduct(
            String id,
            String name,
            float price,
            String description,
            String note,
            int stock,
            String categoryId,
            byte[] imageBytes
    ) throws Exception {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("name", name);
        fields.put("price", String.valueOf(price));
        fields.put("description", description);
        fields.put("note", note);
        fields.put("stock", String.valueOf(stock));
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            fields.put("categoryId", categoryId.trim());
        }
        String path = id == null || id.trim().isEmpty()
                ? "/api/admin/products"
                : "/api/admin/products/" + id.trim();
        String method = id == null || id.trim().isEmpty() ? "POST" : "PUT";
        JSONObject json = apiClient.multipart(method, path, fields, "image", "product.png", "image/png", imageBytes);
        return mapProduct(json);
    }

    public void deleteProduct(String productId) throws Exception {
        apiClient.delete("/api/admin/products/" + productId);
    }

    public NhomSanPham saveCategory(String id, String name, byte[] imageBytes) throws Exception {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("name", name);
        String path = id == null || id.trim().isEmpty()
                ? "/api/admin/categories"
                : "/api/admin/categories/" + id.trim();
        String method = id == null || id.trim().isEmpty() ? "POST" : "PUT";
        JSONObject json = apiClient.multipart(method, path, fields, "image", "category.png", "image/png", imageBytes);
        return mapCategory(json);
    }

    public void deleteCategory(String categoryId) throws Exception {
        apiClient.delete("/api/admin/categories/" + categoryId);
    }

    private ArrayList<SanPham> mapProducts(JSONArray array) {
        ArrayList<SanPham> products = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            products.add(mapProduct(array.optJSONObject(i)));
        }
        return products;
    }

    private SanPham mapProduct(JSONObject json) {
        if (json == null) {
            return new SanPham("", "", 0f, "", "", 0, "", (String) null);
        }
        String categoryId = json.isNull("categoryId") ? "" : String.valueOf(json.optInt("categoryId"));
        return new SanPham(
                String.valueOf(json.optInt("id")),
                json.optString("name", ""),
                (float) json.optDouble("price", 0),
                json.optString("description", ""),
                json.optString("note", ""),
                json.optInt("stock", 0),
                categoryId,
                json.optString("imageUrl", null)
        );
    }

    private ArrayList<NhomSanPham> mapCategories(JSONArray array) {
        ArrayList<NhomSanPham> categories = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            categories.add(mapCategory(array.optJSONObject(i)));
        }
        return categories;
    }

    private NhomSanPham mapCategory(JSONObject json) {
        if (json == null) {
            return new NhomSanPham("", "", (String) null);
        }
        return new NhomSanPham(
                String.valueOf(json.optInt("id")),
                json.optString("name", ""),
                json.optString("imageUrl", null)
        );
    }
}
