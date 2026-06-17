package com.example.appshopbanhang.ui.product;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.session.QuanLyPhien;
import com.example.appshopbanhang.data.model.SanPham;
import com.example.appshopbanhang.ui.account.TrangCaNhan_nguoidung_Activity;
import com.example.appshopbanhang.ui.adapter.SanPhamAdapter;
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

public class TatCaSanPham_Activity extends AppCompatActivity {
    private ArrayList<SanPham> mangSPgrv;
    private SanPhamAdapter adapterGrv;
    private DanhSachSanPhamViewModel productListViewModel;
    private QuanLyPhien sessionManager;
    private String tendn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tat_ca_san_pham);

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

        GridView grv = findViewById(R.id.grv);
        mangSPgrv = new ArrayList<>();
        adapterGrv = new SanPhamAdapter(this, mangSPgrv, false);
        grv.setAdapter(adapterGrv);

        productListViewModel = new ViewModelProvider(this).get(DanhSachSanPhamViewModel.class);
        productListViewModel.getProducts().observe(this, products -> {
            mangSPgrv.clear();
            if (products != null) {
                mangSPgrv.addAll(products);
            }
            adapterGrv.notifyDataSetChanged();
        });
        productListViewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        productListViewModel.loadAllProducts();
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        EditText timkiem = findViewById(R.id.timkiem);

        btntrangchu.setOnClickListener(view -> openWithUser(TrangchuNgdung_Activity.class));
        btntimkiem.setOnClickListener(view -> openWithUser(TimKiemSanPham_Activity.class));
        timkiem.setOnClickListener(view -> openWithUser(TimKiemSanPham_Activity.class));
        btndonhang.setOnClickListener(view -> openWithUser(DonHang_User_Activity.class));
        btncard.setOnClickListener(view -> openWithUser(GioHang_Activity.class));
        btncanhan.setOnClickListener(view -> openWithUser(TrangCaNhan_nguoidung_Activity.class));
    }

    private void openWithUser(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("tendn", tendn);
        startActivity(intent);
    }
}

