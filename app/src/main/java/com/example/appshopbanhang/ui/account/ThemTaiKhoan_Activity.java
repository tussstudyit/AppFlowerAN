package com.example.appshopbanhang.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.repository.TaiKhoanRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThemTaiKhoan_Activity extends AppCompatActivity {
    private RadioButton admin;
    private RadioButton user;
    private TaiKhoanRepository accountRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_tai_khoan);

        accountRepository = new TaiKhoanRepository(this);
        Button btnadd = findViewById(R.id.btnadd);
        EditText tendn = findViewById(R.id.tdn);
        EditText matkhau = findViewById(R.id.mk);
        admin = findViewById(R.id.admin);
        user = findViewById(R.id.user);

        btnadd.setOnClickListener(view -> {
            String username = tendn.getText().toString().trim();
            String password = matkhau.getText().toString().trim();
            String role = admin.isChecked() ? "admin" : "user";
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui long nhap day du thong tin", Toast.LENGTH_SHORT).show();
                return;
            }
            executor.execute(() -> {
                try {
                    accountRepository.saveAccount(null, username, password, role);
                    mainHandler.post(() -> {
                        Toast.makeText(this, "Them tai khoan thanh cong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Taikhoan_admin_Activity.class));
                        finish();
                    });
                } catch (Exception exception) {
                    mainHandler.post(() -> Toast.makeText(this, "Khong them duoc tai khoan", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}
