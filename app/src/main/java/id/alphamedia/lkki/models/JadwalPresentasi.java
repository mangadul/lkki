package id.alphamedia.lkki.models;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class JadwalPresentasi extends RealmObject {

    @PrimaryKey
    private int id_presentasi;

    private RealmList<DataProspek> id_prospek;

    private Date tgl_presentasi;
    private Date jam_mulai;
    private Date jam_selesai;
    private Long time_start_unix;
    private Long time_stop_unix;

    private int jml_durasi; // dalam menit
    private String nama_lokasi;
    private String lokasi_lat;
    private String lokasi_long;

    private String catatan_presentasi;

    private RealmList<Konsultan> id_konsultan;
    private RealmList<Konsultan> pendamping;

    // kontak pemesanan / order
    private String kontak_order;
    private String jabatan_order;
    private String nohp_order;
    private String alamat_order;
    private Date tgl_order;
    private int total_order;

    // jadwal pengiriman
    private Date tgl_pengiriman;
    private RealmList<Kurir> id_kurir;
    private String nama_kurir;
    private String nohp_kurir;
    private int jml_dikirim;

    // status pengiriman barang
    private boolean is_diterima;
    private Date tgl_diterima;
    private String nama_penerima;
    private String nohp_penerima;
    private int jml_diterima;
    private String foto_penerima;

}