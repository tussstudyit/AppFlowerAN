package com.example.appshopbanhang.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.image.HinhAnhUtils;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.ChiTietDonHang;

import java.util.List;

public class ChiTietDonHang_Adapter_3TrangThai_NguoiDung extends ArrayAdapter<ChiTietDonHang> {
    public ChiTietDonHang_Adapter_3TrangThai_NguoiDung(Context context, List<ChiTietDonHang> details, String tendn) {
        super(context, 0, details);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChiTietDonHang detail = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_chitietdonhang_nguoidung_3_trangthaikhac, parent, false);
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

        tvID_dathang.setText(String.valueOf(detail.getId_dathang()));
        tvMaSp.setText(String.valueOf(detail.getMasp()));
        tvTenSp.setText(detail.getTenSanPham() == null ? "" : detail.getTenSanPham());
        tvSoLuong.setText(String.valueOf(detail.getSoLuong()));
        tvDonGia.setText(MoneyFormatter.format(detail.getDonGia()));
        HinhAnhUtils.loadUrl(ivAnh, detail.getImageUrl(), R.drawable.vest);

        return convertView;
    }
}
