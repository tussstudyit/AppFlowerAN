package com.example.appshopbanhang.ui.category;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.repository.SanPhamRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThemNhomSanPham_Activity extends AppCompatActivity {
    private EditText tennsp;
    private ImageView imgnsp;
    private Uri imageUri;
    private SanPhamRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhom_san_pham);

        repository = new SanPhamRepository(this);
        tennsp = findViewById(R.id.ten);
        imgnsp = findViewById(R.id.imgnsp);
        Button chonimgbs = findViewById(R.id.btnAddImg);
        Button btnthem = findViewById(R.id.btnadd);

        chonimgbs.setOnClickListener(view -> openDrawableImagePicker());
        btnthem.setOnClickListener(view -> addCategory());
    }

    private void addCategory() {
        String name = tennsp.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui long dien day du thong tin", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] imageBytes = imageUri == null ? null : getBytesFromUri(imageUri);
        executor.execute(() -> {
            try {
                repository.saveCategory(null, name, imageBytes);
                mainHandler.post(() -> {
                    Toast.makeText(this, "Them nhom san pham thanh cong", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Nhomsanpham_admin_Actvity.class));
                    finish();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Khong them duoc nhom san pham", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void openDrawableImagePicker() {
        final String[] imageNames = {"hoacamchuong", "hoacamtucau", "hoahong", "hoahuongduong", "hoaly", "hoanhi", "hoanhixanh", "hoarambut", "hoasen", "hoatulip"};
        new AlertDialog.Builder(this)
                .setTitle("Chon anh")
                .setItems(imageNames, (dialog, which) -> {
                    int resourceId = getResources().getIdentifier(imageNames[which], "drawable", getPackageName());
                    imgnsp.setImageResource(resourceId);
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
