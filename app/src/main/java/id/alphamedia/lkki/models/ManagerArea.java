package id.alphamedia.lkki.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class ManagerArea extends RealmObject {

    @PrimaryKey
    private int id_ma;

    private String nama_ma;
    private String alamat_ma;
    private String email_ma;
    private String nohp_ma;

}