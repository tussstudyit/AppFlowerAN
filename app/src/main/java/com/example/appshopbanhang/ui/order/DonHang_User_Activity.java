package com.example.appshopbanhang.ui.order;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.session.QuanLyPhien;
import com.example.appshopbanhang.data.model.DonHang;
import com.example.appshopbanhang.ui.account.TrangCaNhan_nguoidung_Activity;
import com.example.appshopbanhang.ui.adapter.DonHang_Adapter_NguoiDung;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.cart.GioHang_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;
import com.example.appshopbanhang.ui.product.TimKiemSanPham_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import java.util.ArrayList;

public class DonHang_User_Activity extends AppCompatActivity {
    private ArrayList<DonHang> orders;
    private DonHang_Adapter_NguoiDung donHangAdapter;
    private DanhSachDonHangViewModel orderListViewModel;
    private QuanLyPhien sessionManager;
    private String tendn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang_user);

        sessionManager = new QuanLyPhien(this);
        tendn = sessionManager.getUsername();
        if (tendn == null) {
            tendn = getIntent().getStringExtra("tendn");
        }
        if (tendn == null) {
            startActivity(new Intent(this, DangNhap_Activity.class));
            finish();
            return;
        }

        TextView textTendn = findViewById(R.id.tendn);
        textTendn.setText(tendn);

        ListView listView = findViewById(R.id.listViewChiTiet);
        orders = new ArrayList<>();
        donHangAdapter = new DonHang_Adapter_NguoiDung(this, orders);
        listView.setAdapter(donHangAdapter);

        orderListViewModel = new ViewModelProvider(this).get(DanhSachDonHangViewModel.class);
        orderListViewModel.getOrders().observe(this, result -> {
            orders.clear();
            if (result != null) {
                orders.addAll(result);
            }
            donHangAdapter.notifyDataSetChanged();
        });
        orderListViewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
        orderListViewModel.loadOrdersByCustomerName(tendn);

        listView.setOnItemClickListener((parent, view, position, id) -> openOrderDetail(orders.get(position)));
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        btntimkiem.setOnClickListener(view -> openWithUser(TimKiemSanPham_Activity.class));
        btntrangchu.setOnClickListener(view -> openWithUser(TrangchuNgdung_Activity.class));
        btncard.setOnClickListener(view -> openWithUser(GioHang_Activity.class));
        btndonhang.setOnClickListener(view -> openWithUser(DonHang_User_Activity.class));
        btncanhan.setOnClickListener(view -> openWithUser(TrangCaNhan_nguoidung_Activity.class));
    }

    private void openWithUser(Class<?> activityClass) {
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, DangNhap_Activity.class));
            return;
        }
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("tendn", tendn);
        startActivity(intent);
    }

    private void openOrderDetail(DonHang order) {
        Toast.makeText(this, "ID don hang: " + order.getId(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChiTietDonHang_NguoiDung_Activity.class);
        intent.putExtra("donHangId", String.valueOf(order.getId()));
        intent.putExtra("tendn", tendn);
        startActivity(intent);
    }
}

