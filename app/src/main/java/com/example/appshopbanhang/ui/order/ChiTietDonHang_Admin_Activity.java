package com.example.appshopbanhang.ui.order;

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
import com.example.appshopbanhang.data.model.ChiTietDonHang;
import com.example.appshopbanhang.data.repository.DonHangRepository;
import com.example.appshopbanhang.ui.account.Taikhoan_admin_Activity;
import com.example.appshopbanhang.ui.account.TrangCaNhan_admin_Activity;
import com.example.appshopbanhang.ui.adapter.ChiTietDonHangAdapter_Admin;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.category.Nhomsanpham_admin_Actvity;
import com.example.appshopbanhang.ui.home.TrangchuAdmin_Activity;
import com.example.appshopbanhang.ui.product.Sanpham_admin_Activity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChiTietDonHang_Admin_Activity extends AppCompatActivity {
    private final ArrayList<ChiTietDonHang> details = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ChiTietDonHangAdapter_Admin adapter;
    private DonHangRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang_admin);

        orderRepository = new DonHangRepository(this);
        ListView listViewChiTiet = findViewById(R.id.listtk);
        adapter = new ChiTietDonHangAdapter_Admin(this, details);
        listViewChiTiet.setAdapter(adapter);
        loadDetails();
        setupNavigationButtons();
    }

    private void loadDetails() {
        String donHangIdStr = getIntent().getStringExtra("donHangId");
        if (donHangIdStr == null) {
            Toast.makeText(this, "ID don hang khong hop le", Toast.LENGTH_SHORT).show();
            return;
        }
        int orderId = Integer.parseInt(donHangIdStr);
        executor.execute(() -> {
            try {
                ArrayList<ChiTietDonHang> result = orderRepository.getOrderDetails(orderId);
                mainHandler.post(() -> {
                    details.clear();
                    details.addAll(result);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Khong tai duoc chi tiet don hang", Toast.LENGTH_SHORT).show());
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
