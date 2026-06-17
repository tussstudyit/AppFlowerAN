package com.example.appshopbanhang.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.model.ChiTietSanPham;
import com.example.appshopbanhang.data.model.NhomSanPham;
import com.example.appshopbanhang.data.model.SanPham;
import com.example.appshopbanhang.data.repository.SanPhamRepository;
import com.example.appshopbanhang.ui.account.TrangCaNhan_nguoidung_Activity;
import com.example.appshopbanhang.ui.adapter.NhomSanPhamAdapter;
import com.example.appshopbanhang.ui.adapter.SanPhamAdapter;
import com.example.appshopbanhang.ui.adapter.SanPham_Moi_Adapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.cart.GioHang_Activity;
import com.example.appshopbanhang.ui.order.DonHang_User_Activity;
import com.example.appshopbanhang.ui.product.ChiTietSanPham_Activity;
import com.example.appshopbanhang.ui.product.DanhMucSanPham_Activity;
import com.example.appshopbanhang.ui.product.TatCaSanPham_Activity;
import com.example.appshopbanhang.ui.product.TimKiemSanPham_Activity;
import com.example.appshopbanhang.ui.support.HoTro_Activity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrangchuNgdung_Activity extends AppCompatActivity {
    private final ArrayList<SanPham> gridProducts = new ArrayList<>();
    private final ArrayList<NhomSanPham> categories = new ArrayList<>();
    private final ArrayList<SanPham> rowProducts1 = new ArrayList<>();
    private final ArrayList<SanPham> rowProducts2 = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private NhomSanPhamAdapter categoryAdapter;
    private SanPhamAdapter gridProductAdapter;
    private SanPham_Moi_Adapter rowAdapter1;
    private SanPham_Moi_Adapter rowAdapter2;
    private SanPhamRepository productRepository;
    private ViewPager2 viewPager;
    private Handler slideHandler;
    private Runnable slideRunnable;
    private int currentPage = 0;
    private String tendn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_ngdung);

        productRepository = new SanPhamRepository(this);
        tendn = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("tendn", null);
        TextView textTendn = findViewById(R.id.tendn);
        if (tendn == null) {
            startActivity(new Intent(this, DangNhap_Activity.class));
            finish();
            return;
        }
        textTendn.setText(tendn);

        GridView categoryGrid = findViewById(R.id.grv2);
        GridView productGrid = findViewById(R.id.grv1);
        categoryAdapter = new NhomSanPhamAdapter(this, categories, false);
        gridProductAdapter = new SanPhamAdapter(this, gridProducts, false);
        categoryGrid.setAdapter(categoryAdapter);
        productGrid.setAdapter(gridProductAdapter);
        categoryGrid.setOnItemClickListener(this::openCategory);

        rowAdapter1 = new SanPham_Moi_Adapter(rowProducts1, this::openProductDetail);
        rowAdapter2 = new SanPham_Moi_Adapter(rowProducts2, this::openProductDetail);
        RecyclerView recyclerView1 = findViewById(R.id.list1);
        RecyclerView recyclerView2 = findViewById(R.id.list2);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView1.setAdapter(rowAdapter1);
        recyclerView2.setAdapter(rowAdapter2);

        setupNavigation();
        setupSlides();
        loadHomeData();
    }

    private void setupNavigation() {
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        EditText timkiem = findViewById(R.id.timkiem);
        FloatingActionButton btnhotro = findViewById(R.id.btnhotro);
        TextView xemall = findViewById(R.id.xemall);

        btntrangchu.setOnClickListener(view -> openWithUser(DonHang_User_Activity.class));
        btntimkiem.setOnClickListener(view -> openWithUser(TimKiemSanPham_Activity.class));
        timkiem.setOnClickListener(view -> openWithUser(TimKiemSanPham_Activity.class));
        btndonhang.setOnClickListener(view -> openWithUser(DonHang_User_Activity.class));
        btncard.setOnClickListener(view -> openWithUser(GioHang_Activity.class));
        btncanhan.setOnClickListener(view -> openWithUser(TrangCaNhan_nguoidung_Activity.class));
        xemall.setOnClickListener(view -> openWithUser(TatCaSanPham_Activity.class));
        btnhotro.setOnClickListener(view -> openWithUser(HoTro_Activity.class));
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
                ArrayList<SanPham> firstGrid = slice(loadedProducts, 8);
                ArrayList<SanPham> firstRow = slice(loadedProducts, 10);
                Collections.shuffle(loadedProducts);
                ArrayList<SanPham> secondRow = slice(loadedProducts, 8);
                mainHandler.post(() -> {
                    categories.clear();
                    categories.addAll(loadedCategories);
                    categoryAdapter.notifyDataSetChanged();

                    gridProducts.clear();
                    gridProducts.addAll(firstGrid);
                    gridProductAdapter.notifyDataSetChanged();

                    rowProducts1.clear();
                    rowProducts1.addAll(firstRow);
                    rowAdapter1.notifyDataSetChanged();

                    rowProducts2.clear();
                    rowProducts2.addAll(secondRow);
                    rowAdapter2.notifyDataSetChanged();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Khong tai duoc du lieu backend", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private ArrayList<SanPham> slice(ArrayList<SanPham> products, int limit) {
        if (products.size() <= limit) {
            return new ArrayList<>(products);
        }
        return new ArrayList<>(products.subList(0, limit));
    }

    private void openCategory(AdapterView<?> parent, View view, int position, long id) {
        NhomSanPham category = categories.get(position);
        Intent intent = new Intent(this, DanhMucSanPham_Activity.class);
        intent.putExtra("nhomSpId", category.getMa());
        intent.putExtra("tendn", tendn);
        startActivity(intent);
    }

    private void openProductDetail(SanPham product) {
        ChiTietSanPham detail = new ChiTietSanPham(
                product.getMasp(),
                product.getTensp(),
                product.getDongia(),
                product.getMota(),
                product.getGhichu(),
                product.getSoluongkho(),
                product.getMansp(),
                product.getImageUrl()
        );
        Intent intent = new Intent(this, ChiTietSanPham_Activity.class);
        intent.putExtra("chitietsanpham", detail);
        intent.putExtra("tendn", tendn);
        startActivity(intent);
    }

    private void openWithUser(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("tendn", tendn);
        startActivity(intent);
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
