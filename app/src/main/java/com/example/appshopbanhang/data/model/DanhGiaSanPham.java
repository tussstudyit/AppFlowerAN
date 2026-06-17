package com.example.appshopbanhang.data.model;

public class DanhGiaSanPham {
    String id_danhgia,masp,id_chitiet,noidung,tendn,sao1,sao2,sao3,sao4,sao5;

    public DanhGiaSanPham(String id_danhgia, String masp, String id_chitiet, String noidung, String tendn, String sao1, String sao2, String sao3, String sao4, String sao5) {
        this.id_danhgia = id_danhgia;
        this.masp = masp;
        this.id_chitiet = id_chitiet;
        this.noidung = noidung;
        this.tendn = tendn;
        this.sao1 = sao1;
        this.sao2 = sao2;
        this.sao3 = sao3;
        this.sao4 = sao4;
        this.sao5 = sao5;
    }

    public String getId_danhgia() {
        return id_danhgia;
    }

    public void setId_danhgia(String id_danhgia) {
        this.id_danhgia = id_danhgia;
    }

    public String getMasp() {
        return masp;
    }

    public void setMasp(String masp) {
        this.masp = masp;
    }

    public String getId_chitiet() {
        return id_chitiet;
    }

    public void setId_chitiet(String id_chitiet) {
        this.id_chitiet = id_chitiet;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public String getTendn() {
        return tendn;
    }

    public void setTendn(String tendn) {
        this.tendn = tendn;
    }

    public String getSao1() {
        return sao1;
    }

    public void setSao1(String sao1) {
        this.sao1 = sao1;
    }

    public String getSao3() {
        return sao3;
    }

    public void setSao3(String sao3) {
        this.sao3 = sao3;
    }

    public String getSao2() {
        return sao2;
    }

    public void setSao2(String sao2) {
        this.sao2 = sao2;
    }

    public String getSao4() {
        return sao4;
    }

    public void setSao4(String sao4) {
        this.sao4 = sao4;
    }

    public String getSao5() {
        return sao5;
    }

    public void setSao5(String sao5) {
        this.sao5 = sao5;
    }
}

