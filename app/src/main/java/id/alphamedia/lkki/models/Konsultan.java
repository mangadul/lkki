package id.alphamedia.lkki.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class Konsultan extends RealmObject {

    @PrimaryKey
    private int id_konsultan;

    private String nik;
    private String nama_konsultan;
    private String nohp;
    private String email;

    public void setNik(String nik){ this.nik = nik; }
    public String getNik(){ return nik; }

    public void setId_konsultan(int id_konsultan){ this.id_konsultan = id_konsultan; }
    public int getId_konsultan() { return id_konsultan; }

    public void setNama_konsultan(String nama_konsultan) { this.nama_konsultan = nama_konsultan; }
    public String getNama_konsultan() { return nama_konsultan; }

    public void setNohp(String nohp) {this.nohp = nohp; }
    public String getNohp(){ return nohp; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail(){ return email; }

}