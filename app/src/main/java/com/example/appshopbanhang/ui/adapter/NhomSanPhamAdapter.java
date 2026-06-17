package com.example.appshopbanhang.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.image.HinhAnhUtils;
import com.example.appshopbanhang.data.model.NhomSanPham;
import com.example.appshopbanhang.data.repository.SanPhamRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NhomSanPhamAdapter extends BaseAdapter {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private final Context context;
    private final ArrayList<NhomSanPham> nhomSanPhamList;
    private final boolean showFullDetails;
    private final SanPhamRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Uri selectedImageUri;

    public NhomSanPhamAdapter(Activity context, ArrayList<NhomSanPham> nhomSanPhamList, boolean showFullDetails) {
        this.context = context;
        this.nhomSanPhamList = nhomSanPhamList;
        this.showFullDetails = showFullDetails;
        this.repository = new SanPhamRepository(context);
    }

    @Override
    public int getCount() {
        return nhomSanPhamList.size();
    }

    @Override
    public Object getItem(int position) {
        return nhomSanPhamList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return showFullDetails ? getViewTrue(position, convertView, parent) : getViewFalse(position, convertView, parent);
    }

    private View getViewTrue(int position, View convertView, ViewGroup parent) {
        View view = convertView == null
                ? LayoutInflater.from(context).inflate(R.layout.ds_nhomsanpham, parent, false)
                : convertView;

        NhomSanPham category = nhomSanPhamList.get(position);
        TextView ten = view.findViewById(R.id.ten);
        TextView id = view.findViewById(R.id.idma);
        ImageView anh = view.findViewById(R.id.imgnsp);
        ImageButton xoa = view.findViewById(R.id.imgxoa);
        ImageButton sua = view.findViewById(R.id.imgsua);

        id.setText(category.getMa());
        ten.setText(category.getTennhom());
        HinhAnhUtils.loadUrl(anh, category.getImageUrl(), R.drawable.vest);

        sua.setOnClickListener(v -> showEditDialog(category));
        xoa.setOnClickListener(v -> confirmDelete(category, position));

        return view;
    }

    private View getViewFalse(int position, View convertView, ViewGroup parent) {
        View view = convertView == null
                ? LayoutInflater.from(context).inflate(R.layout.ds_hienthi_gridview2_nguoidung, parent, false)
                : convertView;

        NhomSanPham category = nhomSanPhamList.get(position);
        TextView ten = view.findViewById(R.id.ten);
        TextView id = view.findViewById(R.id.idma);
        ImageView anh = view.findViewById(R.id.imgnsp);

        id.setText(category.getMa());
        ten.setText(category.getTennhom());
        HinhAnhUtils.loadUrl(anh, category.getImageUrl(), R.drawable.vest);

        return view;
    }

    private void showEditDialog(NhomSanPham category) {
        selectedImageUri = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_sua_nhomsanpham, null);
        builder.setView(dialogView);

        EditText editTen = dialogView.findViewById(R.id.ten);
        ImageView imgPreview = dialogView.findViewById(R.id.imgnsp);
        Button chonanh = dialogView.findViewById(R.id.btnAddImg);

        editTen.setText(category.getTennhom());
        HinhAnhUtils.loadUrl(imgPreview, category.getImageUrl(), R.drawable.tc);
        chonanh.setOnClickListener(view -> openDrawableImagePicker(imgPreview));
        imgPreview.setOnClickListener(imgView -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        });

        builder.setPositiveButton("Luu", (dialog, which) -> {
            String newName = editTen.getText().toString().trim();
            byte[] imageBytes = selectedImageUri == null ? null : getBytesFromUri(selectedImageUri);
            executor.execute(() -> {
                try {
                    NhomSanPham updated = repository.saveCategory(category.getMa(), newName, imageBytes);
                    mainHandler.post(() -> {
                        category.setTennhom(updated.getTennhom());
                        category.setImageUrl(updated.getImageUrl());
                        notifyDataSetChanged();
                        Toast.makeText(context, "Cap nhat nhom san pham thanh cong", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception exception) {
                    mainHandler.post(() -> Toast.makeText(context, "Khong cap nhat duoc nhom san pham", Toast.LENGTH_SHORT).show());
                }
            });
        });
        builder.setNegativeButton("Huy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void confirmDelete(NhomSanPham category, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xac nhan")
                .setMessage("Ban co chac chan muon xoa nhom san pham nay?")
                .setPositiveButton("Co", (dialog, which) -> executor.execute(() -> {
                    try {
                        repository.deleteCategory(category.getMa());
                        mainHandler.post(() -> {
                            nhomSanPhamList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Da xoa nhom san pham", Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception exception) {
                        mainHandler.post(() -> Toast.makeText(context, "Khong xoa duoc nhom san pham", Toast.LENGTH_SHORT).show());
                    }
                }))
                .setNegativeButton("Khong", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openDrawableImagePicker(ImageView imgnsp) {
        final String[] imageNames = {"hoacamchuong", "hoacamtucau", "hoahong", "hoahuongduong", "hoaly", "hoanhi", "hoanhixanh", "hoarambut", "hoasen", "hoatulip"};
        new AlertDialog.Builder(context)
                .setTitle("Chon anh")
                .setItems(imageNames, (dialog, which) -> {
                    String selectedImageName = imageNames[which];
                    int resourceId = context.getResources().getIdentifier(selectedImageName, "drawable", context.getPackageName());
                    imgnsp.setImageResource(resourceId);
                    selectedImageUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
                })
                .show();
    }

    private byte[] getBytesFromUri(Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
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
