package id.alphamedia.lkki.models;

import java.io.Serializable;

public class UserDaftar implements Serializable {

    private String nama;
    private String imei;
    private String no_hp;
    private String email;
    private String passwd;
    private String tempat_lahir;
    private String tgl_lahir;
    private String pendidikan;
    private String perguruan_tinggi;

    private String alamat;
    private String desa;
    private String kecamatan;
    private String kota;
    private String provinsi;
    private String kodepos;

    public void setNama(String nama){ this.nama = nama; }
    public String getNama(){ return nama; }

    public void setImei(String imei) { this.imei = imei; }
    public String getImei(){ return imei; }

    public void setNo_hp(String no_hp) { this.no_hp = no_hp; }
    public String getNo_hp() {return no_hp; }

    public void setEmail(String email){ this.email = email; }
    public String getEmail(){ return email; }

    public void setPasswd(String passwd) { this.passwd = passwd; }
    public String getPasswd() { return passwd; }

    public void setTempat_lahir(String tempat_lahir) { this.tempat_lahir = tempat_lahir; }
    public String getTempat_lahir() { return tempat_lahir; }

    public void setTgl_lahir(String tgl_lahir){ this.tgl_lahir = tgl_lahir; }
    public String getTgl_lahir(){ return tgl_lahir; }

    public void setPendidikan(String pendidikan){ this.pendidikan = pendidikan; }
    public String getPendidikan(){ return pendidikan; }

    public void setPerguruan_tinggi(String perguruan_tinggi){ this.perguruan_tinggi = perguruan_tinggi; }
    public String getPerguruan_tinggi(){ return perguruan_tinggi; }

    public void setAlamat(String alamat){ this.alamat = alamat; }
    public String getAlamat(){ return alamat; }

    public void setDesa(String desa){ this.desa = desa; }
    public String getDesa(){ return desa; }

    public void setKecamatan(String kecamatan){ this.kecamatan = kecamatan; }
    public String getKecamatan(){ return kecamatan; }

    public void setKota(String kota){ this.kota = kota; }
    public String getKota(){ return kota; }

    public void setProvinsi(String provinsi){ this.provinsi = provinsi; }
    public String getProvinsi(){ return provinsi; }

    public void setKodepos(String kodepos){ this.kodepos = kodepos; }
    public String getKodepos(){ return kodepos; }

}