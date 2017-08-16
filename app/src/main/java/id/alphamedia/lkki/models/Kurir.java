package id.alphamedia.lkki.models;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class Kurir extends RealmObject {

    @PrimaryKey
    private int id_kurir;

    private String nik;
    private String nama_kurir;
    private String nohp_kurir;

    public int getId_kurir() { return id_kurir; }
    public void setId_kurir(int id_kurir) { this.id_kurir = id_kurir; }

    public void setNik(String nik){ this.nik = nik; }
    public String getNik(){
        return nik;
    }

    public String getNama_kurir() { return nama_kurir; }
    public void setNama_kurir(String nama_kurir) { this.nama_kurir = nama_kurir; }

    public String getNohp_kurir() { return nohp_kurir; }
    public void setNohp_kurir(String nohp_kurir) { this.nohp_kurir = nohp_kurir; }

    @Override
    public String toString() {
        return nama_kurir;
    }

}