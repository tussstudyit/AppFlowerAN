package com.example.appshopbanhang.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.image.HinhAnhUtils;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.ChiTietSanPham;
import com.example.appshopbanhang.data.model.NhomSanPham;
import com.example.appshopbanhang.data.model.SanPham;
import com.example.appshopbanhang.data.repository.SanPhamRepository;
import com.example.appshopbanhang.ui.product.ChiTietSanPham_Activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SanPhamAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<SanPham> spList;
    private final boolean showFullDetails;
    private final SanPhamRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Uri selectedImageUri;
    private ArrayList<NhomSanPham> categoryList = new ArrayList<>();

    public SanPhamAdapter(Context context, ArrayList<SanPham> spList, boolean showFullDetails) {
        this.context = context;
        this.spList = spList;
        this.showFullDetails = showFullDetails;
        this.repository = new SanPhamRepository(context);
    }

    @Override
    public int getCount() {
        return spList.size();
    }

    @Override
    public Object getItem(int position) {
        return spList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return showFullDetails
                ? getAdminView(position, convertView, parent)
                : getCustomerView(position, convertView, parent);
    }

    private View getAdminView(int position, View convertView, ViewGroup parent) {
        View row = convertView == null
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_sanpham, parent, false)
                : convertView;

        SanPham product = spList.get(position);
        bindProduct(row, product);
        ImageView anh = row.findViewById(R.id.imgsp);
        ImageButton sua = row.findViewById(R.id.imgsua);
        ImageButton xoa = row.findViewById(R.id.imgxoa);
        HinhAnhUtils.loadUrl(anh, product.getImageUrl(), R.drawable.vest);

        sua.setOnClickListener(view -> showEditDialog(product));
        xoa.setOnClickListener(view -> confirmDelete(product, position));
        return row;
    }

    private View getCustomerView(int position, View convertView, ViewGroup parent) {
        View row = convertView == null
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_hienthi_gridview1_nguoidung, parent, false)
                : convertView;

        SanPham product = spList.get(position);
        bindProduct(row, product);
        ImageView anh = row.findViewById(R.id.imgsp);
        TextView sao = row.findViewById(R.id.sao);
        if (sao != null) {
            float randomSao = 4.2f + new java.util.Random().nextFloat() * (4.9f - 4.2f);
            sao.setText(String.format("%.1f", randomSao));
        }
        HinhAnhUtils.loadUrl(anh, product.getImageUrl(), R.drawable.vest);
        row.setOnClickListener(v -> openProductDetail(parent, product));
        return row;
    }

    private void bindProduct(View row, SanPham product) {
        ((TextView) row.findViewById(R.id.masp)).setText(product.getMasp());
        ((TextView) row.findViewById(R.id.tensp)).setText(product.getTensp());
        ((TextView) row.findViewById(R.id.dongia)).setText(MoneyFormatter.format(product.getDongia()));
        ((TextView) row.findViewById(R.id.mota)).setText(product.getMota());
        ((TextView) row.findViewById(R.id.ghichu)).setText(product.getGhichu());
        ((TextView) row.findViewById(R.id.soluongkho)).setText(String.valueOf(product.getSoluongkho()));
        ((TextView) row.findViewById(R.id.manhomsanpham)).setText(product.getMansp());
    }

    private void openProductDetail(ViewGroup parent, SanPham product) {
        Intent intent = new Intent(parent.getContext(), ChiTietSanPham_Activity.class);
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
        intent.putExtra("chitietsanpham", detail);
        parent.getContext().startActivity(intent);
    }

    private void confirmDelete(SanPham product, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xac nhan")
                .setMessage("Ban co chac chan muon xoa san pham nay?")
                .setPositiveButton("Co", (dialog, which) -> executor.execute(() -> {
                    try {
                        repository.deleteProduct(product.getMasp());
                        mainHandler.post(() -> {
                            spList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Xoa san pham thanh cong", Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception exception) {
                        mainHandler.post(() -> Toast.makeText(context, "Khong xoa duoc san pham", Toast.LENGTH_SHORT).show());
                    }
                }))
                .setNegativeButton("Khong", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditDialog(SanPham product) {
        selectedImageUri = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_sua_san_pham, null);
        builder.setView(dialogView);

        EditText editMasp = dialogView.findViewById(R.id.masp);
        EditText editTensp = dialogView.findViewById(R.id.tensp);
        EditText editDongia = dialogView.findViewById(R.id.dongia);
        EditText editMota = dialogView.findViewById(R.id.mota);
        EditText editGhichu = dialogView.findViewById(R.id.ghichu);
        EditText editSoluongkho = dialogView.findViewById(R.id.soluongkho);
        Spinner mansp = dialogView.findViewById(R.id.manhomsanpham);
        ImageView imgsp = dialogView.findViewById(R.id.imgsp);
        Button imgAddanh = dialogView.findViewById(R.id.btnAddImg);

        editMasp.setText(product.getMasp());
        editTensp.setText(product.getTensp());
        editDongia.setText(MoneyFormatter.toPlainVnd(product.getDongia()));
        editMota.setText(product.getMota());
        editGhichu.setText(product.getGhichu());
        editSoluongkho.setText(String.valueOf(product.getSoluongkho()));
        HinhAnhUtils.loadUrl(imgsp, product.getImageUrl(), R.drawable.vest);
        loadCategories(mansp, product.getMansp());
        imgAddanh.setOnClickListener(v -> openDrawableImagePicker(imgsp));

        builder.setPositiveButton("Luu", (dialog, which) -> updateProduct(product, editTensp, editDongia, editMota, editGhichu, editSoluongkho, mansp));
        builder.setNegativeButton("Huy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateProduct(
            SanPham product,
            EditText editTensp,
            EditText editDongia,
            EditText editMota,
            EditText editGhichu,
            EditText editSoluongkho,
            Spinner categorySpinner
    ) {
        String name = editTensp.getText().toString().trim();
        String description = editMota.getText().toString().trim();
        String note = editGhichu.getText().toString().trim();
        float price = MoneyFormatter.parseVndInput(editDongia.getText().toString().trim());
        int stock = Integer.parseInt(editSoluongkho.getText().toString().trim());
        NhomSanPham selectedCategory = categorySpinner.getSelectedItem() instanceof NhomSanPham
                ? (NhomSanPham) categorySpinner.getSelectedItem()
                : null;
        String categoryId = selectedCategory == null ? product.getMansp() : selectedCategory.getMa();
        byte[] imageBytes = selectedImageUri == null ? null : getBytesFromUri(selectedImageUri);

        executor.execute(() -> {
            try {
                SanPham updated = repository.saveProduct(product.getMasp(), name, price, description, note, stock, categoryId, imageBytes);
                mainHandler.post(() -> {
                    product.setTensp(updated.getTensp());
                    product.setDongia(updated.getDongia());
                    product.setMota(updated.getMota());
                    product.setGhichu(updated.getGhichu());
                    product.setSoluongkho(updated.getSoluongkho());
                    product.setMansp(updated.getMansp());
                    product.setImageUrl(updated.getImageUrl());
                    notifyDataSetChanged();
                    Toast.makeText(context, "Cap nhat san pham thanh cong", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(context, "Khong cap nhat duoc san pham", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadCategories(Spinner spinner, String selectedId) {
        executor.execute(() -> {
            try {
                ArrayList<NhomSanPham> categories = repository.getAllCategories();
                mainHandler.post(() -> {
                    categoryList = categories;
                    ArrayAdapter<NhomSanPham> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getMa().equals(selectedId)) {
                            spinner.setSelection(i);
                            break;
                        }
                    }
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(context, "Khong tai duoc nhom san pham", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void openDrawableImagePicker(ImageView imageView) {
        final String[] imageNames = {"img_1", "img_2", "img_3", "img_4", "img_5", "img_6", "img_7", "img_8", "img_9", "img_10", "img_11", "img_12", "img_13", "img_14", "img_15", "img_16", "img_17", "img_18", "img_19", "img_20", "img_21", "img_22", "img_23", "img_24", "img_25", "img_26", "img_27", "img_28", "img_29", "img_30"};
        new AlertDialog.Builder(context)
                .setTitle("Chon anh")
                .setItems(imageNames, (dialog, which) -> {
                    int resourceId = context.getResources().getIdentifier(imageNames[which], "drawable", context.getPackageName());
                    imageView.setImageResource(resourceId);
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
