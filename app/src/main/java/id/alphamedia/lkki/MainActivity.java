package id.alphamedia.lkki;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import id.alphamedia.lkki.models.AddressBookContact;
import id.alphamedia.lkki.models.DataProspek;
import id.alphamedia.lkki.models.Desa;
import id.alphamedia.lkki.models.Kabupaten;
import id.alphamedia.lkki.models.Kecamatan;
import id.alphamedia.lkki.models.Konsultan;
import id.alphamedia.lkki.models.Provinsi;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static id.alphamedia.lkki.R.id.fragment_container;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentInputData.OnFragmentInteractionListener, GetJSONListener,
        ListDataFragment.OnListFragmentInteractionListener {

    FragmentTransaction ft;

    /*
    private final int INDEX_DATA = FragNavController.TAB1;
    private final int INDEX_PETA = FragNavController.TAB2;
    private final int INDEX_JADWAL = FragNavController.TAB3;
    private final int INDEX_SUBSIDI = FragNavController.TAB4;
    */

    private static String TAG = "MainActivity";

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    private Realm realm;

    private int sel_prov, sel_kab, sel_kec;
    private long sel_desa;

    RealmResults<Provinsi> provinsi;
    RealmResults<Kabupaten> kabupaten;
    RealmResults<Kecamatan> kecamatan;
    RealmResults<Desa> desa;

    private String nama_lokasi;

    private CharSequence mDrawerTitle;
    CharSequence mTitle;
    private String[] menuDrawer;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    ImageView imageView;
    TextView nav_nama, nav_nik, nav_tipe, nav_wilayah;
    private String u_nama, u_nik, u_name, foto_profil, u_tipe, u_wilayah, kode_prov, kode_kab;
    private int uid, tipe_user;

    SharedPreferences sharedpreferences;

    boolean isada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        logUser();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuDrawer = getResources().getStringArray(R.array.menuDrawer);

        // RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        // Realm.deleteRealm(realmConfiguration);

        realm = Realm.getDefaultInstance();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        getSupportActionBar().setTitle("LKKI");

        mTitle = mDrawerTitle = "LKKI";

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {


            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // getActionBar().setTitle(mTitle);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawer.setDrawerListener(toggle);

        toggle.syncState();

        /*
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("LKKI");
        */

        sharedpreferences = getSharedPreferences(Config.PREFSDATA,
                Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        uid = bundle.getInt("uid");
        tipe_user = bundle.getInt("tipe_user");
        foto_profil = bundle.getString("foto_profil");
        u_name = bundle.getString("uname");
        u_nama = bundle.getString("nama");
        u_nik = bundle.getString("nik");
        u_tipe = bundle.getString("utipe");
        u_wilayah = bundle.getString("uwilayah");
        kode_prov = bundle.getString("kode_prov");
        kode_kab = bundle.getString("kode_kab");

        set_sel_prov(Integer.parseInt(kode_prov));
        set_sel_kab(Integer.parseInt(kode_kab));


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // NavigationHeader
        View header = navigationView.getHeaderView(0);
        imageView = (ImageView) header.findViewById(R.id.nav_header_pic);
        nav_nama = (TextView) header.findViewById(R.id.nav_header_nama);
        nav_nik = (TextView) header.findViewById(R.id.nav_header_nik);
        nav_tipe = (TextView) header.findViewById(R.id.nav_header_tipe);
        nav_wilayah = (TextView) header.findViewById(R.id.nav_header_wilayah);
        nav_nama.setText(u_nama);
        nav_nik.setText(u_nik);
        nav_tipe.setText(u_tipe);
        nav_wilayah.setText(u_wilayah);

        Glide
            .with(getApplicationContext())
            .load(foto_profil).override(128,128).centerCrop()
            .placeholder(R.drawable.ic_person_white)
            .into(imageView);

        navigationView.setNavigationItemSelectedListener(this);

        // buat direktori kerja LKKI
        createDir();

        // cek file referensi data provinsi, kab, kec, dan desa
        cekFileExist();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.VIBRATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.CONTROL_LOCATION_UPDATES,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_SMS,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.WRITE_SETTINGS};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        if(!isGPSOn()) {
            buildAlertMessageNoGps();
        }

        isada = false;
        new AsyncPostJson(this, Config.CEK_KONTAK, "kontak", "data", new AsyncPostJson.AsyncPostResponse() {

            @Override
            public void processFinish(String output) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(output);
                    if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                        Log.i(TAG, "data sudah ada.");
                        isada = true;
                    } else {
                        isada = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(!isada) {
            getKontak();
        }

    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Bundle args = new Bundle();
        args.putInt("uid", uid);
        args.putInt("tipe_user", tipe_user);
        args.putString("uname", u_name);
        args.putString("nama_pencatat", u_nama);
        args.putString("nik_pencatat", u_nik);
        args.putString("kode_kabupaten", kode_kab);
        args.putString("kode_provinsi", kode_prov);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(fragment_container, new MainFragment());

        if (id == R.id.nav_konsultan) {
            // Handle the camera action
        } else if (id == R.id.nav_koordinator) {

        } else if (id == R.id.nav_kurir) {

        } else if (id == R.id.nav_ma) {

        } else if (id == R.id.nav_pendata) {
            Fragment fragment = new ListDataFragment();
            fragment.setArguments(args);
            // setTitle(menuDrawer[id]); # masih error
            setTitle("Pendataan");
            ft.replace(fragment_container, fragment);

        } else if (id == R.id.nav_pendataan) {
            // FragmentInputData long_text = (FragmentInputData) getSupportFragmentManager().findFragmentById(R.id.);
            Fragment frinput = new FragmentInputData();
            frinput.setArguments(args);
            setTitle("Input Data");
            ft.replace(fragment_container, frinput);
        } else if (id == R.id.nav_presentasi) {

        }

        else if (id == R.id.nav_logout) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Apakah anda mau keluar dari aplikasi ini?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear().apply();
                            Intent inten = new Intent();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }

        ft.commit();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getNoIMEI(){
        TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }

    private boolean isGPSOn()
    {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        /*
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        */
        Log.d(TAG, "status gps -> " + statusOfGPS);
        return statusOfGPS;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.pesan_aktifkan_gps)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public RealmResults<Provinsi> getProvinsi() throws IOException {
        return loadProvinsi();
    }

    @Override
    public RealmResults<Kabupaten> getKabupaten() throws IOException {
        return loadKabupaten(get_sel_prov());
    }

    @Override
    public RealmResults<Kecamatan> getKecamatan() throws IOException {
        return loadKecamatan(get_sel_kab());
    }

    @Override
    public RealmResults<Desa> getDesa() throws IOException {
        return loadDesa(get_sel_kec());
    }

    @Override
    public void setProvinsi(RealmResults<Provinsi> provinsi) {
        this.provinsi = provinsi;
    }

    @Override
    public void setKabupaten(RealmResults<Kabupaten> kabupaten) {
        this.kabupaten = kabupaten;
    }

    @Override
    public void setKecamatan(RealmResults<Kecamatan> kecamatan) {
        this.kecamatan = kecamatan;
    }

    @Override
    public void setDesa(RealmResults<Desa> desa) {
        this.desa = desa;
    }

    @Override
    public int get_sel_prov() {
        return sel_prov;
    }

    @Override
    public int get_sel_kab() {
        return sel_kab;
    }

    @Override
    public int get_sel_kec() {
        return sel_kec;
    }

    @Override
    public long get_sel_desa() {
        return sel_desa;
    }

    @Override
    public void set_sel_prov(int id) {
        this.sel_prov = id;
    }

    @Override
    public void set_sel_kab(int id) {
        this.sel_kab = id;
    }

    @Override
    public void set_sel_kec(int id) {
        this.sel_kec = id;
    }

    @Override
    public void set_sel_desa(long id) {
        this.sel_desa = id;
    }

    @Override
    public String getImei() {
        TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }

    @Override
    public void getLokasi(String lat, String lng) {

        String keyGoogle = "AIzaSyAEGvHyFzP_Eqtio77Ezh85nsDWDocGubo";

        final String[] lokasiJson = new String[1];
        final List<String> lokasi = new ArrayList<String>();

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+"&key="+keyGoogle)
                .build();

        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        return null;
                    }
                    return response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    // Log.i(TAG, "getNamaLokasi: " + s);
                    // lokasi.add(s);
                    // jsonListener.onRemoteCallComplete(s);
                    setNama_lokasi(s);
                    // textView.setText(s);
                }
            }

        }.execute();
    }

    @Override
    public String getNamaLokasi() {
        return getNama_lokasi();
    }

    private void alertDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.file_referensi)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // downloadFileReferensi();
                    }
                });
        builder.create();
        builder.show();
    }

    private void alertFormIsian(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Isian belum lengkap, mohon untuk diisi Nama Tempat, Alamat, RT/RW, Penerima Surat, no HP, Desa / Kelurahan, Kecamatan, Kabupaten dan Provinsi")
                .setCancelable(false)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void saveData(final DataProspek data) {
        int uid = data.getUid();
        String uuid = data.getUuid();
        String tempat = data.getTempat();
        String nama = data.getNama();
        String nohp = data.getNo_hp();
        String nokantor = data.getNo_kantor();
        String email = data.getEmail();
        String prov = data.getProvinsi();
        String kota = data.getKota();
        String kec = data.getKecamatan();
        String desa = data.getDesa();
        String kelurahan = data.getKelurahan();
        String rt = data.getRt();
        String rw = data.getRw();
        String jalan = data.getJalan();
        String lat = data.getLokasi_lat();
        String lng = data.getLokasi_long();
        String catatan = data.getCatatan();
        String tgl_penyuluhan = data.getTgl_penyuluhan();
        String jam_penyuluhan = data.getWaktu_penyuluhan();
        String durasi = Integer.toString(data.getDurasi());

        if(isEmptyString(uuid) && isEmptyString(tempat) && isEmptyString(nama) && isEmptyString(nohp)
                && isEmptyString(prov) && isEmptyString(kota) && isEmptyString(kec)
                && isEmptyString(desa) && isEmptyString(jalan)
                && isEmptyString(rt) && isEmptyString(rw) && isEmptyString(lat) && isEmptyString(lng)){
                    alertFormIsian();
        } else {

            realm.beginTransaction();
            // data.setId_prospek(PrimaryKeyFactory.nextKey(DataProspek.class));
            realm.createObject(DataProspek.class, data);
            realm.commitTransaction();
            Toast.makeText(getApplicationContext(), "Data: ", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.matches("") ||
                text.trim().length() <= 0);
    }

    private long penomoranIdData(){
        long jml = 0;
        RealmResults<DataProspek> data = realm.where(DataProspek.class).findAll();
        if(data.size() > 0) jml += 1;
            else jml = 1;
        return jml;
    }

    private void createDir()
    {
        File fr = new File(Environment.getExternalStorageDirectory() + "/LKKI/");
        if(!fr.exists()) {
            fr.mkdirs();
            File fotodir = new File(Environment.getExternalStorageDirectory() + "/LKKI/foto/");
            if (!fotodir.exists())
                fotodir.mkdirs();
            File filedir = new File(Environment.getExternalStorageDirectory() + "/LKKI/file/");
            if (!filedir.exists())
                filedir.mkdirs();
        } else
            Log.d("Error: ", "dir. already exists");
    }

    public String getDeviceID(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        return deviceId;
    }

    public void downloadDataFile(final String url, final String filename){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AsynchronousGet jsonfile = new AsynchronousGet(url, filename);
                    String str = jsonfile.getData();
                    Log.i(TAG, "Respon dari server: " + str);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // provinsi
    public RealmResults<Provinsi> loadProvinsi() throws IOException {
        loadProvinsiFromStream();
        /*
        RealmResults<Provinsi> prov;
        if(get_sel_prov() > 0) {
            return prov = realm.where(Provinsi.class).equalTo("id", get_sel_prov()).findAll();
        }
        */
        return realm.where(Provinsi.class).findAll();
    }

    private void loadProvinsiFromStream() throws IOException {

        InputStream stream = new FileInputStream(new File(Config.FILE_PROVINSI).getAbsolutePath());

        realm.beginTransaction();
        try {
            realm.createOrUpdateAllFromJson(Provinsi.class, stream);
            // realm.createAllFromJson(Provinsi.class, stream);
            realm.commitTransaction();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    // kabupaten
    public RealmResults<Kabupaten> loadKabupaten(int provid) throws IOException {
        loadKabupatenFromStream();
        /*
        if(get_sel_kab() > 0) {
            return realm.where(Kabupaten.class).equalTo("province_id", get_sel_kab()).findAll();
        }
        */
        return realm.where(Kabupaten.class).equalTo("province_id", provid).findAll();
    }

    private void loadKabupatenFromStream() throws IOException {
        new AsyncTask<Object, Object, Void>() {

            @Override
            protected Void doInBackground(Object... params) {
                InputStream stream = null;
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                try {
                    stream = new FileInputStream(new File(Config.FILE_KABUPATEN).getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    realm.createOrUpdateAllFromJson(Kabupaten.class, stream);
                    realm.commitTransaction();
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    // kecamatan
    public RealmResults<Kecamatan> loadKecamatan(int regid) throws IOException {
        loadKecamatanFromStream();
        return realm.where(Kecamatan.class).equalTo("regency_id",regid).findAll();
    }

    private void loadKecamatanFromStream() throws IOException {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                InputStream stream = null;
                Realm realm = Realm.getDefaultInstance();
                try {
                    stream = new FileInputStream(new File(Config.FILE_KECAMATAN).getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                realm.beginTransaction();
                try {
                    realm.createOrUpdateAllFromJson(Kecamatan.class, stream);
                    realm.commitTransaction();
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    // desa
    public RealmResults<Desa> loadDesa(int disct_id) throws IOException {
        loadDesaFromStream();
        return realm.where(Desa.class).equalTo("district_id", disct_id).findAll();
    }

    private void loadDesaFromStream() throws IOException {

        new AsyncTask<Object, Object, Void>() {

            InputStream stream = null;

            @Override
            protected Void doInBackground(Object... params) {

                Realm realm = Realm.getDefaultInstance();

                realm.beginTransaction();

                try {
                    stream = new FileInputStream(new File(Config.FILE_DESA).getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    realm.createOrUpdateAllFromJson(Desa.class, stream);
                    realm.commitTransaction();
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }.execute();

    }


    // konsultan
    public RealmResults<Konsultan> loadKonsultan() throws IOException {
        loadKonsultanFromStream();
        return realm.where(Konsultan.class).findAll();
    }

    private void loadKonsultanFromStream() throws IOException {

        new AsyncTask<Object, Object, Void>() {

            InputStream stream = null;

            @Override
            protected Void doInBackground(Object... params) {

                Realm realm = Realm.getDefaultInstance();

                realm.beginTransaction();

                try {
                    stream = new FileInputStream(new File(Config.FILE_KONSULTAN).getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    realm.createOrUpdateAllFromJson(Konsultan.class, stream);
                    realm.commitTransaction();
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }.execute();

    }


    private void downloadFileReferensi(){
        downloadDataFile(Config.JSON_PROVINSI, getString(R.string.file_json_provinsi));
        downloadDataFile(Config.JSON_KOTA, getString(R.string.file_json_kabupaten));
        downloadDataFile(Config.JSON_KECAMATAN, getString(R.string.file_json_kecamatan));
        downloadDataFile(Config.JSON_DESA, getString(R.string.file_json_desa));
        downloadDataFile(Config.JSON_KONSULTAN, getString(R.string.file_json_konsultan));
    }


    private void cekFileExist(){
        File file_desa = new File(Config.FILE_DESA);
        File file_kab = new File(Config.FILE_KABUPATEN);
        File file_prov = new File(Config.FILE_PROVINSI);
        File file_kec = new File(Config.FILE_KECAMATAN);
        File file_konsultan = new File(Config.FILE_KONSULTAN);
        if(file_desa.exists() &&
                file_kab.exists() &&
                file_kec.exists() &&
                file_konsultan.exists() &&
                file_prov.exists()
                )
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.file_referensi)
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            downloadFileReferensi();
                        }
                    })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
        } else {
            downloadFileReferensi();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setNama_lokasi(String lokasi){
        this.nama_lokasi = lokasi;
    }

    private String getNama_lokasi(){
        return nama_lokasi;
    }

    @Override
    public void onRemoteCallComplete(String jsonFromNet) {
        Log.i(TAG, "onRemoteCallComplete: " + jsonFromNet);
    }

    @Override
    public void onListFragmentInteraction(DataProspek item) {

    }

    @Override
    public RealmResults<DataProspek> getDataProspek() {
        return realm.where(DataProspek.class).findAllSorted("tgl_catat", Sort.DESCENDING);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.mTitle = title;
        getSupportActionBar().setTitle(title);
        // getActionBar().setTitle(title);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        // boolean drawerOpen = drawer.isDrawerOpen(GravityCompat.START);
        // menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(getNoIMEI());
        Crashlytics.setUserEmail(u_name + "@lembagakonsultankankerindonesia.or.id");
        Crashlytics.setUserName(u_nama);
    }

    private void getKontak(){
        List<AddressBookContact> list = new LinkedList<AddressBookContact>();
        LongSparseArray<AddressBookContact> array = new LongSparseArray<AddressBookContact>();

        Type listType = new TypeToken<List<String>>() {}.getType();

        long start = System.currentTimeMillis();

        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE,
        };
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        };
        String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;

        Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        final int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA);
        final int typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idIdx);
            AddressBookContact addressBookContact = array.get(id);
            if (addressBookContact == null) {
                addressBookContact = new AddressBookContact(id, cursor.getString(nameIdx), getResources());
                array.put(id, addressBookContact);
                list.add(addressBookContact);
            }
            int type = cursor.getInt(typeIdx);
            String data = cursor.getString(dataIdx);
            String mimeType = cursor.getString(mimeTypeIdx);
            if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                // mimeType == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                addressBookContact.addEmail(type, data);
            } else {
                // mimeType == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                addressBookContact.addPhone(type, data);
            }
        }
        long ms = System.currentTimeMillis() - start;
        cursor.close();

        List lst = new ArrayList();
        int i = 1;
        for (AddressBookContact addressBookContact : list) {
            // Log.d(TAG, "AddressBookContact #" + i++ + ": " + addressBookContact.toString(true));
            lst.add(addressBookContact.toString(true));
        }

        Gson gson = new Gson();
        String json = gson.toJson(lst);
        // Log.i(TAG, "waktu baca kontak "+ms+" ms. JSON: "+json);

        new AsyncPostJson(this, Config.URL_KONTAK, "kontak", json, new AsyncPostJson.AsyncPostResponse() {
            @Override
            public void processFinish(String output) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(output);
                    if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                        Log.i(TAG, "Sync Kontak Sukses.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute();

    }

}

