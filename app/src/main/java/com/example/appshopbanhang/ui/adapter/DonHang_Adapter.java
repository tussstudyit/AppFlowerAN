package com.example.appshopbanhang.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.DonHang;
import com.example.appshopbanhang.data.repository.DonHangRepository;
import com.example.appshopbanhang.ui.order.ChiTietDonHang_Admin_Activity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DonHang_Adapter extends ArrayAdapter<DonHang> {
    private final DonHangRepository orderRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public DonHang_Adapter(Context context, List<DonHang> orders) {
        super(context, 0, orders);
        this.orderRepository = new DonHangRepository(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_donhang, parent, false);
        }

        DonHang order = getItem(position);
        if (order == null) {
            return convertView;
        }

        TextView txtMadh = convertView.findViewById(R.id.txtMahd);
        TextView txtTenKh = convertView.findViewById(R.id.txtTenKh);
        TextView txtDiaChi = convertView.findViewById(R.id.txtDiaChi);
        TextView txtSdt = convertView.findViewById(R.id.txtSdt);
        TextView txtTongThanhToan = convertView.findViewById(R.id.txtTongThanhToan);
        TextView txtNgayDatHang = convertView.findViewById(R.id.txtNgayDatHang);
        TextView txtTrangthai = convertView.findViewById(R.id.txtTrangthai);
        ImageButton sua = convertView.findViewById(R.id.imgsua);
        ImageButton next = convertView.findViewById(R.id.imgnext);

        txtTenKh.setText(order.getTenKh());
        txtDiaChi.setText(order.getDiaChi());
        txtSdt.setText(order.getSdt());
        txtTongThanhToan.setText(MoneyFormatter.format(order.getTongTien()));
        txtNgayDatHang.setText(order.getNgayDatHang());
        txtTrangthai.setText(order.getTrangthai());
        txtMadh.setText(String.valueOf(order.getId()));

        next.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ChiTietDonHang_Admin_Activity.class);
            intent.putExtra("donHangId", String.valueOf(order.getId()));
            getContext().startActivity(intent);
        });
        sua.setOnClickListener(view -> showStatusDialog(order));
        setStatusTextStyle(txtTrangthai, order.getTrangthai());
        return convertView;
    }

    private void showStatusDialog(DonHang order) {
        String[] statuses = {
                "Cho Thanh Toan",
                "Cho Xac Nhan",
                "Dang Chuan Bi Hang",
                "Da Giao Cho DVVC",
                "Da Giao Hang",
                "Da Huy"
        };
        new AlertDialog.Builder(getContext())
                .setTitle("Chon trang thai")
                .setItems(statuses, (dialog, which) -> updateStatus(order, statuses[which]))
                .show();
    }

    private void updateStatus(DonHang order, String status) {
        executor.execute(() -> {
            try {
                DonHang updated = orderRepository.updateStatus(order.getId(), status);
                mainHandler.post(() -> {
                    order.setTrangthai(updated.getTrangthai());
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "Da cap nhat trang thai", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(getContext(), "Khong cap nhat duoc trang thai", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setStatusTextStyle(TextView textView, String status) {
        if ("Da Huy".equals(status)) {
            textView.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_light));
        } else if ("Dang Chuan Bi Hang".equals(status) || "Da Giao Hang".equals(status)) {
            textView.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if ("Cho Xac Nhan".equals(status) || "Cho Thanh Toan".equals(status)) {
            textView.setTextColor(getContext().getResources().getColor(android.R.color.holo_purple));
        } else {
            textView.setTextColor(getContext().getResources().getColor(android.R.color.black));
        }
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
    }
}
