package com.example.appshopbanhang.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.repository.TaiKhoanRepository;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DoiMatKhau_Activity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private TaiKhoanRepository accountRepository;
    private String role = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        accountRepository = new TaiKhoanRepository(this);
        TextView ql = findViewById(R.id.ql);
        ql.setOnClickListener(view -> startActivity(new Intent(this, DangNhap_Activity.class)));

        Button btndoimk = findViewById(R.id.btnDoi);
        EditText tendn = findViewById(R.id.tdn);
        EditText matkhau = findViewById(R.id.mk);
        EditText nhaplaimatkhau = findViewById(R.id.mk2);
        Spinner spinner = findViewById(R.id.quyen);

        ArrayList<String> roles = new ArrayList<>();
        roles.add("user");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, roles);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, android.view.View view, int i, long l) {
                role = roles.get(i);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {
            }
        });

        btndoimk.setOnClickListener(view -> {
            String username = tendn.getText().toString().trim();
            String password = matkhau.getText().toString().trim();
            String confirmPassword = nhaplaimatkhau.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Ten dang nhap va mat khau khong duoc de trong", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mat khau khong khop", Toast.LENGTH_SHORT).show();
                return;
            }
            executor.execute(() -> {
                try {
                    accountRepository.saveAccount(username, username, password, role);
                    mainHandler.post(() -> {
                        Toast.makeText(this, "Doi mat khau thanh cong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, DangNhap_Activity.class));
                        finish();
                    });
                } catch (Exception exception) {
                    mainHandler.post(() -> Toast.makeText(this, "Khong doi duoc mat khau", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}
