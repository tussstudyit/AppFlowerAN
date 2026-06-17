package com.example.appshopbanhang.ui.product;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.session.QuanLyPhien;
import com.example.appshopbanhang.data.model.SanPham;
import com.example.appshopbanhang.ui.account.TrangCaNhan_nguoidung_Activity;
import com.example.appshopbanhang.ui.adapter.SanPham_TimKiem_Adapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.cart.GioHang_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;
import com.example.appshopbanhang.ui.order.DonHang_User_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import java.util.ArrayList;

public class TimKiemSanPham_Activity extends AppCompatActivity {
    private ArrayList<SanPham> productList;
    private SanPham_TimKiem_Adapter productAdapter;
    private DanhSachSanPhamViewModel productListViewModel;
    private QuanLyPhien sessionManager;
    private String tendn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_kiem_san_pham);

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

        EditText timkiem = findViewById(R.id.timkiem);
        timkiem.requestFocus();

        GridView grv = findViewById(R.id.grv);
        productList = new ArrayList<>();
        productAdapter = new SanPham_TimKiem_Adapter(this, productList, false);
        grv.setAdapter(productAdapter);

        productListViewModel = new ViewModelProvider(this).get(DanhSachSanPhamViewModel.class);
        productListViewModel.getProducts().observe(this, products -> {
            productList.clear();
            if (products != null) {
                productList.addAll(products);
            }
            productAdapter.notifyDataSetChanged();
        });
        productListViewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        timkiem.setOnClickListener(view ->
                productListViewModel.searchProducts(timkiem.getText().toString().trim())
        );

        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        btntimkiem.setOnClickListener(view ->
                startActivity(new Intent(this, TimKiemSanPham_Activity.class))
        );
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
}

