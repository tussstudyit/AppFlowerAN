package com.example.appshopbanhang.ui.launch;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class KhoiDong_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Tạo Handler để chuyển Activity sau 5 giây
        new Handler().postDelayed(() -> {
            // Chuyển sang DangNhap_Activity
            Intent intent = new Intent(KhoiDong_Activity.this, DangNhap_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc KhoiDong_Activity nếu không muốn quay lại
        }, 5000); // 5000 milliseconds = 5 seconds
    }
}
