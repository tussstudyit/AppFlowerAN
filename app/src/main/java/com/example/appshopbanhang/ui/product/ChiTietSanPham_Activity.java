package com.example.appshopbanhang.ui.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.image.HinhAnhUtils;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.ChiTietSanPham;
import com.example.appshopbanhang.data.model.DanhGiaSanPham;
import com.example.appshopbanhang.data.repository.DanhGiaRepository;
import com.example.appshopbanhang.data.repository.GioHangManager;
import com.example.appshopbanhang.ui.account.TrangCaNhan_nguoidung_Activity;
import com.example.appshopbanhang.ui.adapter.DanhGiaSanPham_Adapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.cart.GioHang_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;
import com.example.appshopbanhang.ui.order.DonHang_User_Activity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChiTietSanPham_Activity extends AppCompatActivity {
    private String masp;
    private String tendn;
    private ChiTietSanPham chiTietSanPham;
    private GioHangManager gioHangManager;
    private final ArrayList<DanhGiaSanPham> reviews = new ArrayList<>();
    private DanhGiaSanPham_Adapter adapter;
    private DanhGiaRepository reviewRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_san_pham);

        Button btndathang = findViewById(R.id.btndathang);
        Button btnaddcart = findViewById(R.id.btnaddcart);
        TextView tensp = findViewById(R.id.tensp);
        ImageView imgsp = findViewById(R.id.imgsp);
        TextView dongia = findViewById(R.id.dongia);
        TextView ghichu = findViewById(R.id.ghichu);
        TextView mota = findViewById(R.id.mota);
        TextView soluongkho = findViewById(R.id.soluongkho);
        TextView textTendn = findViewById(R.id.tendn);
        ListView listView = findViewById(R.id.listtk);

        gioHangManager = GioHangManager.getInstance();
        reviewRepository = new DanhGiaRepository(this);

        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        tendn = sharedPre.getString("tendn", null);
        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            startActivity(new Intent(this, DangNhap_Activity.class));
            finish();
            return;
        }

        chiTietSanPham = getIntent().getParcelableExtra("chitietsanpham");
        if (chiTietSanPham != null) {
            masp = chiTietSanPham.getMasp();
            tensp.setText(chiTietSanPham.getTensp());
            dongia.setText(chiTietSanPham.getDongia() != null ? MoneyFormatter.format(chiTietSanPham.getDongia()) : "");
            mota.setText(chiTietSanPham.getMota() != null ? chiTietSanPham.getMota() : "");
            soluongkho.setText(String.valueOf(chiTietSanPham.getSoluongkho()));
            ghichu.setText(chiTietSanPham.getGhichu() == null ? "" : chiTietSanPham.getGhichu());
            HinhAnhUtils.loadUrl(imgsp, chiTietSanPham.getImageUrl(), R.drawable.vest, 720, 720);
        } else {
            tensp.setText("Khong co du lieu");
        }

        btnaddcart.setOnClickListener(view -> addToCart(false));
        btndathang.setOnClickListener(view -> addToCart(true));

        adapter = new DanhGiaSanPham_Adapter(this, reviews, true);
        listView.setAdapter(adapter);
        loadReviews();
        setupNavigationButtons();
    }

    private void addToCart(boolean openCart) {
        if (chiTietSanPham == null) {
            return;
        }
        if (!getSharedPreferences("MyPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, DangNhap_Activity.class));
            return;
        }
        gioHangManager.addItem(chiTietSanPham);
        Toast.makeText(this, "Them vao gio hang thanh cong", Toast.LENGTH_SHORT).show();
        if (openCart) {
            startActivity(new Intent(this, GioHang_Activity.class));
        }
    }

    private void loadReviews() {
        if (masp == null) {
            return;
        }
        executor.execute(() -> {
            try {
                ArrayList<DanhGiaSanPham> result = reviewRepository.getReviewsByProduct(masp);
                mainHandler.post(() -> {
                    reviews.clear();
                    reviews.addAll(result);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Khong tai duoc danh gia", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupNavigationButtons() {
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btngiohang = findViewById(R.id.btncart);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        btntrangchu.setOnClickListener(view -> startActivity(new Intent(this, TrangchuNgdung_Activity.class)));
        btntimkiem.setOnClickListener(view -> startActivity(new Intent(this, TimKiemSanPham_Activity.class)));
        btngiohang.setOnClickListener(view -> navigateIfLoggedIn(GioHang_Activity.class));
        btndonhang.setOnClickListener(view -> navigateIfLoggedIn(DonHang_User_Activity.class));
        btncanhan.setOnClickListener(view -> navigateIfLoggedIn(TrangCaNhan_nguoidung_Activity.class));
    }

    private void navigateIfLoggedIn(Class<?> activityClass) {
        if (!getSharedPreferences("MyPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, DangNhap_Activity.class));
            return;
        }
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("tendn", tendn);
        startActivity(intent);
    }
}
