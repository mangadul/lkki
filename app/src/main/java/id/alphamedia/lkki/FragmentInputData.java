package id.alphamedia.lkki;

/**
 * Created by abdulmuin on 12/07/17.
 */

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import id.alphamedia.lkki.models.DataProspek;
import id.alphamedia.lkki.models.Desa;
import id.alphamedia.lkki.models.Kabupaten;
import id.alphamedia.lkki.models.Kecamatan;
import id.alphamedia.lkki.models.Provinsi;
import io.realm.Realm;
import io.realm.RealmResults;

import static id.alphamedia.lkki.BaseFragment.ARGS_INSTANCE;


public class FragmentInputData extends Fragment implements LocationListener, View.OnClickListener {

    private double latitude, longitude;

    private static String TAG = "FragmentInputData";

    private OnFragmentInteractionListener mListener;

    private static final String UID = "uid";
    private static final String TIPE_USER = "tipe_user";
    private static final String NAMA_PENCATAT = "nama_pencatat";
    private static final String NIK_PENCATAT = "nik_pencatat";
    private static final String KODE_KABUPATEN = "kode_kabupaten";
    private static final String KODE_ROVINSI = "kode_provinsi";

    private String param_nama, param_nik, param_kota, param_prov;

    LocationManager lm;

    EditText str_lat;
    EditText str_lon;

    private int sel_item_prov;
    private int sel_item_kab;
    private int sel_item_kec;
    private int sel_item_desa;

    RealmResults<Kabupaten> kabupaten;
    RealmResults<Kecamatan> kecamatan;
    RealmResults<Desa> desa;

    Realm realm;

    ArrayAdapter<Kabupaten> kabAdapter;

    List<Kabupaten> spinkab = null;
    List<Kecamatan> spinkec;
    List<Desa> spindesa;

    EditText tempat, nama, alamat, lokasi_lat, lokasi_long, durasi, jabatan;
    EditText nohp, nokantor, email, jalan, kelurahan, rt, rw, catatan;

    private SearchableSpinner spin_prov, spin_kab, spin_kec, spin_desa;

    Button btnDatePicker, btnTimePicker, btnSimpan;

    private int uid, tipe_user, mYear, mMonth, mDay, mHour, mMinute;

    EditText txtDate, txtTime;

    public FragmentInputData() {
    }

