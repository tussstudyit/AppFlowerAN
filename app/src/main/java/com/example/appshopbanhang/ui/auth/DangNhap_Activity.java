package com.example.appshopbanhang.ui.auth;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.network.ApiConfig;
import com.example.appshopbanhang.ui.home.TrangchuAdmin_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


public class DangNhap_Activity extends AppCompatActivity {
    private DangNhapViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        EditText apiBackend = findViewById(R.id.apiBackend);
        EditText tdn = findViewById(R.id.tdn);
        EditText mk = findViewById(R.id.mk);
        TextView dangki = findViewById(R.id.dangki);
        TextView qmk = findViewById(R.id.qmk);

        ApiConfig.load(this);
        apiBackend.setText(ApiConfig.getBaseUrl());

        loginViewModel = new ViewModelProvider(this).get(DangNhapViewModel.class);
        loginViewModel.getLoginState().observe(this, this::handleLoginState);

        qmk.setOnClickListener(view -> {
            saveBackendUrl(apiBackend);
            Intent intent = new Intent(getApplicationContext(), DoiMatKhau_Activity.class);
            startActivity(intent);
        });

        dangki.setOnClickListener(view -> {
            saveBackendUrl(apiBackend);
            Intent intent = new Intent(getApplicationContext(), DangKiTaiKhoan_Activity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            saveBackendUrl(apiBackend);
            String username = tdn.getText().toString();
            String password = mk.getText().toString();
            loginViewModel.login(username, password);
        });
    }

    private void saveBackendUrl(EditText apiBackend) {
        ApiConfig.save(this, apiBackend.getText().toString());
        apiBackend.setText(ApiConfig.getBaseUrl());
    }

    private void handleLoginState(TrangThaiDangNhapUi state) {
        if (state == null) {
            return;
        }

        if (!state.isSuccess()) {
            Toast.makeText(DangNhap_Activity.this, state.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent;
        String role = state.getRole();
        if ("admin".equals(role)) {
            intent = new Intent(DangNhap_Activity.this, TrangchuAdmin_Activity.class);
            Toast.makeText(this, "Dang nhap voi quyen Admin", Toast.LENGTH_SHORT).show();
        } else if ("user".equals(role)) {
            intent = new Intent(DangNhap_Activity.this, TrangchuNgdung_Activity.class);
            intent.putExtra("tendn", state.getUsername());
            Toast.makeText(this, "Dang nhap voi quyen User", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Quyen khong xac dinh", Toast.LENGTH_SHORT).show();
            loginViewModel.logout();
            return;
        }

        startActivity(intent);
        finish();
    }
}

