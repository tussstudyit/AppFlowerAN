package com.example.appshopbanhang.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.model.NhomSanPham;
import com.example.appshopbanhang.data.model.SanPham;
import com.example.appshopbanhang.data.repository.SanPhamRepository;
import com.example.appshopbanhang.ui.account.Taikhoan_admin_Activity;
import com.example.appshopbanhang.ui.account.TrangCaNhan_admin_Activity;
import com.example.appshopbanhang.ui.adapter.NhomSanPham_trangChuadmin_Adapter;
import com.example.appshopbanhang.ui.adapter.SanPham_TrangChuAdmin_Adapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.category.Nhomsanpham_admin_Actvity;
import com.example.appshopbanhang.ui.order.DonHang_admin_Activity;
import com.example.appshopbanhang.ui.product.Sanpham_admin_Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrangchuAdmin_Activity extends AppCompatActivity {
    private final ArrayList<SanPham> products = new ArrayList<>();
    private final ArrayList<NhomSanPham> categories = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private SanPham_TrangChuAdmin_Adapter productAdapter;
    private NhomSanPham_trangChuadmin_Adapter categoryAdapter;
    private SanPhamRepository productRepository;
    private ViewPager2 viewPager;
    private Handler slideHandler;
    private Runnable slideRunnable;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_admin);

        productRepository = new SanPhamRepository(this);
        GridView categoryGrid = findViewById(R.id.grv2);
        GridView productGrid = findViewById(R.id.grv1);
        categoryAdapter = new NhomSanPham_trangChuadmin_Adapter(this, categories, false);
        productAdapter = new SanPham_TrangChuAdmin_Adapter(this, products, false);
        categoryGrid.setAdapter(categoryAdapter);
        productGrid.setAdapter(productAdapter);

        setupNavigation();
        setupSlides();
        loadHomeData();
    }

    private void setupNavigation() {
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btnsanpham = findViewById(R.id.btnsanpham);
        ImageButton btnnhomsp = findViewById(R.id.btnnhomsp);
        ImageButton btntaikhoan = findViewById(R.id.btntaikhoan);

        btncanhan.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
                startActivity(new Intent(this, DangNhap_Activity.class));
            } else {
                startActivity(new Intent(this, TrangCaNhan_admin_Activity.class));
            }
        });
        btndonhang.setOnClickListener(view -> startActivity(new Intent(this, DonHang_admin_Activity.class)));
        btnsanpham.setOnClickListener(view -> startActivity(new Intent(this, Sanpham_admin_Activity.class)));
        btnnhomsp.setOnClickListener(view -> startActivity(new Intent(this, Nhomsanpham_admin_Actvity.class)));
        btntaikhoan.setOnClickListener(view -> startActivity(new Intent(this, Taikhoan_admin_Activity.class)));
    }

    private void setupSlides() {
        viewPager = findViewById(R.id.sl1);
        int[] adImages = {R.drawable.sl6, R.drawable.sl2, R.drawable.sl1, R.drawable.sl3};
        viewPager.setAdapter(new ViewPagerAdapter(adImages));
        slideHandler = new Handler(Looper.getMainLooper());
        slideRunnable = () -> {
            currentPage = (currentPage + 1) % adImages.length;
            viewPager.setCurrentItem(currentPage, true);
            slideHandler.postDelayed(slideRunnable, 6000);
        };
        slideHandler.postDelayed(slideRunnable, 6000);
    }

    private void loadHomeData() {
        executor.execute(() -> {
            try {
                ArrayList<NhomSanPham> loadedCategories = productRepository.getRandomCategories(10);
                ArrayList<SanPham> loadedProducts = productRepository.getAllProducts();
                Collections.shuffle(loadedProducts);
                if (loadedProducts.size() > 10) {
                    loadedProducts = new ArrayList<>(loadedProducts.subList(0, 10));
                }
                ArrayList<SanPham> finalProducts = loadedProducts;
                mainHandler.post(() -> {
                    categories.clear();
                    categories.addAll(loadedCategories);
                    categoryAdapter.notifyDataSetChanged();

                    products.clear();
                    products.addAll(finalProducts);
                    productAdapter.notifyDataSetChanged();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Khong tai duoc du lieu backend", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (slideHandler != null && slideRunnable != null) {
            slideHandler.removeCallbacks(slideRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (slideHandler != null && slideRunnable != null) {
            slideHandler.postDelayed(slideRunnable, 2000);
        }
    }

    private static class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.SlideViewHolder> {
        private final int[] images;

        ViewPagerAdapter(int[] images) {
            this.images = images;
        }

        @Override
        public SlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide, parent, false);
            return new SlideViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SlideViewHolder holder, int position) {
            holder.adImage.setImageResource(images[position]);
        }

        @Override
        public int getItemCount() {
            return images.length;
        }

        static class SlideViewHolder extends RecyclerView.ViewHolder {
            ImageView adImage;

            SlideViewHolder(View itemView) {
                super(itemView);
                adImage = itemView.findViewById(R.id.sl1);
            }
        }
    }
}
