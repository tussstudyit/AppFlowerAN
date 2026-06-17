package com.example.appshopbanhang.ui.order;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.session.QuanLyPhien;
import com.example.appshopbanhang.data.model.DonHang;
import com.example.appshopbanhang.ui.account.Taikhoan_admin_Activity;
import com.example.appshopbanhang.ui.account.TrangCaNhan_admin_Activity;
import com.example.appshopbanhang.ui.adapter.DonHang_Adapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.category.Nhomsanpham_admin_Actvity;
import com.example.appshopbanhang.ui.home.TrangchuAdmin_Activity;
import com.example.appshopbanhang.ui.product.Sanpham_admin_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import java.util.ArrayList;

public class DonHang_admin_Activity extends AppCompatActivity {
    private ArrayList<DonHang> orders;
    private DonHang_Adapter donHangAdapter;
    private DanhSachDonHangViewModel orderListViewModel;
    private QuanLyPhien sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang_admin);

        sessionManager = new QuanLyPhien(this);

        ListView listView = findViewById(R.id.listViewChiTiet);
        orders = new ArrayList<>();
        donHangAdapter = new DonHang_Adapter(this, orders);
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
        orderListViewModel.loadAllOrders();

        listView.setOnItemClickListener((parent, view, position, id) -> openOrderDetail(orders.get(position)));
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btnsanpham = findViewById(R.id.btnsanpham);
        ImageButton btnnhomsp = findViewById(R.id.btnnhomsp);
        ImageButton btntaikhoan = findViewById(R.id.btntaikhoan);

        btntrangchu.setOnClickListener(view -> startActivity(new Intent(this, TrangchuAdmin_Activity.class)));
        btncanhan.setOnClickListener(view -> {
            if (!sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, DangNhap_Activity.class));
            } else {
                startActivity(new Intent(this, TrangCaNhan_admin_Activity.class));
            }
        });
        btndonhang.setOnClickListener(view -> startActivity(new Intent(this, DonHang_admin_Activity.class)));
        btnsanpham.setOnClickListener(view -> startActivity(new Intent(this, Sanpham_admin_Activity.class)));
        btnnhomsp.setOnClickListener(view -> startActivity(new Intent(this, Nhomsanpham_admin_Actvity.class)));
        btntaikhoan.setOnClickListener(view -> startActivity(new Intent(this, Taikhoan_admin_Activity.class)));
    }

    private void openOrderDetail(DonHang order) {
        Toast.makeText(this, "ID don hang: " + order.getId(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChiTietDonHang_Admin_Activity.class);
        intent.putExtra("donHangId", String.valueOf(order.getId()));
        startActivity(intent);
    }
}

