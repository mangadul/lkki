package id.alphamedia.lkki.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class Koordinator extends RealmObject {

    @PrimaryKey
    private String id_koordinator;

    private String kode_koordinator;
    private String nama_koordinator;
    private String nohp_koordinator;
    private String email_koordinator;
    private String alamat_koordinator;

    private RealmList<Kabupaten> id_kab;

}