package com.example.appshopbanhang.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChiTietDonHang implements Parcelable {
    private int id_chitiet;
    private int id_dathang;
    private int masp;
    private String tenSanPham;
    private int soLuong;
    private float donGia;
    private String imageUrl;

    public ChiTietDonHang(int id_chitiet, int id_dathang, int masp, String tenSanPham, int soLuong, float donGia, String imageUrl) {
        this.id_chitiet = id_chitiet;
        this.id_dathang = id_dathang;
        this.masp = masp;
        this.tenSanPham = tenSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.imageUrl = imageUrl;
    }

    protected ChiTietDonHang(Parcel in) {
        id_chitiet = in.readInt();
        id_dathang = in.readInt();
        masp = in.readInt();
        tenSanPham = in.readString();
        soLuong = in.readInt();
        donGia = in.readFloat();
        imageUrl = in.readString();
    }

    public static final Creator<ChiTietDonHang> CREATOR = new Creator<ChiTietDonHang>() {
        @Override
        public ChiTietDonHang createFromParcel(Parcel in) {
            return new ChiTietDonHang(in);
        }

        @Override
        public ChiTietDonHang[] newArray(int size) {
            return new ChiTietDonHang[size];
        }
    };

    public int getId_chitiet() {
        return id_chitiet;
    }

    public void setId_chitiet(int id_chitiet) {
        this.id_chitiet = id_chitiet;
    }

    public int getId_dathang() {
        return id_dathang;
    }

    public void setId_dathang(int id_dathang) {
        this.id_dathang = id_dathang;
    }

    public int getMasp() {
        return masp;
    }

    public void setMasp(int masp) {
        this.masp = masp;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public float getDonGia() {
        return donGia;
    }

    public void setDonGia(float donGia) {
        this.donGia = donGia;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_chitiet);
        dest.writeInt(id_dathang);
        dest.writeInt(masp);
        dest.writeString(tenSanPham);
        dest.writeInt(soLuong);
        dest.writeFloat(donGia);
        dest.writeString(imageUrl);
    }
}
