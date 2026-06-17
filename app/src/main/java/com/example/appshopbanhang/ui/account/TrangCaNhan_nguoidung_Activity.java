package com.example.appshopbanhang.ui.account;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.cart.GioHang_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;
import com.example.appshopbanhang.ui.order.DonHang_User_Activity;
import com.example.appshopbanhang.ui.product.TimKiemSanPham_Activity;

public class TrangCaNhan_nguoidung_Activity extends AppCompatActivity {
    private String tendn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_ca_nhan_nguoidung);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        tendn = sharedPreferences.getString("tendn", null);
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

        Button dangxuat = findViewById(R.id.btndangxuat);
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        btncard.setOnClickListener(view -> {
            if (sharedPreferences.getBoolean("isLoggedIn", false)) {
                startActivity(new Intent(this, GioHang_Activity.class));
            } else {
                startActivity(new Intent(this, DangNhap_Activity.class));
            }
        });

        btntrangchu.setOnClickListener(view -> startActivity(new Intent(this, TrangchuNgdung_Activity.class)));
        btntimkiem.setOnClickListener(view -> startActivity(new Intent(this, TimKiemSanPham_Activity.class)));
        btncanhan.setOnClickListener(view -> {
        });

        btndonhang.setOnClickListener(view -> {
            if (sharedPreferences.getBoolean("isLoggedIn", false)) {
                Intent intent = new Intent(this, DonHang_User_Activity.class);
                intent.putExtra("tendn", tendn);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, DangNhap_Activity.class));
            }
        });

        dangxuat.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.putString("tendn", null);
                    editor.apply();

                    Intent intent = new Intent(this, TrangchuNgdung_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show());
    }
}
