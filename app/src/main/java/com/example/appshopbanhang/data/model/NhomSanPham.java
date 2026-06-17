package com.example.appshopbanhang.data.model;

public class NhomSanPham {

    String ma;
    String tennhom;
    String imageUrl;

    public NhomSanPham(String ma, String tennhom, String imageUrl) {
        this.ma = ma;
        this.tennhom = tennhom;
        this.imageUrl = imageUrl;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTennhom() {
        return tennhom;
    }

    public void setTennhom(String tennhom) {
        this.tennhom = tennhom;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String toString() {
        return tennhom; // Hiển thị tên nhóm sản phẩm
    }
}

