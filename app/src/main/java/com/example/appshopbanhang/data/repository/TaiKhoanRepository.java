package com.example.appshopbanhang.data.repository;

import android.content.Context;

import com.example.appshopbanhang.core.network.ApiConfig;
import com.example.appshopbanhang.core.network.ApiClient;
import com.example.appshopbanhang.data.model.KetQuaDangNhap;
import com.example.appshopbanhang.data.model.TaiKhoan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaiKhoanRepository {
    private final ApiClient apiClient = new ApiClient();

    public TaiKhoanRepository(Context context) {
        ApiConfig.load(context);
    }

    public KetQuaDangNhap login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return KetQuaDangNhap.error("Vui long nhap day du tai khoan va mat khau");
        }

        JSONObject request = new JSONObject();
        request.put("username", username.trim());
        request.put("password", password);
        JSONObject response = apiClient.postJson("/api/auth/login", request);
        if (response.optBoolean("success", false)) {
            return KetQuaDangNhap.success(response.optString("username", username.trim()), response.optString("role", ""));
        }
        return KetQuaDangNhap.error(response.optString("message", "Ten dang nhap hoac mat khau khong dung"));
    }

    public ArrayList<TaiKhoan> getAllAccounts() throws Exception {
        JSONArray array = apiClient.getArray("/api/admin/accounts");
        ArrayList<TaiKhoan> accounts = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json != null) {
                accounts.add(new TaiKhoan(
                        json.optString("username", ""),
                        json.optString("password", ""),
                        json.optString("role", "")
                ));
            }
        }
        return accounts;
    }

    public TaiKhoan saveAccount(String oldUsername, String username, String password, String role) throws Exception {
        JSONObject request = new JSONObject();
        request.put("username", username.trim());
        request.put("password", password);
        request.put("role", role);

        boolean isUpdate = oldUsername != null && !oldUsername.trim().isEmpty();
        JSONObject response = isUpdate
                ? apiClient.putJson("/api/admin/accounts/" + oldUsername.trim(), request)
                : apiClient.postJson("/api/admin/accounts", request);
        return new TaiKhoan(
                response.optString("username", username.trim()),
                response.optString("password", password),
                response.optString("role", role)
        );
    }

    public void deleteAccount(String username) throws Exception {
        apiClient.delete("/api/admin/accounts/" + username);
    }
}
