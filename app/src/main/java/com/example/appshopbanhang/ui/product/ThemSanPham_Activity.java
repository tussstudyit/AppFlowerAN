package com.example.appshopbanhang.ui.product;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.NhomSanPham;
import com.example.appshopbanhang.data.repository.SanPhamRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThemSanPham_Activity extends AppCompatActivity {
    private EditText tensp;
    private EditText dongia;
    private EditText mota;
    private EditText ghichu;
    private EditText soluongkho;
    private Spinner mansp;
    private ImageView imgsp;
    private Uri imageUri;
    private ArrayList<NhomSanPham> categories = new ArrayList<>();
    private SanPhamRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_san_pham);

        repository = new SanPhamRepository(this);
        tensp = findViewById(R.id.tensp);
        imgsp = findViewById(R.id.imgsp);
        mota = findViewById(R.id.mota);
        ghichu = findViewById(R.id.ghichu);
        dongia = findViewById(R.id.dongia);
        soluongkho = findViewById(R.id.soluongkho);
        mansp = findViewById(R.id.spn);
        Button chonimgbs = findViewById(R.id.btnAddImg);
        Button btnthem = findViewById(R.id.btnadd);

        chonimgbs.setOnClickListener(view -> openDrawableImagePicker());
        btnthem.setOnClickListener(view -> addSanPham());
        loadCategories();
    }

    private void loadCategories() {
        executor.execute(() -> {
            try {
                ArrayList<NhomSanPham> result = repository.getAllCategories();
                mainHandler.post(() -> {
                    categories = result;
                    ArrayAdapter<NhomSanPham> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mansp.setAdapter(adapter);
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Khong tai duoc nhom san pham", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void addSanPham() {
        String name = tensp.getText().toString().trim();
        String description = mota.getText().toString().trim();
        String note = ghichu.getText().toString().trim();
        String priceText = dongia.getText().toString().trim();
        String stockText = soluongkho.getText().toString().trim();
        if (name.isEmpty() || description.isEmpty() || note.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            Toast.makeText(this, "Vui long dien day du thong tin", Toast.LENGTH_SHORT).show();
            return;
        }
        NhomSanPham category = mansp.getSelectedItem() instanceof NhomSanPham ? (NhomSanPham) mansp.getSelectedItem() : null;
        byte[] imageBytes = imageUri == null ? null : getBytesFromUri(imageUri);
        float price = MoneyFormatter.parseVndInput(priceText);
        int stock = Integer.parseInt(stockText);
        String categoryId = category == null ? null : category.getMa();

        executor.execute(() -> {
            try {
                repository.saveProduct(null, name, price, description, note, stock, categoryId, imageBytes);
                mainHandler.post(() -> {
                    Toast.makeText(this, "Them san pham thanh cong", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Sanpham_admin_Activity.class));
                    finish();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Khong them duoc san pham", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void openDrawableImagePicker() {
        final String[] imageNames = {"img_1", "img_2", "img_3", "img_4", "img_5", "img_6", "img_7", "img_8", "img_9", "img_10", "img_11", "img_12", "img_13", "img_14", "img_15", "img_16", "img_17", "img_18", "img_19", "img_20", "img_21", "img_22", "img_23", "img_24", "img_25", "img_26", "img_27", "img_28", "img_29", "img_30"};
        new AlertDialog.Builder(this)
                .setTitle("Chon anh")
                .setItems(imageNames, (dialog, which) -> {
                    int resourceId = getResources().getIdentifier(imageNames[which], "drawable", getPackageName());
                    imgsp.setImageResource(resourceId);
                    imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
                })
                .show();
    }

    private byte[] getBytesFromUri(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                return null;
            }
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        } catch (Exception exception) {
            return null;
        }
    }
}
