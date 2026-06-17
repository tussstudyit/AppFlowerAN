package com.example.appshopbanhang.ui.product;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.session.QuanLyPhien;
import com.example.appshopbanhang.data.model.ChiTietSanPham;
import com.example.appshopbanhang.data.model.SanPham;
import com.example.appshopbanhang.ui.account.TrangCaNhan_nguoidung_Activity;
import com.example.appshopbanhang.ui.adapter.SanPham_DanhMuc_Adapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.cart.GioHang_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;
import com.example.appshopbanhang.ui.order.DonHang_User_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import java.util.ArrayList;

public class DanhMucSanPham_Activity extends AppCompatActivity {
    private ArrayList<SanPham> productList;
    private SanPham_DanhMuc_Adapter productAdapter;
    private DanhSachSanPhamViewModel productListViewModel;
    private QuanLyPhien sessionManager;
    private String tendn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_muc_san_pham);

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
        productList = new ArrayList<>();
        productAdapter = new SanPham_DanhMuc_Adapter(this, productList, false);
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

        String nhomSpId = getIntent().getStringExtra("nhomSpId");
        if (nhomSpId == null) {
            Toast.makeText(this, "ID nhom san pham khong hop le", Toast.LENGTH_SHORT).show();
        } else {
            productListViewModel.loadProductsByCategory(nhomSpId);
        }

        grv.setOnItemClickListener((parent, view, position, id) -> openProductDetail(productList.get(position)));
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btngiohang = findViewById(R.id.btncart);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        btntrangchu.setOnClickListener(view -> openWithUser(TrangchuNgdung_Activity.class));
        btntimkiem.setOnClickListener(view -> openWithUser(TimKiemSanPham_Activity.class));
        btngiohang.setOnClickListener(view -> openWithUser(GioHang_Activity.class));
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

    private void openProductDetail(SanPham sanPham) {
        Intent intent = new Intent(this, ChiTietSanPham_Activity.class);
        intent.putExtra("chitietsanpham", new ChiTietSanPham(
                sanPham.getMasp(),
                sanPham.getTensp(),
                sanPham.getDongia(),
                sanPham.getMota(),
                sanPham.getGhichu(),
                sanPham.getSoluongkho(),
                sanPham.getMansp(),
                sanPham.getImageUrl()
        ));
        startActivity(intent);
    }
}

