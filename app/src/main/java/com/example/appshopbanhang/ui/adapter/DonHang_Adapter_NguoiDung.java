package com.example.appshopbanhang.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.DonHang;
import com.example.appshopbanhang.ui.order.ChiTietDonHang_NguoiDung_3_TrangThai_Khac;
import com.example.appshopbanhang.ui.order.ChiTietDonHang_NguoiDung_Activity;

import java.util.List;

public class DonHang_Adapter_NguoiDung extends ArrayAdapter<DonHang> {
    public DonHang_Adapter_NguoiDung(Context context, List<DonHang> orders) {
        super(context, 0, orders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_donhang_nguoidung, parent, false);
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
        ImageButton next = convertView.findViewById(R.id.imgnext);

        txtTenKh.setText(order.getTenKh());
        txtDiaChi.setText(order.getDiaChi());
        txtSdt.setText(order.getSdt());
        txtTongThanhToan.setText(MoneyFormatter.format(order.getTongTien()));
        txtNgayDatHang.setText(order.getNgayDatHang());
        txtTrangthai.setText(order.getTrangthai());
        txtMadh.setText(String.valueOf(order.getId()));

        next.setOnClickListener(view -> {
            Intent intent = "Da Giao Hang".equals(order.getTrangthai())
                    ? new Intent(getContext(), ChiTietDonHang_NguoiDung_Activity.class)
                    : new Intent(getContext(), ChiTietDonHang_NguoiDung_3_TrangThai_Khac.class);
            intent.putExtra("donHangId", String.valueOf(order.getId()));
            getContext().startActivity(intent);
        });
        setStatusTextStyle(txtTrangthai, order.getTrangthai());
        return convertView;
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
