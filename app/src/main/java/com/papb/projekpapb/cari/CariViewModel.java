package com.papb.projekpapb.cari;

public class CariViewModel {

    public String namaKendaraan;
    public int image;
    public String merk;
    public String cc;
    public String hargaSewa;

    public CariViewModel (String namaKendaraan, int image, String merk, String cc, String hargaSewa) {
        this.namaKendaraan = namaKendaraan;
        this.image = image;
        this.merk = merk;
        this.cc = cc;
        this.hargaSewa = hargaSewa;
    }
}
