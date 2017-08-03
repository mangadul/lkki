package id.alphamedia.lkki.models;

import java.io.Serializable;

public class UserLogin implements Serializable {

    private int uid;
    private String uname;
    private String nama;
    private String nik;
    private int tipe_user;
    private String kode_prov;
    private String kode_kab;
    private String session;
    private String foto_profil;
    private String utipe;
    private String uwilayah;

    private boolean is_login;

    public void setUid(int uid){ this.uid = uid; }
    public int getUid(){ return uid; }

    public void setNama(String nama){ this.nama = nama; }
    public String getNama(){ return nama; }

    public void setUname(String uname){ this.uname = uname; }
    public String getUname(){ return uname; }

    public void setNik(String nik) { this.nik = nik; }
    public String getNik() { return nik; }

    public void setTipe_user(int tipe_user){ this.tipe_user = tipe_user; }
    public int getTipe_user(){ return tipe_user; }

    public void setKode_prov(String kode_prov){ this.kode_prov = kode_prov; }
    public String getKode_prov(){ return kode_prov; }

    public void setKode_kab(String kode_kab){ this.kode_kab = kode_kab; }
    public String getKode_kab() { return kode_kab; }

    public void setIs_login(boolean is_login) {this.is_login = is_login; }
    public boolean isIs_login(){ return is_login; }

    public void setSession(String session){ this.session = session;}
    public String getSession() { return session; }

    public void setFoto_profil(String foto_profil) { this.foto_profil = foto_profil; }
    public String getFoto_profil() { return foto_profil; }

    public void setUtipe(String utipe){ this.utipe = utipe; }
    public String getUtipe(){ return utipe; }

    public void setUwilayah(String uwilayah) { this.uwilayah = uwilayah; }
    public String getUwilayah() { return uwilayah; }

}