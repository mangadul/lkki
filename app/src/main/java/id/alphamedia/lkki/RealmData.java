package id.alphamedia.lkki;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import id.alphamedia.lkki.models.Desa;
import id.alphamedia.lkki.models.Kabupaten;
import id.alphamedia.lkki.models.Kecamatan;
import id.alphamedia.lkki.models.Provinsi;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.internal.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by abdulmuin on 16/07/17.
 */

public class RealmData extends Activity implements  Callback {

    private static Realm realm;
    private final static String TAG = "RealmData";

    RealmResults<Provinsi> provinsi = null;
    RealmResults<Kabupaten> kabupaten = null;
    RealmResults<Kecamatan> kecamatan = null;
    RealmResults<Desa> desa = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration);
        Realm.setDefaultConfiguration(realmConfiguration);
        */

        realm = Realm.getDefaultInstance();

        // realm = realm.getInstance(realmConfiguration);
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            provinsi = loadProvinsi();
            setProvinsi(provinsi);
            /*
            kabupaten = loadKota();
            setKabupaten(kabupaten);
            kecamatan = loadKecamatan();
            setKecamatan(kecamatan);
            desa = loadDesa();
            setDesa(desa);
            */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        //realm.close();
        //Realm.deleteRealm(realmConfiguration);
    }

    public RealmResults<Provinsi> loadProvinsi() throws IOException {
        Log.i(TAG, "load data provinsi from server...");
        try {
            loadJsonFromStream("Provinsi", Config.JSON_PROVINSI);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return realm.where(Provinsi.class).findAll();
    }

    public RealmResults<Kabupaten> loadKota() throws IOException {
        try {
            Log.i(TAG, "load data kabupaten from server...");
            loadJsonFromStream("Kabupaten", Config.JSON_KOTA);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return realm.where(Kabupaten.class).findAll();
    }

    public RealmResults<Kecamatan> loadKecamatan() throws IOException {
        try {
            Log.i(TAG, "load data kecamatan from server...");
            loadJsonFromStream("Kecamatan", Config.JSON_KECAMATAN);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return realm.where(Kecamatan.class).findAll();
    }

    public RealmResults<Desa> loadDesa() throws IOException {
        try {
            Log.i(TAG, "load data desa from server...");
            loadJsonFromStream("Desa", Config.JSON_DESA);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return realm.where(Desa.class).findAll();
    }


    /*
    private void loadKotaFromStream() throws IOException, java.io.IOException {

        InputStream stream = null;
        try {
            // stream = getAssets().open("cities.json");
            URL fileURL = new URL(Config.JSON_KOTA);
            URLConnection connection = fileURL.openConnection();
            connection.connect();
            stream = new java.io.BufferedInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Open a transaction to store items into the realm
        realm.beginTransaction();
        try {
            realm.createAllFromJson(Kabupaten.class, stream);
            realm.commitTransaction();
        } catch (IOException e) {
            // Remember to cancel the transaction if anything goes wrong.
            realm.cancelTransaction();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
    */

    private void loadJsonFromStream(final String classname, String url) throws IOException, java.io.IOException {

        // final InputStream stream = null;
        OkHttpClient client = new OkHttpClient();
        Log.i(TAG, "call loadJsonFromStream.");
        realm.beginTransaction();
        try {
            // stream = getAssets().open("cities.json");
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(this);
            realm.commitTransaction();

        } catch (IOException e) {
            e.printStackTrace();
            realm.cancelTransaction();
        }
        // Open a transaction to store items into the realm
    }

    public RealmResults<Provinsi> getProvinsi() {
        return provinsi;
    }

    public RealmResults<Kabupaten> getKabupaten() {
        return kabupaten;
    }

    public RealmResults<Kecamatan> getKecamatan() {
        return kecamatan;
    }

    public RealmResults<Desa> getDesa() {
        return desa;
    }

    public void setProvinsi(RealmResults<Provinsi> provinsi) {
        this.provinsi = provinsi;
    }

    public void setKabupaten(RealmResults<Kabupaten> kabupaten) {
        this.kabupaten = kabupaten;
    }

    public void setKecamatan(RealmResults<Kecamatan> kecamatan) {
        this.kecamatan = kecamatan;
    }

    public void setDesa(RealmResults<Desa> desa) {
        this.desa = desa;
    }

    @Override
    public void onFailure(Call call, java.io.IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws java.io.IOException {
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        // System.out.println(response.body().string());
        String respon = response.body().string();
        Log.i(TAG, "Respon data json dari server: "+ respon);
        InputStream stream = new ByteArrayInputStream(respon.getBytes(StandardCharsets.UTF_8));
        try {
            // Class nama_kelas = Class.forName(classname);
            realm.createAllFromJson(Provinsi.class, stream);
        } catch (IOException e) {
            // Remember to cancel the transaction if anything goes wrong.
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
