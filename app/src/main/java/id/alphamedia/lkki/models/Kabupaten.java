package id.alphamedia.lkki.models;


import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class Kabupaten extends RealmObject {

    @PrimaryKey
    @Index
    private int id;

    @Index
    private int province_id;

    private String name;

    public Kabupaten(){
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){ this.name = name; }

    public int getProvince_id(){
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }

    @Override
    public String toString() {
        return name;
    }

}

