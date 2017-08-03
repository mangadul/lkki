package id.alphamedia.lkki.models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class Provinsi extends RealmObject {

    @PrimaryKey
    @Index
    private int id;

    private String name;

    public Provinsi(){
    }

    public String getNama_prov() { return name; }
    public void setNama_prov(String nama_prov) { this.name = nama_prov; }

    public int getId_prov() { return id; }
    public void setId_prov(int id_prov) { this.id = id_prov; }

    @Override
    public String toString() {
        return name;
    }

}
