package com.example.appshopbanhang.data.repository;

import android.content.Context;

import com.example.appshopbanhang.core.network.ApiConfig;
import com.example.appshopbanhang.core.network.ApiClient;
import com.example.appshopbanhang.data.model.ChiTietDonHang;
import com.example.appshopbanhang.data.model.DonHang;
import com.example.appshopbanhang.data.model.GioHang;
import com.example.appshopbanhang.data.model.VnpayPayment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DonHangRepository {
    private static final String VNPAY_LOCALE = "vn";

    private final ApiClient apiClient = new ApiClient();

    public DonHangRepository(Context context) {
        ApiConfig.load(context);
    }

    public ArrayList<DonHang> getOrdersByCustomerName(String customerName) throws Exception {
        if (customerName == null || customerName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put("username", customerName.trim());
        return mapOrders(apiClient.getArray("/api/orders?" + ApiClient.query(params)));
    }

    public ArrayList<DonHang> getAllOrders() throws Exception {
        return mapOrders(apiClient.getArray("/api/admin/orders"));
    }

    public DonHang getOrder(int orderId) throws Exception {
        return mapOrder(apiClient.getObject("/api/orders/" + orderId));
    }

    public ArrayList<ChiTietDonHang> getOrderDetails(int orderId) throws Exception {
        return new ArrayList<>(getOrder(orderId).getChiTietList());
    }

    public DonHang createOrder(
            String username,
            String customerName,
            String address,
            String phone,
            List<GioHang> items
    ) throws Exception {
        return mapOrder(apiClient.postJson("/api/orders", buildOrderRequest(username, customerName, address, phone, items)));
    }

    public VnpayPayment createVnpayOrder(
            String username,
            String customerName,
            String address,
            String phone,
            List<GioHang> items
    ) throws Exception {
        JSONObject request = buildOrderRequest(username, customerName, address, phone, items);
        request.put("returnUrl", ApiConfig.getBaseUrl() + "/api/payments/vnpay/return");
        request.put("locale", VNPAY_LOCALE);

        JSONObject response = apiClient.postJson("/api/payments/vnpay/create-order", request);
        return new VnpayPayment(
                response.optInt("orderId"),
                response.optString("txnRef", ""),
                (float) response.optDouble("amount", 0),
                response.optString("paymentStatus", ""),
                response.optString("paymentUrl", ""),
                response.optString("qrContent", ""),
                response.optString("qrImageBase64", ""),
                response.optString("qrMimeType", ""),
                response.optLong("expiresAt", 0L),
                response.optString("message", "")
        );
    }

    public DonHang updateStatus(int orderId, String status) throws Exception {
        JSONObject request = new JSONObject();
        request.put("status", status);
        return mapOrder(apiClient.putJson("/api/admin/orders/" + orderId + "/status", request));
    }

    private JSONObject buildOrderRequest(
            String username,
            String customerName,
            String address,
            String phone,
            List<GioHang> items
    ) throws Exception {
        JSONObject request = new JSONObject();
        request.put("username", username);
        request.put("customerName", customerName);
        request.put("address", address);
        request.put("phone", phone);

        JSONArray itemArray = new JSONArray();
        for (GioHang item : items) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("productId", Integer.parseInt(item.getSanPham().getMasp()));
            itemJson.put("quantity", item.getSoLuong());
            itemArray.put(itemJson);
        }
        request.put("items", itemArray);
        return request;
    }

    private ArrayList<DonHang> mapOrders(JSONArray array) {
        ArrayList<DonHang> orders = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json != null) {
                orders.add(mapOrder(json));
            }
        }
        return orders;
    }

    private DonHang mapOrder(JSONObject json) {
        DonHang order = new DonHang(
                json.optInt("id"),
                json.optString("customerName", ""),
                json.optString("address", ""),
                json.optString("phone", ""),
                (float) json.optDouble("total", 0),
                json.optString("orderDate", ""),
                json.optString("status", "")
        );
        order.setPaymentMethod(json.optString("paymentMethod", ""));
        order.setPaymentStatus(json.optString("paymentStatus", ""));

        JSONArray items = json.optJSONArray("items");
        ArrayList<ChiTietDonHang> details = new ArrayList<>();
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.optJSONObject(i);
                if (item != null) {
                    details.add(new ChiTietDonHang(
                            item.optInt("id"),
                            json.optInt("id"),
                            item.optInt("productId"),
                            item.optString("productName", ""),
                            item.optInt("quantity", 0),
                            (float) item.optDouble("price", 0),
                            item.optString("imageUrl", null)
                    ));
                }
            }
        }
        order.setChiTietList(details);
        return order;
    }
}
