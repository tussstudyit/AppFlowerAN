package com.example.appshopbanhang.data.repository;

import android.content.Context;

import com.example.appshopbanhang.core.network.ApiConfig;
import com.example.appshopbanhang.core.network.ApiClient;
import com.example.appshopbanhang.data.model.DanhGiaSanPham;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DanhGiaRepository {
    private final ApiClient apiClient = new ApiClient();

    public DanhGiaRepository(Context context) {
        ApiConfig.load(context);
    }

    public ArrayList<DanhGiaSanPham> getReviewsByProduct(String productId) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("productId", productId);
        JSONArray array = apiClient.getArray("/api/reviews?" + ApiClient.query(params));
        ArrayList<DanhGiaSanPham> reviews = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json != null) {
                reviews.add(mapReview(json));
            }
        }
        return reviews;
    }

    public DanhGiaSanPham createReview(DanhGiaSanPham review) throws Exception {
        JSONObject request = new JSONObject();
        request.put("productId", Integer.parseInt(review.getMasp()));
        if (review.getId_chitiet() != null && !review.getId_chitiet().trim().isEmpty()) {
            request.put("orderDetailId", Integer.parseInt(review.getId_chitiet()));
        }
        request.put("username", review.getTendn());
        request.put("content", review.getNoidung());
        request.put("star1", review.getSao1());
        request.put("star2", review.getSao2());
        request.put("star3", review.getSao3());
        request.put("star4", review.getSao4());
        request.put("star5", review.getSao5());
        return mapReview(apiClient.postJson("/api/reviews", request));
    }

    public boolean isReviewExists(String orderDetailId) throws Exception {
        JSONArray array = apiClient.getArray("/api/reviews");
        ArrayList<DanhGiaSanPham> reviews = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json != null) {
                reviews.add(mapReview(json));
            }
        }
        for (DanhGiaSanPham review : reviews) {
            if (orderDetailId != null && orderDetailId.equals(review.getId_chitiet())) {
                return true;
            }
        }
        return false;
    }

    private DanhGiaSanPham mapReview(JSONObject json) {
        return new DanhGiaSanPham(
                String.valueOf(json.optInt("id")),
                json.isNull("productId") ? "" : String.valueOf(json.optInt("productId")),
                json.isNull("orderDetailId") ? "" : String.valueOf(json.optInt("orderDetailId")),
                json.optString("content", ""),
                json.optString("username", ""),
                json.optString("star1", ""),
                json.optString("star2", ""),
                json.optString("star3", ""),
                json.optString("star4", ""),
                json.optString("star5", "")
        );
    }
}