    public static FragmentInputData  newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        FragmentInputData fragment = new FragmentInputData();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        // get argument passing by activity
        Bundle args = this.getArguments();
        if (args != null) {
            uid = args.getInt(UID);
            tipe_user = args.getInt(TIPE_USER);
            param_nama = args.getString(NAMA_PENCATAT);
            param_nik = args.getString(NIK_PENCATAT);
            param_kota = args.getString(KODE_KABUPATEN);
            param_prov = args.getString(KODE_ROVINSI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_input_data, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Setup any handles to view objects here
        nama = (EditText) view.findViewById(R.id.input_nama);
        tempat = (EditText) view.findViewById(R.id.input_tempat);
        email = (EditText) view.findViewById(R.id.input_email);
        tempat = (EditText) view.findViewById(R.id.input_tempat);
        nohp = (EditText) view.findViewById(R.id.input_nohp);
        nokantor = (EditText) view.findViewById(R.id.input_nokantor);
        rt = (EditText) view.findViewById(R.id.rt);
        rw = (EditText) view.findViewById(R.id.rw);
        jalan = (EditText) view.findViewById(R.id.input_jalan);
        kelurahan = (EditText) view.findViewById(R.id.input_kelurahan);
        lokasi_lat = (EditText) view.findViewById(R.id.lokasi_lat);
        lokasi_long = (EditText) view.findViewById(R.id.lokasi_long);
        catatan = (EditText) view.findViewById(R.id.input_tag);
        jabatan = (EditText) view.findViewById(R.id.input_jabatan);
        // kecamtn = (EditText) view.findViewById(R.id.input_kecamatan);

        spin_prov = (SearchableSpinner) view.findViewById(R.id.provinsi);
        spin_prov.setTitle("Pilih Provinsi");
        spin_prov.setPositiveButton("OK");

        spin_kab = (SearchableSpinner) view.findViewById(R.id.kota);
        spin_kab.setTitle("Pilih Kabupaten / Kota");
        spin_kab.setPositiveButton("OK");

        spin_kec = (SearchableSpinner) view.findViewById(R.id.kecamatan);
        spin_kec.setTitle("Pilih Kecamatan");
        spin_kec.setPositiveButton("OK");

        spin_desa = (SearchableSpinner) view.findViewById(R.id.desa);
        spin_desa.setTitle("Pilih Desa");
        spin_desa.setPositiveButton("OK");

        btnDatePicker=(Button) view.findViewById(R.id.btn_date);
        btnTimePicker=(Button) view.findViewById(R.id.btn_time);
        btnSimpan=(Button) view.findViewById(R.id.btnSimpan);

        txtDate=(EditText) view.findViewById(R.id.in_date);
        txtTime=(EditText) view.findViewById(R.id.in_time);

        durasi = (EditText) view.findViewById(R.id.input_durasi);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);


        // spinner provinsi
        List<Provinsi> spinprov = new ArrayList<>();
        RealmResults<Provinsi>  resultProvinsi = null;

        try {
            resultProvinsi = mListener.getProvinsi();
        } catch (IOException e) {
            e.printStackTrace();
        }

        spinprov = realm.copyFromRealm(resultProvinsi);

        // String defaultTextForSpinner = "Pilih Provinsi";

        ArrayAdapter<Provinsi> adapter = new ArrayAdapter<Provinsi>(getContext(), android.R.layout.simple_spinner_item, spinprov);
        spin_prov = (SearchableSpinner) view.findViewById(R.id.provinsi);
        spin_prov.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final SearchableSpinner finalSpin_kab = spin_kab;
        spin_prov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.i(TAG, "posisi provinsi: " + position);
                Provinsi prov = (Provinsi) parentView.getSelectedItem();
                // Toast.makeText(getContext(), "pilihan ID : " + prov.getId_prov() + " - Pilihan nama: " + prov.getNama_prov(), Toast.LENGTH_SHORT).show();
                finalSpin_kab.setEnabled(true);
                finalSpin_kab.setClickable(true);
                mListener.set_sel_prov(prov.getId_prov());
                try {
                    Log.i(TAG, "Pilihan Provinsi: " + mListener.get_sel_prov());
                    RealmResults<Kabupaten> kab = mListener.getKabupaten();
                    List<Kabupaten> datakab = realm.copyFromRealm(kab);
                    spinnerKabupaten(finalSpin_kab, datakab);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        Log.d(TAG, "latitude: " + getLatitude());
        Log.d(TAG, "longitude: " + getLongitude());

        EditText strNama = (EditText) view.findViewById(R.id.input_nama);

        updateUILatLong(view);
    }

    private void updateUILatLong(View view)
    {
        str_lat = (EditText) view.findViewById(R.id.lokasi_lat);
        str_lon = (EditText) view.findViewById(R.id.lokasi_long);
        str_lat.setText(String.valueOf(getLatitude()));
        str_lon.setText(String.valueOf(getLongitude()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        lm.removeUpdates(this);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private Double getLatitude(){
        return latitude;
    }

    private Double getLongitude(){
        return longitude;
    }


    private void setLatitude(Double latitude){
        this.latitude = latitude;
    }

    private void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    private void setKabupaten(RealmResults<Kabupaten> kab){
        this.kabupaten = kab;
    }

    private RealmResults<Kabupaten> getKabupaten(){
        return kabupaten;
    }

    private void setKecamatan(RealmResults<Kecamatan> kec){
        this.kecamatan = kec;
    }

    private RealmResults<Kecamatan> getKecamatan(){
        return kecamatan;
    }

    private void setDesa(RealmResults<Desa> desa){
        this.desa = desa;
    }

    private RealmResults<Desa> getDesa(){
        return desa;
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            Log.i(TAG, String.valueOf(location.getLatitude()));
            Log.i(TAG, String.valueOf(location.getLongitude()));

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            /*
            if(jalan.getText().toString().matches("")) {
                mListener.getLokasi(String.valueOf(latitude), String.valueOf(longitude));
                String lokasi = mListener.getNamaLokasi();
            }
            */

            str_lat.setText(String.valueOf(latitude));
            str_lon.setText(String.valueOf(longitude));

            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Provider " + provider + " is disabled");
    }

    @Override
    public void onClick(View v) {
        if (v == btnDatePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DecimalFormat mFormat= new DecimalFormat("00");
            mFormat.format(Double.valueOf(mMonth));

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            DecimalFormat mFormat= new DecimalFormat("00");
                            int bln = monthOfYear + 1;
                            String bul = mFormat.format(Double.valueOf(bln));
                            txtDate.setText(year +"-" + String.valueOf(bul) + "-" + mFormat.format(Double.valueOf(dayOfMonth)));
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.setTitle("Tanggal Penyuluhan");
            datePickerDialog.show();
        }

        if (v == btnTimePicker) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    txtTime.setText( new DecimalFormat("00").format(Double.valueOf(selectedHour)) + ":" + new DecimalFormat("00").format(Double.valueOf(selectedMinute)) + ":00");
                }
            }, mHour, mMinute, true);
            mTimePicker.setTitle("Jam Penyuluhan");
            mTimePicker.show();
        }

        if(v == btnSimpan) {
            simpanData(v);
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        RealmResults<Provinsi> getProvinsi() throws IOException;
        RealmResults<Kabupaten> getKabupaten() throws IOException;
        RealmResults<Kecamatan> getKecamatan() throws IOException;
        RealmResults<Desa> getDesa() throws IOException;
        void setProvinsi(RealmResults<Provinsi> provinsi);
        void setKabupaten(RealmResults<Kabupaten> kabupaten);
        void setKecamatan(RealmResults<Kecamatan> kecamatan);
        void setDesa(RealmResults<Desa> desa);
        int get_sel_prov();
        int get_sel_kab();
        int get_sel_kec();
        long get_sel_desa();
        void set_sel_prov(int id);
        void set_sel_kab(int id);
        void set_sel_kec(int id);
        void set_sel_desa(long id);
        String getImei();
        void getLokasi(String lat, String lng);
        String getNamaLokasi();
        void saveData(final DataProspek data);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }

    public int getSel_item_prov() {
        // return mListener.get_sel_prov();
        return sel_item_prov;
    }

    public void setSel_item_prov(int sel_item_prov){
        // mListener.set_sel_prov(sel_item_prov);
        this.sel_item_prov = sel_item_prov;
    }

    public int getSel_item_kab(){
        // return mListener.get_sel_kab();
        return sel_item_kab;
    }

    public void setSel_item_kab(int sel_item_kab){
        // mListener.set_sel_kab(sel_item_kab);
        this.sel_item_kab = sel_item_kab;
    }

    public int getSel_item_kec(){
        // return mListener.get_sel_kec();
        return sel_item_kec;
    }

    public void setSel_item_kec(int sel_item_kec){
        this.sel_item_kec = sel_item_kec;
        // mListener.set_sel_kec(sel_item_kec);
    }

    public int getSel_item_desa(){
        // return mListener.get_sel_desa();
        return sel_item_desa;
    }

    public void setSel_item_desa(int sel_item_desa){
        this.sel_item_desa = sel_item_desa;
        // mListener.set_sel_desa(sel_item_desa);
    }

    private void spinnerKabupaten(SearchableSpinner vspinnerkab, List<Kabupaten> spinkabs){

        ArrayAdapter<Kabupaten> adapterKabs = new ArrayAdapter<Kabupaten>(getContext(), android.R.layout.simple_spinner_item, spinkabs);
        Log.i(TAG, "pilih kab: " + getSel_item_kab());
        vspinnerkab.setAdapter(adapterKabs);
        adapterKabs.notifyDataSetChanged();

        final SearchableSpinner spinKec = spin_kec;

        vspinnerkab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Kabupaten kab = (Kabupaten) parentView.getSelectedItem();
                // Toast.makeText(getContext(), "pilihan ID : " + kab.getId() + " - Pilihan nama: " + kab.getName(), Toast.LENGTH_SHORT).show();
                setSel_item_kab(kab.getId());
                mListener.set_sel_kab(kab.getId());
                spinKec.setEnabled(true);
                spinKec.setClickable(true);
                try {
                    RealmResults<Kecamatan> kec = mListener.getKecamatan();
                    List<Kecamatan> datakec = realm.copyFromRealm(kec);
                    spinnerKecamatan(spinKec, datakec);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private void spinnerKecamatan(SearchableSpinner spinkec, List<Kecamatan> datakec){

        final ArrayAdapter<Kecamatan> adapterKec = new ArrayAdapter<Kecamatan>(getContext(), android.R.layout.simple_spinner_dropdown_item, datakec);
        spinkec.setAdapter(adapterKec);
        adapterKec.notifyDataSetChanged();

        final SearchableSpinner finalSpin_desa = spin_desa;

        spinkec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Kecamatan kec = (Kecamatan) parentView.getSelectedItem();
                // Toast.makeText(getContext(), "pilihan ID : " + kec.getId() + " - Pilihan nama: " + kec.getName(), Toast.LENGTH_SHORT).show();
                finalSpin_desa.setEnabled(true);
                finalSpin_desa.setClickable(true);
                mListener.set_sel_kec(kec.getId());
                try {
                    RealmResults<Desa> desa = mListener.getDesa();
                    List<Desa> datadesa = realm.copyFromRealm(desa);
                    spinnerDesa(finalSpin_desa, datadesa);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private void spinnerDesa(SearchableSpinner spindesa, List<Desa> datadesa){

        final ArrayAdapter<Desa> adapterDesa = new ArrayAdapter<Desa>(getContext(), android.R.layout.simple_spinner_dropdown_item, datadesa);

        spindesa.setAdapter(adapterDesa);
        adapterDesa.notifyDataSetChanged();

        spindesa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Desa desa = (Desa) parentView.getSelectedItem();
                // Toast.makeText(getContext(), "pilihan ID : " + desa.getId() + " - Pilihan nama: " + desa.getName(), Toast.LENGTH_SHORT).show();
                mListener.set_sel_desa(desa.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String tgl = dateFormat.format(date);
        return tgl;
    }

    private void simpanData(View v){

        DataProspek data = new DataProspek();

        Number maxValue = realm.where(DataProspek.class).max("id_prospek");
        long pk = ((maxValue == null) && (String.valueOf(maxValue).contains("null")) ) ? 1 : maxValue.longValue() + 1;

        String uuid = UUID.randomUUID().toString();
        String imei = mListener.getImei();
        String tempats = isEmpty(tempat.getText().toString()) ? null : tempat.getText().toString();

        String jalans = isEmpty(jalan.getText().toString()) ? null : jalan.getText().toString();
        String rts = isEmpty(rt.getText().toString()) ? null : rt.getText().toString();
        String rws = isEmpty(rw.getText().toString()) ? null : rw.getText().toString();
        String namas = isEmpty(nama.getText().toString()) ? null : nama.getText().toString();

        String no_hps = isEmpty(nohp.getText().toString()) ? null : nohp.getText().toString();
        String no_kantors = isEmpty(nokantor.getText().toString()) ? null : nokantor.getText().toString();
        String jabatans = isEmpty(jabatan.getText().toString()) ? null : jabatan.getText().toString();
        String emails = isEmpty(email.getText().toString()) ? null : email.getText().toString();

        String provs = isEmpty(String.valueOf(mListener.get_sel_prov())) ? null : String.valueOf(mListener.get_sel_prov());
        String kotas = isEmpty(String.valueOf(mListener.get_sel_kab())) ? null :String.valueOf(mListener.get_sel_kab());
        String kecs = isEmpty(String.valueOf(mListener.get_sel_kec())) ? null :String.valueOf(mListener.get_sel_kec());
        String desas = isEmpty(String.valueOf(mListener.get_sel_desa())) ? null :String.valueOf(mListener.get_sel_desa());

        String kelurahans = isEmpty(kelurahan.getText().toString()) ? null : kelurahan.getText().toString();
        String tgl_penyuluhans = isEmpty(txtDate.getText().toString()) ? "" : txtDate.getText().toString();
        String jam_penyuluhans = isEmpty(txtTime.getText().toString()) ? "" : txtTime.getText().toString();
        String durasis = isEmpty(durasi.getText().toString()) ? "0" : durasi.getText().toString();
        String lats = isEmpty(lokasi_lat.getText().toString()) ? null : lokasi_lat.getText().toString();
        String lngs = isEmpty(lokasi_long.getText().toString()) ? null : lokasi_long.getText().toString();
        String catatans = isEmpty(catatan.getText().toString()) ? null : catatan.getText().toString();

        data.setUuid(uuid);
        data.setId_prospek(pk++);

        /*
        if((tempats == null || tempats.matches("")) || (uid < 1) || (hitungJmlKata(tempats) < 3)
                || (jalans == null || jalans.matches(""))
            || (namas == null || namas.matches("")) || (no_hps == null || no_hps.matches(""))
                || (provs == null || provs.matches("")) || (kotas == null || kotas.matches(""))
                || (kecs == null || kecs.matches("")) || (desas == null || desas.matches(""))
                || (lats == null || lats.matches("")) || (lngs == null || lngs.matches(""))
                ) {
            alertFormIsian();
        } else {
        * */

        if((tempats == null || tempats.matches("")) || (uid < 1) || (hitungJmlKata(tempats) < 3)
            || (namas == null || namas.matches("")) || (no_hps == null || no_hps.matches(""))
                || (lats == null || lats.matches("")) || (lngs == null || lngs.matches(""))
                ) {
            alertFormIsian();
        } else {
            data.setUid(uid);
            data.setNik_pencatat(param_nik);
            data.setNama_pencatat(param_nama);
            data.setImei(imei);
            data.setTgl_catat(Commons.toDate(getDate()));
            data.setIs_dikirim(false);
            data.setStatus_koor(false);
            data.setStatus_ma(false);
            data.setStatus_presentasi(false);
            data.setTempat(tempats);
            data.setJalan(jalans);
            data.setRt(rts);
            data.setRw(rws);
            data.setKelurahan(kelurahans);

            Log.i(TAG, "ID desa: " + String.valueOf(desas));

            data.setDesa(desas);
            data.setKecamatan(kecs);
            data.setKota(kotas);
            data.setProvinsi(provs);
            data.setNama(namas);
            data.setJabatan(jabatans);
            data.setNo_hp(no_hps);
            data.setNo_kantor(no_kantors);
            data.setEmail(emails);
            data.setLokasi_lat(lats);
            data.setLokasi_long(lngs);
            data.setStatus_prospek(1);
            data.setTgl_penyuluhan(Commons.toDate(tgl_penyuluhans +" "+jam_penyuluhans));
            data.setWaktu_penyuluhan(jam_penyuluhans);
            data.setDurasi(Integer.parseInt(durasis));

            data.setKonsultan1("");
            data.setKonsultan2("");

            data.setCatatan(catatans);

            realm.beginTransaction();
            // data.setId_prospek(PrimaryKeyFactory.nextKey(DataProspek.class));
            // realm.createObject(DataProspek.class, data);
            realm.copyToRealmOrUpdate(data);
            realm.commitTransaction();

            Toast.makeText(getContext(), "Data berhasil disimpan.", Toast.LENGTH_SHORT).show();
        }

        // mListener.saveData(data);
    }

    private void alertFormIsian(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // String str_alert = "Isian belum lengkap, mohon untuk diisi Tempat (minimal 3 kata), Alamat, RT/RW, Penerima Surat, no HP, Desa / Kelurahan, Kecamatan, Kabupaten dan Provinsi. Aktifkan GPS untuk mendapatkan titik koordinat lokasi.";
        String str_alert = "Isian belum lengkap, mohon untuk diisi Tempat (minimal 3 kata), Nama Kontak dan No HP, pastikan juga anda mendapatkan titik koordinat.";
        builder.setMessage(str_alert)
                .setCancelable(false)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isEmptyStrings(String text) {
        return (text == null || text.trim().equals("null") || text.matches("") ||
                text.trim().length() <= 0);
    }


    private boolean isEmpty(String text){
        return (text == null || text.matches("") || text.length() <= 0);
    }

    private int hitungJmlKata(String s){
        int wordCount = 0;
        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }

}