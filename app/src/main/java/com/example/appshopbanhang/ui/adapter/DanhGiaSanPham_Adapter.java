package com.example.appshopbanhang.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.model.DanhGiaSanPham;

import java.util.ArrayList;

public class DanhGiaSanPham_Adapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<DanhGiaSanPham> reviews;
    private final boolean showFullDetails;

    public DanhGiaSanPham_Adapter(Context context, ArrayList<DanhGiaSanPham> reviews, boolean showFullDetails) {
        this.context = context;
        this.reviews = reviews;
        this.showFullDetails = showFullDetails;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!showFullDetails) {
            return convertView == null
                    ? LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_hienthi_gridview1_nguoidung, parent, false)
                    : convertView;
        }

        View row = convertView == null
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_danhgiasanpham, parent, false)
                : convertView;

        DanhGiaSanPham review = reviews.get(position);
        ((TextView) row.findViewById(R.id.masp)).setText(review.getMasp());
        ((TextView) row.findViewById(R.id.id_chitiet)).setText(review.getId_chitiet());
        ((TextView) row.findViewById(R.id.id_danhgia)).setText(review.getId_danhgia());
        ((EditText) row.findViewById(R.id.txtNoidungdanhgia)).setText(review.getNoidung());
        ((TextView) row.findViewById(R.id.tenkhachhang)).setText(review.getTendn());

        setStarImage(row.findViewById(R.id.star1), review.getSao1());
        setStarImage(row.findViewById(R.id.star2), review.getSao2());
        setStarImage(row.findViewById(R.id.star3), review.getSao3());
        setStarImage(row.findViewById(R.id.star4), review.getSao4());
        setStarImage(row.findViewById(R.id.star5), review.getSao5());
        return row;
    }

    private void setStarImage(ImageButton starButton, String saoName) {
        int resourceId = context.getResources().getIdentifier(saoName, "drawable", context.getPackageName());
        starButton.setImageResource(resourceId == 0 ? R.drawable.star2 : resourceId);
    }
}
