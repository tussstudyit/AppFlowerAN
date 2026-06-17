package com.example.appshopbanhang.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.image.HinhAnhUtils;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.ChiTietDonHang;
import com.example.appshopbanhang.data.model.DanhGiaSanPham;
import com.example.appshopbanhang.data.repository.DanhGiaRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChiTietDonHangAdapter_NguoiDung extends ArrayAdapter<ChiTietDonHang> {
    private final String tendn;
    private final DanhGiaRepository reviewRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ChiTietDonHangAdapter_NguoiDung(Context context, List<ChiTietDonHang> details, String tendn) {
        super(context, 0, details);
        this.tendn = tendn;
        this.reviewRepository = new DanhGiaRepository(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChiTietDonHang detail = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_chitietdonhang_nguoidung, parent, false);
        }
        if (detail == null) {
            return convertView;
        }

        TextView tvID_dathang = convertView.findViewById(R.id.txt_Iddathang);
        TextView tvMaSp = convertView.findViewById(R.id.txtMasp);
        TextView tvTenSp = convertView.findViewById(R.id.txtTensp);
        TextView tvSoLuong = convertView.findViewById(R.id.txtSoLuong);
        TextView tvDonGia = convertView.findViewById(R.id.txtGia);
        ImageView ivAnh = convertView.findViewById(R.id.imgsp);
        Button btndanhgia = convertView.findViewById(R.id.btndanhgia);
        TextView txtDanhgia = convertView.findViewById(R.id.textdanhgia);

        tvID_dathang.setText(String.valueOf(detail.getId_dathang()));
        tvMaSp.setText(String.valueOf(detail.getMasp()));
        tvTenSp.setText(detail.getTenSanPham() == null ? "" : detail.getTenSanPham());
        tvSoLuong.setText(String.valueOf(detail.getSoLuong()));
        tvDonGia.setText(MoneyFormatter.format(detail.getDonGia()));
        HinhAnhUtils.loadUrl(ivAnh, detail.getImageUrl(), R.drawable.vest);

        txtDanhgia.setVisibility(View.GONE);
        btndanhgia.setVisibility(View.VISIBLE);
        btndanhgia.setOnClickListener(view -> showRatingDialog(detail.getMasp(), detail.getId_chitiet(), () -> {
            btndanhgia.setVisibility(View.GONE);
            txtDanhgia.setVisibility(View.VISIBLE);
        }));

        return convertView;
    }

    private void showRatingDialog(int masp, int idChitiet, RatingSubmissionListener listener) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.activity_danhgia_sanpham);

        ImageButton sao1 = dialog.findViewById(R.id.star1);
        ImageButton sao2 = dialog.findViewById(R.id.star2);
        ImageButton sao3 = dialog.findViewById(R.id.star3);
        ImageButton sao4 = dialog.findViewById(R.id.star4);
        ImageButton sao5 = dialog.findViewById(R.id.star5);
        EditText edtReview = dialog.findViewById(R.id.txtNoidungdanhgia);
        Button btnSubmit = dialog.findViewById(R.id.btnSave);

        final int[] starCount = {0};
        ImageButton[] stars = {sao1, sao2, sao3, sao4, sao5};
        for (int i = 0; i < stars.length; i++) {
            final int index = i;
            stars[i].setOnClickListener(v -> {
                setStarRating(stars, index);
                starCount[0] = index + 1;
            });
            stars[i].setOnLongClickListener(v -> {
                resetStarRating(stars, index);
                if (starCount[0] > index) {
                    starCount[0]--;
                }
                return true;
            });
        }

        btnSubmit.setOnClickListener(v -> {
            String content = edtReview.getText().toString().trim();
            if (starCount[0] == 0) {
                Toast.makeText(getContext(), "Vui long chon it nhat mot sao.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Vui long nhap noi dung danh gia.", Toast.LENGTH_SHORT).show();
                return;
            }

            DanhGiaSanPham review = new DanhGiaSanPham(
                    null,
                    String.valueOf(masp),
                    String.valueOf(idChitiet),
                    content,
                    tendn,
                    starCount[0] >= 1 ? "star2" : "star1",
                    starCount[0] >= 2 ? "star2" : "star1",
                    starCount[0] >= 3 ? "star2" : "star1",
                    starCount[0] >= 4 ? "star2" : "star1",
                    starCount[0] >= 5 ? "star2" : "star1"
            );

            executor.execute(() -> {
                try {
                    reviewRepository.createReview(review);
                    mainHandler.post(() -> {
                        Toast.makeText(getContext(), "Danh gia thanh cong", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onRatingSubmitted();
                        }
                    });
                } catch (Exception exception) {
                    mainHandler.post(() -> Toast.makeText(getContext(), "Khong gui duoc danh gia", Toast.LENGTH_SHORT).show());
                }
            });
        });

        dialog.show();
    }

    private void setStarRating(ImageButton[] stars, int index) {
        for (int i = 0; i <= index; i++) {
            stars[i].setImageResource(R.drawable.star2);
        }
        for (int i = index + 1; i < stars.length; i++) {
            stars[i].setImageResource(R.drawable.star1);
        }
    }

    private void resetStarRating(ImageButton[] stars, int index) {
        stars[index].setImageResource(R.drawable.star1);
    }

    public interface RatingSubmissionListener {
        void onRatingSubmitted();
    }
}
