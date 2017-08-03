package id.alphamedia.lkki.models;


import io.realm.RealmObject;

/**
 * Created by abdulmuin on 12/07/17.
 */

public class FotoPresentasi extends RealmObject {

    private String uuid;
    private String uri_foto;

    private boolean is_dikirim;
    private String tgl_dikirim;

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public String getUuid(){
        return uuid;
    }

    public void setUri_foto(String uri_foto){
        this.uri_foto = uri_foto;
    }

    public String getUri_foto(){
        return uri_foto;
    }


    public void setIs_dikirim(boolean is_dikirim){
        this.is_dikirim = is_dikirim;
    }

    public boolean isIs_dikirim(){
        return is_dikirim;
    }

    public void setTgl_dikirim(String tgl_dikirim) {
        this.tgl_dikirim = tgl_dikirim;
    }

    public String getTgl_dikirim(){
        return tgl_dikirim;
    }

}