package com.example.appshopbanhang.ui.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.model.TaiKhoan;
import com.example.appshopbanhang.data.repository.TaiKhoanRepository;
import com.example.appshopbanhang.ui.adapter.TaiKhoanAdapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.category.Nhomsanpham_admin_Actvity;
import com.example.appshopbanhang.ui.home.TrangchuAdmin_Activity;
import com.example.appshopbanhang.ui.order.DonHang_admin_Activity;
import com.example.appshopbanhang.ui.product.Sanpham_admin_Activity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Taikhoan_admin_Activity extends AppCompatActivity {
    private final ArrayList<TaiKhoan> accounts = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private TaiKhoanAdapter adapter;
    private TaiKhoanRepository accountRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taikhoan_admin);

        accountRepository = new TaiKhoanRepository(this);
        FloatingActionButton addButton = findViewById(R.id.btnthem);
        ListView lv = findViewById(R.id.listtk);
        adapter = new TaiKhoanAdapter(this, R.layout.ds_taikhoan, accounts);
        lv.setAdapter(adapter);
        addButton.setOnClickListener(view -> startActivity(new Intent(this, ThemTaiKhoan_Activity.class)));

        setupNavigationButtons();
        loadAccounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccounts();
    }

    private void loadAccounts() {
        executor.execute(() -> {
            try {
                ArrayList<TaiKhoan> result = accountRepository.getAllAccounts();
                mainHandler.post(() -> {
                    accounts.clear();
                    accounts.addAll(result);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Khong tai duoc tai khoan", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupNavigationButtons() {
        findViewById(R.id.btntrangchu).setOnClickListener(view -> startActivity(new Intent(this, TrangchuAdmin_Activity.class)));
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        btncanhan.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
                startActivity(new Intent(this, DangNhap_Activity.class));
            } else {
                startActivity(new Intent(this, TrangCaNhan_admin_Activity.class));
            }
        });
        findViewById(R.id.btndonhang).setOnClickListener(view -> startActivity(new Intent(this, DonHang_admin_Activity.class)));
        findViewById(R.id.btnsanpham).setOnClickListener(view -> startActivity(new Intent(this, Sanpham_admin_Activity.class)));
        findViewById(R.id.btnnhomsp).setOnClickListener(view -> startActivity(new Intent(this, Nhomsanpham_admin_Actvity.class)));
        findViewById(R.id.btntaikhoan).setOnClickListener(view -> startActivity(new Intent(this, Taikhoan_admin_Activity.class)));
    }
}
