package id.alphamedia.lkki.models;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class PesertaPresentasi extends RealmObject {

    @PrimaryKey
    private int id_peserta_presentasi;

    private RealmList<JadwalPresentasi> id_presentasi;

    private String nama_peserta;
    private String alamat;
    private String email;
    private String no_hp;
    private String no_rumah;
    private String nama_instansi;
    private String nama_divisi;

    private String ket_order;
    private int jml_order;
    private int status_penggunaan;

    public int getId_peserta_presentasi(){ return id_peserta_presentasi; }
    public void setId_peserta_presentasi(int id_peserta_presentasi) { this.id_peserta_presentasi = id_peserta_presentasi; }

}