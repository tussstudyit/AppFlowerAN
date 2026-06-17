package com.example.appshopbanhang.ui.adapter;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.image.HinhAnhUtils;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.SanPham;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SanPham_Moi_Adapter extends RecyclerView.Adapter<SanPham_Moi_Adapter.SanPhamViewHolder> {

    private List<SanPham> productList;
    private OnItemClickListener listener;

    // Interface to handle click events
    public interface OnItemClickListener {
        void onItemClick(SanPham product);
    }

    // Constructor with listener parameter
    public SanPham_Moi_Adapter(List<SanPham> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SanPhamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_sp_moi, parent, false);
        return new SanPhamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SanPhamViewHolder holder, int position) {
        SanPham product = productList.get(position);

        // Display data
        holder.tensp.setText(product.getTensp());
        holder.mota.setText(product.getMota());
        holder.giatien.setText(MoneyFormatter.format(product.getDongia())); // Convert float to String
        holder.soluongkho.setText(String.valueOf(product.getSoluongkho())); // Convert float to String
        holder.ghichu.setText(product.getGhichu());
        holder.manhomsp.setText(product.getMansp()); // Convert float to String
        holder.masp.setText(product.getMasp()); // Convert float to String
        HinhAnhUtils.loadUrl(holder.img, product.getImageUrl(), R.drawable.logo1);

// Hiển thị sao ngẫu nhiên từ 4.2 đến 4.9
        float randomSao = 4.2f + new java.util.Random().nextFloat() * (4.9f - 4.2f);
        holder.sao.setText(String.format("%.1f", randomSao));
        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class SanPhamViewHolder extends RecyclerView.ViewHolder {
        TextView tensp, ghichu, mota, giatien,masp,manhomsp,soluongkho,sao;
        ImageView img;

        SanPhamViewHolder(View itemView) {
            super(itemView);
            tensp = itemView.findViewById(R.id.tensp);
            masp = itemView.findViewById(R.id.masp);
            manhomsp = itemView.findViewById(R.id.manhomsanpham);
            soluongkho = itemView.findViewById(R.id.soluongkho);
            ghichu = itemView.findViewById(R.id.ghichu);
            mota = itemView.findViewById(R.id.mota);
            giatien = itemView.findViewById(R.id.dongia);
            sao=itemView.findViewById(R.id.sao);
            img = itemView.findViewById(R.id.imgsp);
        }
    }
}

