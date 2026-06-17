package com.example.appshopbanhang.ui.order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.model.ChiTietDonHang;
import com.example.appshopbanhang.data.repository.DonHangRepository;
import com.example.appshopbanhang.ui.account.TrangCaNhan_nguoidung_Activity;
import com.example.appshopbanhang.ui.adapter.ChiTietDonHang_Adapter_3TrangThai_NguoiDung;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.cart.GioHang_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;
import com.example.appshopbanhang.ui.product.TimKiemSanPham_Activity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChiTietDonHang_NguoiDung_3_TrangThai_Khac extends AppCompatActivity {
    private final ArrayList<ChiTietDonHang> details = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ChiTietDonHang_Adapter_3TrangThai_NguoiDung adapter;
    private DonHangRepository orderRepository;
    private String tendn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang_nguoidung_3_trang_thai_khac);

        TextView textTendn = findViewById(R.id.tendn);
        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        tendn = sharedPre.getString("tendn", null);
        if (tendn == null) {
            startActivity(new Intent(this, DangNhap_Activity.class));
            finish();
            return;
        }
        textTendn.setText(tendn);

        orderRepository = new DonHangRepository(this);
        ListView listViewChiTiet = findViewById(R.id.listtk);
        adapter = new ChiTietDonHang_Adapter_3TrangThai_NguoiDung(this, details, tendn);
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
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        btncard.setOnClickListener(view -> openWithUser(GioHang_Activity.class));
        btntrangchu.setOnClickListener(view -> openWithUser(TrangchuNgdung_Activity.class));
        btndonhang.setOnClickListener(view -> openWithUser(DonHang_User_Activity.class));
        btncanhan.setOnClickListener(view -> openWithUser(TrangCaNhan_nguoidung_Activity.class));
        btntimkiem.setOnClickListener(view -> openWithUser(TimKiemSanPham_Activity.class));
    }

    private void openWithUser(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("tendn", tendn);
        startActivity(intent);
    }
}
