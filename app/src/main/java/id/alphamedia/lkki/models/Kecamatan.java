package id.alphamedia.lkki.models;


import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class Kecamatan extends RealmObject {

    @PrimaryKey
    @Index
    private int id;

    @Index
    private int regency_id;

    // private RealmList<Kabupaten> id_kab;
    private String name;

    public Kecamatan(){
    }

    public int getId(){ return id; }
    public void setId(int id) { this.id = id; }

    public int getRegency_id() { return regency_id; }
    public void setRegency_id(int regency_id) { this.regency_id = regency_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }

}