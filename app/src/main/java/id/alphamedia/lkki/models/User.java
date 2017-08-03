package id.alphamedia.lkki.models;

import io.realm.RealmObject;

/**
 * Created by abdulmuin on 10/07/17.
 */

public class User extends RealmObject {

    private String uname;
    private String nama;
    private String nik;
    private int tipe_user;
    private String kode_prov;
    private String kode_kab;
    private String session;

    private boolean is_login;

    void setNama(String nama){ this.nama = nama; }
    String getNama(){ return nama; }

    void setUname(String uname){ this.uname = uname; }
    String getUname(){ return uname; }

    void setNik(String nik) { this.nik = nik; }
    String getNik() { return nik; }

    void setTipe_user(int tipe_user){ this.tipe_user = tipe_user; }
    int getTipe_user(){ return tipe_user; }

    void setKode_prov(String kode_prov){ this.kode_prov = kode_prov; }
    String getKode_prov(){ return kode_prov; }

    void setKode_kab(String kode_kab){ this.kode_kab = kode_kab; }
    String getKode_kab() { return kode_kab; }

    void setIs_login(boolean is_login) {this.is_login = is_login; }
    boolean isIs_login(){ return is_login; }

    void setSession(String session){ this.session = session;}
    String getSession() { return session; }

}
