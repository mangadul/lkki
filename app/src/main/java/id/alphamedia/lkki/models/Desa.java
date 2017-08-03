package id.alphamedia.lkki.models;


import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class Desa extends RealmObject {

    @PrimaryKey
    @Index
    private long id;

    @Index
    private int district_id;

    private String name;

    // private RealmList<Kecamatan> id_kec;

    public Desa(){
    }

    public long getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDistrict_id() { return district_id; }
    public void setDistrict_id(int district_id) { this.district_id = district_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }

}