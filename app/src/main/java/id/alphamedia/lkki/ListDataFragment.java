package id.alphamedia.lkki;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.alphamedia.lkki.adapter.DividerItemDecoration;
import id.alphamedia.lkki.adapter.ItemClickSupport;
import id.alphamedia.lkki.adapter.MyListDataRecyclerViewAdapter;
import id.alphamedia.lkki.models.Constants;
import id.alphamedia.lkki.models.DataHelper;
import id.alphamedia.lkki.models.DataProspek;
import id.alphamedia.lkki.models.DataProspekSerializer;
import id.alphamedia.lkki.models.Desa;
import id.alphamedia.lkki.models.FotoPresentasi;
import id.alphamedia.lkki.models.FotoProspek;
import id.alphamedia.lkki.models.Kabupaten;
import id.alphamedia.lkki.models.Kecamatan;
import id.alphamedia.lkki.models.Konsultan;
import id.alphamedia.lkki.models.Kurir;
import id.alphamedia.lkki.models.Provinsi;
import id.alphamedia.lkki.tools.GlideToFile;
import id.alphamedia.lkki.tools.UploadFile;
import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static id.alphamedia.lkki.BaseFragment.ARGS_INSTANCE;
import static io.realm.Realm.getDefaultInstance;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListDataFragment extends Fragment  {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int MODE_FOTO = 1;
    private static final int MODE_VIDEO = 2;
    private static final String TAG = "ListDataFragment";
    private static final String FRAGMENT_TAG = "camera";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    Realm realm;
    private Menu menu;
    MyListDataRecyclerViewAdapter adapter;

    OrderedRealmCollection<DataProspek> dataProspek;

    RecyclerView recyclerView;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    String mCurrentPhotoPath;

    Uri imageUri;
    File tempfile;

    int pilkurir;
    String pilkonsul1, pilkonsul2;

    private int uid, tipe_user;
    private String param_nama, param_nik, param_kota, param_prov;

    SwipeRefreshLayout swipeLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListDataFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ListDataFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        ListDataFragment fragment = new ListDataFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        realm = getDefaultInstance();

        Bundle args = this.getArguments();
        if (args != null) {
            uid = args.getInt(Config.UID);
            tipe_user = args.getInt(Config.TIPE_USER);
            param_nama = args.getString(Config.NAMA_PENCATAT);
            param_nik = args.getString(Config.NIK_PENCATAT);
            param_kota = args.getString(Config.KODE_KABUPATEN);
            param_prov = args.getString(Config.KODE_ROVINSI);
        }

        OrderedRealmCollection<DataProspek> orc = getAllData();
        setDataProspek(orc);
        adapter = new MyListDataRecyclerViewAdapter(getContext(), realm, orc);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listdata_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

            final LinearLayoutManager mLayoutManager;
            mLayoutManager = new LinearLayoutManager(context);
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLayoutManager);

            RealmResults<Konsultan> konsultanRealmResults =  realm.where(Konsultan.class).findAll();
            RealmResults<Kurir> kurirRealmResults =  realm.where(Kurir.class).findAll();

            if(konsultanRealmResults.size() == 0)
            {
                try {
                    loadKonsultanFromStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(kurirRealmResults.size() == 0)
            {
                try {
                    loadKurirFromStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            */

            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) //check for scroll down
                    {
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                loading = false;
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });

            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, final View v) {
                    // adapter = recyclerView.getAdapter();
                    OrderedRealmCollection<DataProspek> odp = getDataProspek();
                    final DataProspek dp = odp.get(position);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle(dp.getTempat());
                    dialog.setItems(R.array.pilihanmenu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Toast.makeText(context, "Anda memilih menu: " + which, Toast.LENGTH_SHORT).show();
                            switch (which) {
                                case 0:
                                    try {
                                        ambilFoto(dp);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 1:
                                    if(isNetworkConnected()) {
                                        lihatData(dp);
                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                        alertDialogBuilder.setTitle("Kesalahan Jaringan");
                                        alertDialogBuilder.setMessage("Silahkan aktifkan koneksi internet terlebih dahulu untuk melihat detail Data ini!");
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alertDialogBuilder.create().show();
                                    }
                                    break;
                                case 2:
                                    editData(dp);
                                    break;
                                case 3:
                                    ruteAlamat(dp);
                                    break;
                                case 4:
                                    if(tipe_user == 4){
                                        pilihKonsultan(dp);
                                    } else {
                                        buatPesanJendela("Error", "Anda tidak berhak mengakses menu ini, hanya untuk koordinator.");
                                    }
                                    break;
                                case 5:
                                    if(tipe_user == 4){
                                        pilihKurir(dp);
                                    } else {
                                        buatPesanJendela("Error", "Anda tidak berhak mengakses menu ini, hanya untuk koordinator.");
                                    }
                                    break;
                                case 6:
                                    if(isNetworkConnected()) {
                                        kirimDataServer(dp);
                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                        alertDialogBuilder.setTitle("Kesalahan Jaringan");
                                        alertDialogBuilder.setMessage("Silahkan aktifkan koneksi internet terlebih dahulu untuk mengirim data ke server!");
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alertDialogBuilder.create().show();
                                    }
                                    break;
                                case 7:
                                    if(tipe_user == 4){
                                        updateDataByKoor(dp.getId_prospek());
                                    } else {
                                        buatPesanJendela("Error", "Anda tidak berhak mengakses menu ini, hanya untuk koordinator");
                                    }
                                    break;
                                case 8:
                                    if(tipe_user == 5){
                                        updateDataByMA(dp.getId_prospek());
                                    } else {
                                        buatPesanJendela("Error", "Anda tidak berhak mengakses menu ini, hanya untuk Manager Area (MA).");
                                    }
                                    break;
                                case 9:
                                    if(tipe_user == 4 || tipe_user == 2){
                                        updateStatus(dp);
                                    } else {
                                        buatPesanJendela("Error", "Anda tidak berhak mengakses menu ini, hanya untuk Konsultan dan Koordinator");
                                    }
                                    break;
                            }
                        }
                    });
                    dialog.create();
                    dialog.show();
                }
            });

            TouchHelperCallback touchHelperCallback = new TouchHelperCallback();
            ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
            touchHelper.attachToRecyclerView(recyclerView);

        }
        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void editData(DataProspek dp){
        final View promptsView;
        final LayoutInflater li = getActivity().getLayoutInflater();
        promptsView = li.inflate(R.layout.edit_data, null);

        final TextView edit_tempat = (TextView) promptsView.findViewById(R.id.edit_tempat);
        final TextView edit_catatan = (TextView) promptsView.findViewById(R.id.edit_catatan);
        final TextView edit_durasi = (TextView) promptsView.findViewById(R.id.edit_durasi);
        final TextView edit_email = (TextView) promptsView.findViewById(R.id.edit_email);
        final TextView edit_jabatan = (TextView) promptsView.findViewById(R.id.edit_jabatan);
        final TextView edit_jalan = (TextView) promptsView.findViewById(R.id.edit_jalan);
        final TextView edit_kelurahan = (TextView) promptsView.findViewById(R.id.edit_kelurahan);
        final TextView edit_nama = (TextView) promptsView.findViewById(R.id.edit_nama);
        final TextView edit_nohp = (TextView) promptsView.findViewById(R.id.edit_nohp);
        final TextView edit_nokantor = (TextView) promptsView.findViewById(R.id.edit_nokantor);
        final TextView edit_rt = (TextView) promptsView.findViewById(R.id.edit_rt);
        final TextView edit_rw = (TextView) promptsView.findViewById(R.id.edit_rw);
        final TextView lokasi_lat = (TextView) promptsView.findViewById(R.id.lokasi_lat);
        final TextView lokasi_long = (TextView) promptsView.findViewById(R.id.lokasi_long);
        final TextView edit_tgl_penyuluhan = (TextView) promptsView.findViewById(R.id.edit_tgl_penyuluhan);
        final TextView edit_jam_penyuluhan = (TextView) promptsView.findViewById(R.id.edit_jam_penyuluhan);

        // button
        Button btn_provinsi = (Button) promptsView.findViewById(R.id.btn_prov);
        Button btn_kab = (Button) promptsView.findViewById(R.id.btn_kab);
        Button btn_kec = (Button) promptsView.findViewById(R.id.btn_kec);
        Button btn_desa = (Button) promptsView.findViewById(R.id.btn_desa);
        Button btn_tanggal = (Button) promptsView.findViewById(R.id.btn_tanggal);
        Button btn_jam = (Button) promptsView.findViewById(R.id.btn_jam);

        btn_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

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
                                edit_tgl_penyuluhan.setText(year +"-" + String.valueOf(bul) + "-" + mFormat.format(Double.valueOf(dayOfMonth)));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.setTitle("Tanggal Penyuluhan");
                datePickerDialog.show();

            }
        });

        btn_jam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        edit_jam_penyuluhan.setText( new DecimalFormat("00").format(Double.valueOf(selectedHour)) + ":" + new DecimalFormat("00").format(Double.valueOf(selectedMinute)) + ":00");
                    }
                }, mHour, mMinute, true);
                mTimePicker.setTitle("Jam Penyuluhan");
                mTimePicker.show();

            }
        });

        // prov, kota, kec, desa
        final TextView kode_prov = (TextView) promptsView.findViewById(R.id.kode_prov);
        final TextView nama_prov = (TextView) promptsView.findViewById(R.id.nama_prov);
        final TextView kode_kab = (TextView) promptsView.findViewById(R.id.kode_kab);
        final TextView nama_kab  = (TextView) promptsView.findViewById(R.id.nama_kab);
        final TextView kode_kec  = (TextView) promptsView.findViewById(R.id.kode_kec);
        final TextView nama_kec  = (TextView) promptsView.findViewById(R.id.nama_kec);
        final TextView kode_desa  = (TextView) promptsView.findViewById(R.id.kode_desa);
        final TextView nama_desa  = (TextView) promptsView.findViewById(R.id.nama_desa);

        // pilih provinsi
        btn_provinsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmResults<Provinsi> prov = realm.getDefaultInstance().where(Provinsi.class).findAll();
                final List<Provinsi> lis_prov = realm.getDefaultInstance().copyFromRealm(prov);
                final AlertDialog.Builder dialogProv = new AlertDialog.Builder(getActivity());
                dialogProv.setTitle("Pilih Provinsi");
                final ArrayAdapter<Provinsi> adapterProv = new ArrayAdapter<Provinsi>(getActivity(), android.R.layout.select_dialog_singlechoice);
                adapterProv.addAll(lis_prov);
                adapterProv.notifyDataSetChanged();
                dialogProv.setAdapter(adapterProv, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Provinsi pilprov = (Provinsi) lis_prov.get(which);
                        // Toast.makeText(getActivity(), "Pilih Provinsi ID : "+pilprov.getId_prov()+" - "+pilprov.getNama_prov(), Toast.LENGTH_SHORT).show();
                        kode_prov.setText(String.valueOf(pilprov.getId_prov()));
                        nama_prov.setText(String.valueOf(pilprov.getNama_prov()));
                        kode_kab.setText("");
                        nama_kab.setText("");
                        kode_kec.setText("");
                        nama_kec.setText("");
                        kode_desa.setText("");
                        nama_desa.setText("");
                    }
                });
                dialogProv.create().show();
            }
        });

        // pilih kabupaten
        btn_kab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kode_prov.getText().toString().length() > 0)
                {
                    RealmResults<Kabupaten> prov = realm.getDefaultInstance().where(Kabupaten.class).equalTo("province_id", Integer.parseInt(kode_prov.getText().toString())).findAll();
                    final List<Kabupaten> list_kab = realm.getDefaultInstance().copyFromRealm(prov);
                    final AlertDialog.Builder dialogKab = new AlertDialog.Builder(getActivity());
                    dialogKab.setTitle("Pilih Kabupaten");
                    final ArrayAdapter<Kabupaten> adapterKab = new ArrayAdapter<Kabupaten>(getActivity(), android.R.layout.select_dialog_singlechoice);
                    adapterKab.addAll(list_kab);
                    adapterKab.notifyDataSetChanged();
                    dialogKab.setAdapter(adapterKab, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Kabupaten pilkab = (Kabupaten) list_kab.get(which);
                            kode_kab.setText(String.valueOf(pilkab.getId()));
                            nama_kab.setText(String.valueOf(pilkab.getName()));
                            kode_kec.setText("");
                            nama_kec.setText("");
                            kode_desa.setText("");
                            nama_desa.setText("");
                        }
                    });
                    dialogKab.create().show();
                } else {
                    buatPesanJendela("Kesalahan", "Provinsi belum dipilih");
                }
            }
        });

        // pilih kecamatan
        btn_kec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kode_kab.getText().toString().length() > 0){
                    RealmResults<Kecamatan> kecamatan = realm.getDefaultInstance().where(Kecamatan.class).equalTo("regency_id", Integer.parseInt(kode_kab.getText().toString())).findAll();
                    final List<Kecamatan> list_kec = realm.getDefaultInstance().copyFromRealm(kecamatan);
                    final AlertDialog.Builder dialogKec = new AlertDialog.Builder(getActivity());
                    dialogKec.setTitle("Pilih Kecamatan");
                    final ArrayAdapter<Kecamatan> adapterKec = new ArrayAdapter<Kecamatan>(getActivity(), android.R.layout.select_dialog_singlechoice);
                    adapterKec.addAll(list_kec);
                    adapterKec.notifyDataSetChanged();
                    dialogKec.setAdapter(adapterKec, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Kecamatan pilkec = (Kecamatan) list_kec.get(which);
                            kode_kec.setText(String.valueOf(pilkec.getId()));
                            nama_kec.setText(String.valueOf(pilkec.getName()));
                            kode_desa.setText("");
                            nama_desa.setText("");
                        }
                    });
                    dialogKec.create().show();
                } else {
                    buatPesanJendela("Kesalahan", "Kabupaten / Kota belum dipilih");
                }
            }
        });

        // pilih desa
        btn_desa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kode_kec.getText().toString().length() > 0){
                    RealmResults<Desa> desa = realm.getDefaultInstance().where(Desa.class).equalTo("district_id", Long.parseLong(kode_kec.getText().toString())).findAll();
                    final List<Desa> list_desa = realm.getDefaultInstance().copyFromRealm(desa);
                    final AlertDialog.Builder dialogDesa = new AlertDialog.Builder(getActivity());
                    dialogDesa.setTitle("Pilih Desa");
                    final ArrayAdapter<Desa> adapterDesa = new ArrayAdapter<Desa>(getActivity(), android.R.layout.select_dialog_singlechoice);
                    adapterDesa.addAll(list_desa);
                    adapterDesa.notifyDataSetChanged();
                    dialogDesa.setAdapter(adapterDesa, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Desa pildesa = (Desa) list_desa.get(which);
                            kode_desa.setText(String.valueOf(pildesa.getId()));
                            nama_desa.setText(String.valueOf(pildesa.getName()));
                        }
                    });
                    dialogDesa.create().show();
                } else {
                    buatPesanJendela("Kesalahan", "Kecamatan belum dipilih");
                }
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Edit " + dp.getTempat());
        alertDialogBuilder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();


    }


    private void lihatData(DataProspek dp) {
        final View promptsView;
        LayoutInflater li = getActivity().getLayoutInflater();
        promptsView = li.inflate(R.layout.detail_view, null);

        ImageView iv_peta = (ImageView) promptsView.findViewById(R.id.peta_lokasi);

        // head
        TextView head_tgl_presentasi = (TextView) promptsView.findViewById(R.id.head_tgl_presentasi);
        TextView head_catatan = (TextView) promptsView.findViewById(R.id.head_catatan);
        TextView head_konsultan = (TextView) promptsView.findViewById(R.id.head_konsultan);
        TextView head_kurir = (TextView) promptsView.findViewById(R.id.head_kurir);
        TextView head_nama_tempat = (TextView) promptsView.findViewById(R.id.head_nama_tempat);
        TextView head_pendata = (TextView) promptsView.findViewById(R.id.head_pendata);
        TextView head_tgl_catat = (TextView) promptsView.findViewById(R.id.head_tgl_catat);
        TextView head_tgl_kirim = (TextView) promptsView.findViewById(R.id.head_tgl_kirim);
        TextView head_tgl_update_server = (TextView) promptsView.findViewById(R.id.head_tgl_update_server);


        // tgl update server
        if(dp.getTgl_dikirim() == null)
        {
            head_tgl_update_server.setText("Refresh utk melihat tgl update server");
        } else {
            head_tgl_update_server.setText(dp.getTgl_dikirim().toString());
        }

        // tanggal pengiriman kurir
        if(dp.getKurir_tgl_kirim() == null
            || dp.getKurir_tgl_kirim().matches("")
            || dp.getKurir_tgl_kirim().matches("null"))
        {
            head_tgl_kirim.setText("Belum ditentukan");
        } else {
            head_tgl_kirim.setText(dp.getKurir_tgl_kirim());
        }

        // tanggal penyuluhan
        if(dp.getTgl_penyuluhan() != null) {
            String tgl_presentasi = dp.getTgl_penyuluhan().toString().matches("")
                    || dp.getTgl_penyuluhan().toString().matches("null") ?
                    "Belum ditentukan"
                    : dp.getTgl_penyuluhan().toString();
            head_tgl_presentasi.setText(tgl_presentasi);
        } else {
            head_tgl_presentasi.setText("Belum ditentukan");
        }

        // konsultan
        if(dp.getKonsultan1() == null
                || dp.getKonsultan1().matches("")
                || dp.getKonsultan1().matches("null")) {
            head_konsultan.setText("Belum dipilih");
        } else {
            Konsultan konsultan = realm.getDefaultInstance().where(Konsultan.class).equalTo("id_konsultan", Integer.parseInt(dp.getKonsultan1())).findFirst();
            if(konsultan != null) head_konsultan.setText(konsultan.getNama_konsultan());
                else head_konsultan.setText("Belum dipilih");
        }

        // kurir
        if(dp.getKurir() == 0) {
            head_kurir.setText("Belum dipilih");
        } else {
            Kurir kurir = realm.getDefaultInstance().where(Kurir.class).equalTo("id_kurir", dp.getKurir()).findFirst();
            if(kurir != null) head_kurir.setText(kurir.getNama_kurir());
                else head_kurir.setText("Belum dipilih");
        }

        head_tgl_catat.setText(dp.getTgl_catat().toString());
        head_pendata.setText(dp.getNama_pencatat());
        head_nama_tempat.setText(dp.getTempat());
        head_catatan.setText(dp.getCatatan());

        // isi
        TextView info_nama_tempat = (TextView) promptsView.findViewById(R.id.info_nama_tempat);
        TextView info_alamat = (TextView) promptsView.findViewById(R.id.info_alamat);
        TextView info_rt_rw = (TextView) promptsView.findViewById(R.id.info_rt_rw);
        TextView info_desa = (TextView) promptsView.findViewById(R.id.info_desa);
        TextView info_kecamatan = (TextView) promptsView.findViewById(R.id.info_kecamatan);
        TextView info_kabupaten = (TextView) promptsView.findViewById(R.id.info_kabupaten);
        TextView info_provinsi = (TextView) promptsView.findViewById(R.id.info_provinsi);
        TextView info_nama_kontak = (TextView) promptsView.findViewById(R.id.info_nama_kontak);
        TextView info_kontak_no_hp = (TextView) promptsView.findViewById(R.id.info_kontak_no_hp);
        TextView info_jabatan = (TextView) promptsView.findViewById(R.id.info_jabatan);
        TextView info_koordinat = (TextView) promptsView.findViewById(R.id.info_koordinat);
        TextView info_alamat_google = (TextView) promptsView.findViewById(R.id.info_alamat_google);

        // desa
        if(dp.getDesa() == null || dp.getDesa().matches("")
                || dp.getDesa().matches("null")
                || dp.getDesa().matches("0")){
            info_desa.setText("Desa <Belum dipilih>");
        } else {
            Desa desa = realm.getDefaultInstance().where(Desa.class).equalTo("id", Long.parseLong(dp.getDesa())).findFirst();
            if(desa != null) info_desa.setText("Desa " + desa.getName());
                else info_desa.setText("Desa <Belum dipilih>");
        }

        // kecamatan
        if(dp.getKecamatan() == null
                || dp.getKecamatan().matches("")
                || dp.getKecamatan().matches("null")
                || dp.getKecamatan().matches("0")){
            info_kecamatan.setText("Kecamatan <Belum dipilih>");
        } else {
            Kecamatan kecamatan = realm.getDefaultInstance().where(Kecamatan.class).equalTo("id", Integer.parseInt(dp.getKecamatan())).findFirst();
            if(kecamatan != null) info_kecamatan.setText("Kecamatan " + kecamatan.getName());
                else info_kecamatan.setText("Kecamatan <Belum dipilih>");
        }

        // kabupaten
        if(dp.getKota() == null
                || dp.getKota().matches("")
                || dp.getKota().matches("null")
                || dp.getKota().matches("0")){
            info_kabupaten.setText("Kabupaten <Belum dipilih>");
        } else {
            Kabupaten kabupaten = realm.getDefaultInstance().where(Kabupaten.class).equalTo("id", Integer.parseInt(dp.getKota())).findFirst();
            if(kabupaten != null) info_kabupaten.setText("Kabupaten " + kabupaten.getName());
                else info_kabupaten.setText("Kabupaten <Belum dipilih>");
        }

        // provinsi
        if(dp.getProvinsi() == null
                || dp.getProvinsi().matches("")
                || dp.getProvinsi().matches("null")
                || dp.getProvinsi().matches("0")){
            info_provinsi.setText("Provinsi <Belum dipilih>");
        } else {
            Provinsi provinsi = realm.getDefaultInstance().where(Provinsi.class).equalTo("id", Integer.parseInt(dp.getProvinsi())).findFirst();
            if(provinsi != null) info_provinsi.setText("Provinsi " + provinsi.getNama_prov());
                else info_provinsi.setText("Provinsi <Belum dipilih>");
        }

        Geocoder coder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> results;
        try {
            results = coder.getFromLocation(Double.parseDouble(dp.getLokasi_lat()), Double.parseDouble(dp.getLokasi_long()), 1);
            if(results != null){
                Address location = results.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();
                for(int i = 0; i <= location.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(location.getAddressLine(i));
                }
                // System.getProperty("line.separator")
                String alamatg = TextUtils.join(", ", addressFragments);
                StringBuilder sbalamat = new StringBuilder().append(alamatg);
                info_alamat_google.setText(sbalamat.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder petaStatis = new StringBuilder()
                .append("https://maps.googleapis.com/maps/api/staticmap?center=")
                .append(dp.getLokasi_lat()).append(",").append(dp.getLokasi_long())
                .append("&zoom=16")
                .append("&size=400x400")
                .append("&maptype=roadmap")
                .append("&markers=color:red%7Clabel:AA%7C")
                .append(dp.getLokasi_lat()).append(",").append(dp.getLokasi_long())
                .append("&key=").append(Constants.GMAP_STATIC_KEY);

        Glide
            .with(getContext())
            .load(petaStatis.toString()).override(300,300).centerCrop()
            .placeholder(R.drawable.ic_person_white)
            .into(iv_peta);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        // alertDialogBuilder.setTitle(dp.getTempat());

        /*
        alertDialogBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        */

        // foto pendataan
        new AmbilFotoProspek(getContext(), dp.getUuid(), "p", Config.GET_FOTO, new AmbilFotoProspek.AsyncPostResponse() {
            @Override
            public void processFinish(String output) {
                if(output != null) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(output);
                        if(jsonObject.has("success") && jsonObject.getBoolean("success")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            LinearLayout layout = (LinearLayout) promptsView.findViewById(R.id.layout_foto_pendataan);
                            for(int i=0; i < data.length(); i++){
                                JSONObject fotop = data.getJSONObject(i);
                                String url_foto = fotop.getString("url_foto");
                                ImageView image = new ImageView(getContext());
                                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                // image.setMaxHeight(600);
                                // image.setMaxWidth(600);
                                image.setId(i);
                                image.setPadding(0,2,0,0);
                                image.setImageResource(R.drawable.ic_account_circle_white_48dp);
                                image.setVisibility(View.VISIBLE);
                                layout.addView(image);
                                Glide
                                    .with(getContext())
                                    .load(url_foto).override(600,600).centerCrop()
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(image);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute();

        // foto presentasi
        new AmbilFotoProspek(getContext(), dp.getUuid(), "k", Config.GET_FOTO, new AmbilFotoProspek.AsyncPostResponse() {
            @Override
            public void processFinish(String output) {
                if(output != null) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(output);
                        if(jsonObject.has("success") && jsonObject.getBoolean("success")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            LinearLayout layout = (LinearLayout) promptsView.findViewById(R.id.layout_foto_presentasi);
                            for(int i=0; i < data.length(); i++){
                                JSONObject fotop = data.getJSONObject(i);
                                String url_foto = fotop.getString("url_foto");
                                ImageView image = new ImageView(getContext());
                                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                image.setPadding(0,2,0,0);
                                image.setImageResource(R.drawable.ic_account_circle_white_48dp);
                                image.setVisibility(View.VISIBLE);
                                layout.addView(image);
                                Glide
                                    .with(getContext())
                                    .load(url_foto).override(600,600).centerCrop()
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(image);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute();

        String jalan = "<Alamat jalan belum diisi>";
        if(dp.getJalan() != null)
        {
            jalan = dp.getJalan().matches("") || dp.getJalan().matches("null") ? "<Alamat jalan belum diisi>" : dp.getJalan();
        }

        String rt = "<RT belum diisi>";
        if(dp.getRt() != null) {
            rt = dp.getRt().matches("") || dp.getRt().matches("null") ? "<RT belum diisi>" : dp.getRt();
        }

        String rw = "<RW belum diisi>";
        if(dp.getRw() != null)
        {
            rw = dp.getRw().matches("") || dp.getRw().matches("null")  ? "<RW belum diisi>" : dp.getRw();
        }

        String jabatan = "<Jabatan belum diisi>";
        if(dp.getJabatan() != null) {
            jabatan = dp.getJabatan().matches("") || dp.getJabatan().matches("null") ? "<Jabatan belum diisi>" : dp.getJabatan();
        }

        info_nama_tempat.setText(dp.getTempat());
        info_nama_kontak.setText(dp.getNama());
        info_kontak_no_hp.setText(dp.getNo_hp());
        info_jabatan.setText(jabatan);
        info_alamat.setText(jalan);
        info_rt_rw.setText("Rt. " + rt + "  Rw. " + rw);
        info_koordinat.setText(dp.getLokasi_lat() +", "+dp.getLokasi_long());

        alertDialogBuilder.create().show();
    }

    private void ruteAlamat(final DataProspek dataProspek){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Petunjuk lokasi ke "+dataProspek.getTempat());
        alertDialogBuilder.setIcon(R.drawable.ic_location_on_black_36dp);
        alertDialogBuilder.setMessage(R.string.pesan_arahgoogle);
        alertDialogBuilder.setPositiveButton("Bantu Arahkan Lokasi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder uri = new StringBuilder()
                        .append("google.navigation:q=")
                        .append(dataProspek.getLokasi_lat())
                        .append(",")
                        .append(dataProspek.getLokasi_long())
                        .append("&mode=d&avoid=t");
                Uri gmmIntentUri = Uri.parse(uri.toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                getActivity().startActivity(mapIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();

    }

    private void ambilFoto(final DataProspek dp) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String currentDateandTime = sdf.format(new Date()) + ".jpg";
        File photo = new File(Config.FOTO_DIR,  currentDateandTime);
        photo.createNewFile();
        tempfile = createMediaFile(MODE_FOTO);
        final String foto_lok = photo.getAbsolutePath();
        final String temp_foto = tempfile.getAbsolutePath();

        final int usid = uid;
        final int guid = dp.getUid();
        final String konsul = dp.getKonsultan1();

        final String uuid = dp.getUuid();
        final File tmpfile = tempfile;
        try {
            takePhoto(currentDateandTime);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide
                        .with(getContext())
                        .load(temp_foto)
                        .asBitmap()
                        .override(640,480)
                        //.centerCrop()
                        .into(new GlideToFile(foto_lok, 640, 480));

                }
            }, 2000);

            if(tmpfile.exists() && tmpfile.isFile()){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(tmpfile.delete()) {
                            Log.i(TAG, "tempfile foto has been deleted.");
                        } else {
                            // getContext().deleteFile(tmpfile.getAbsolutePath());
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    tempfile.getName());
                            boolean deleted = file.delete();
                            if(deleted) Log.i(TAG, "file "+ tempfile.getName() +" berhasil dihapus.");
                        }
                    }
                }, 500);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                switch(tipe_user){
                    case 8:
                    case 7:
                    case 6:
                    case 5:
                    case 4:
                    case 3:
                    case 1:
                        if(usid > guid) {
                            buatPesanJendela("Error", "Anda tidak berhak mengakses data ini, anda bukan penginput (pendata) untuk data ini.");
                        } else {
                            Number maxValue = getDefaultInstance().where(FotoProspek.class).max("id");
                            int pk = ((maxValue == null) && (String.valueOf(maxValue).contains("null")) ) ? 1 : maxValue.intValue() + 1;
                            FotoProspek fotoProspek = new FotoProspek();
                            fotoProspek.setId(pk);
                            fotoProspek.setUuid(uuid);
                            fotoProspek.setIs_dikirim(false);
                            fotoProspek.setFilename(currentDateandTime);
                            fotoProspek.setUri_foto(foto_lok);
                            getDefaultInstance().insert(fotoProspek);
                        }
                        break;
                    case 2: // konsultan
                        if(konsul.matches("") || konsul.matches("null") || konsul == null) {
                            buatPesanJendela("Error", "Silahkan pilih konsultan terlebih dahulu.");
                        } else {
                            if(usid > Integer.parseInt(konsul))
                            {
                                buatPesanJendela("Error", "Anda bukan Konsultan yang telah ditetapkan untuk mengakses data ini.");
                            } else {
                                Number maxid = getDefaultInstance().where(FotoPresentasi.class).max("id");
                                int ppk = ((maxid == null) && (String.valueOf(maxid).contains("null")) ) ? 1 : maxid.intValue() + 1;
                                FotoPresentasi fotoPresentasi = new FotoPresentasi();
                                fotoPresentasi.setId(ppk);
                                fotoPresentasi.setUuid(uuid);
                                fotoPresentasi.setIs_dikirim(false);
                                fotoPresentasi.setFilename(currentDateandTime);
                                fotoPresentasi.setUri_foto(foto_lok);
                                getDefaultInstance().insert(fotoPresentasi);
                            }
                        }
                        break;
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                buatPesanJendela("Sukses", "Foto baru telah ditambahkan dengan nama file "+currentDateandTime);
            }
        });

        // File f = getContext().getFileStreamPath(foto_lok);
        File file = new File(Config.FOTO_DIR, currentDateandTime + ".jpg");
        boolean deleted = file.delete();
        if(deleted) Log.i(TAG, "file "+foto_lok+" berhasil dihapus.");

    }

    private void pilihKurir(DataProspek dataProspek)
    {
        LayoutInflater li = getActivity().getLayoutInflater();
        View promptsView = li.inflate(R.layout.pilih_kurir, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Pilih Kurir :: " + dataProspek.getTempat());
        alertDialogBuilder.setIcon(R.drawable.ic_airport_shuttle_black_36dp);
        final Spinner spinkurir = (Spinner) promptsView.findViewById(R.id.kurir);
        final TextView tvTanggal =(TextView) promptsView.findViewById(R.id.tgl_kirim);
        final TextView tvOrder =(TextView) promptsView.findViewById(R.id.jml_order);
        final Button btnTanggal =(Button) promptsView.findViewById(R.id.btn_tanggal);

        final TextView tvJam =(TextView) promptsView.findViewById(R.id.jam_kirim);
        final Button btnJam =(Button) promptsView.findViewById(R.id.btn_jam);

        final long idp = dataProspek.getId_prospek();

        String tgl_kirim = (dataProspek.getTgl_dikirim()== null || String.valueOf(dataProspek.getTgl_dikirim()).matches("")) ? "" : String.valueOf(dataProspek.getTgl_dikirim());
        int jml_order = (dataProspek.getJmlorder() <= 0 || String.valueOf(dataProspek.getJmlorder()).matches("")) ? 0 : dataProspek.getJmlorder();

        tvTanggal.setText(tgl_kirim);
        tvOrder.setText(String.valueOf(jml_order));

        btnTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

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
                                tvTanggal.setText(year+ "-" +String.valueOf(bul) + "-"+ mFormat.format(Double.valueOf(dayOfMonth)));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.setTitle("Tanggal Pengiriman");
                datePickerDialog.show();
            }
        });

        btnJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tvJam.setText( new DecimalFormat("00").format(Double.valueOf(selectedHour)) + ":" + new DecimalFormat("00").format(Double.valueOf(selectedMinute)) + ":00");
                    }
                }, mHour, mMinute, true);
                mTimePicker.setTitle("Jam Pengiriman");
                mTimePicker.show();
            }
        });

        RealmResults<Kurir> reskurir = getDefaultInstance().where(Kurir.class).findAll();
        List<Kurir> listkurir = getDefaultInstance().copyFromRealm(reskurir);

        SpinnerKurirAdapter adapterkurir = new SpinnerKurirAdapter(listkurir);
        spinkurir.setAdapter(adapterkurir);
        adapterkurir.notifyDataSetChanged();

        spinkurir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, final long id) {
                Kurir kurir = (Kurir) parent.getSelectedItem();
                setPilkurir(kurir.getId_kurir());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alertDialogBuilder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "tanggal kirim: " + tvTanggal.getText().toString());
                Log.i(TAG, "jam kirim: " + tvJam.getText().toString());
                getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        int pilkurir = 0;
                        pilkurir = getPilkurir();
                        DataProspek dpss = getDefaultInstance().where(DataProspek.class).equalTo("id_prospek", idp).findFirst();
                        if (!tvOrder.getText().toString().matches("")
                                && !tvTanggal.getText().toString().matches("")) {
                            dpss.setKurir(pilkurir);
                            String tanggal_kirim = (tvTanggal.getText().toString().matches("")) ? "" : tvTanggal.getText().toString();
                            String jam_kirim = (tvJam.getText().toString().matches("")) ? "00:00:00" : tvJam.getText().toString();
                            String waktu = tanggal_kirim + " " + jam_kirim;
                            Log.i(TAG, "Tanggal kirim: "+ waktu);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            if(!tanggal_kirim.matches("")) {
                                try {
                                    String tgl = sdf.format(sdf.parse(waktu));
                                    Date tgl_kurir = sdf.parse(tgl.toString());
                                    dpss.setKurir_tgl_kirim(tgl_kurir.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            dpss.setJmlorder(Integer.parseInt(tvOrder.getText().toString()));
                        }
                        getDefaultInstance().insertOrUpdate(dpss);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        buatPesanJendela("Sukses Update", "Data kurir berhasil diupdate");
                    }
                });
            }
        });

        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.create().show();
    }

    private void setPilkurir(int pilkurir){
        this.pilkurir = pilkurir;
    }

    private int getPilkurir(){
        return pilkurir;
    }

    private void setPilkonsul1(String pilkonsul1){
        this.pilkonsul1 = pilkonsul1;
    }

    private String getPilkonsul1(){
        return pilkonsul1;
    }

    private void setPilkonsul2(String pilkonsul2){
        this.pilkonsul2 = pilkonsul2;
    }

    private String getPilkonsul2(){
        return pilkonsul2;
    }

    private void pilihKonsultan(final DataProspek dp){

        LayoutInflater li = getActivity().getLayoutInflater();
        View promptsView = li.inflate(R.layout.pilih_penyuluh, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Pilih Konsultan :: " + dp.getTempat());
        alertDialogBuilder.setIcon(R.drawable.ic_account_box_black_36dp);
        final Spinner konsul1 = (Spinner) promptsView.findViewById(R.id.konsultan1);
        final Spinner konsul2 = (Spinner) promptsView.findViewById(R.id.konsultan2);

        final TextView tvTgl =(TextView) promptsView.findViewById(R.id.tgl_presentasi);
        final TextView tvJam =(TextView) promptsView.findViewById(R.id.jam_presentasi);

        final Button btnDatePicker=(Button) promptsView.findViewById(R.id.btn_datep);
        final Button btnTimePicker=(Button) promptsView.findViewById(R.id.btn_timep);

        final DataProspek dps = dp;
        final long idp = dps.getId_prospek();

        String tgl_penyuluh = (String.valueOf(dp.getTgl_penyuluhan()) == null || String.valueOf(dp.getTgl_penyuluhan()).matches("")) ? "" : String.valueOf(dp.getTgl_penyuluhan());
        String jam_penyuluh =  (dp.getWaktu_penyuluhan()==null || dp.getWaktu_penyuluhan().matches("")) ? "" : dp.getWaktu_penyuluhan();
        tvTgl.setText(tgl_penyuluh);
        tvJam.setText(jam_penyuluh);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

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
                            tvTgl.setText(year + "-" + String.valueOf(bul) + "-"+ mFormat.format(Double.valueOf(dayOfMonth)));
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.setTitle("Tanggal Penyuluhan");
            datePickerDialog.show();
            }
        });

        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    tvJam.setText( new DecimalFormat("00").format(Double.valueOf(selectedHour)) + ":" + new DecimalFormat("00").format(Double.valueOf(selectedMinute)) + ":00");
                }
            }, mHour, mMinute, true);
            mTimePicker.setTitle("Jam Penyuluhan");
            mTimePicker.show();
            }
        });

        // spinner konsultan
        // List<Konsultan> spinkonsul = new ArrayList<>();
        // RealmResults<Konsultan> resultKonsul = null;
        RealmResults<Konsultan> resultKonsul = getDefaultInstance().where(Konsultan.class).findAll();
        List<Konsultan> spinkonsul = getDefaultInstance().copyFromRealm(resultKonsul);

        SpinnerKonsultanAdapter isiadapter = new SpinnerKonsultanAdapter(spinkonsul);
        konsul1.setAdapter(isiadapter);
        isiadapter.notifyDataSetChanged();


        // ArrayAdapter<Konsultan> adapter1 = new ArrayAdapter<Konsultan>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, spinkonsul);
            // adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        konsul1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, final long id) {
                getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        DataProspek dp1 = bgRealm.where(DataProspek.class).equalTo("id_prospek", idp).findFirst();
                        Konsultan kons = (Konsultan) parent.getSelectedItem();
                        dp1.setKonsultan1(String.valueOf(kons.getId_konsultan()));
                        getDefaultInstance().insertOrUpdate(dp1);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        // buatPesanJendela("Sukses", "Data pemilihan Konsultan berhasil diupdate");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // ArrayAdapter<Konsultan> adapter2 = new ArrayAdapter<Konsultan>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, spinkonsul);
        konsul2.setAdapter(isiadapter);
        isiadapter.notifyDataSetChanged();
        konsul2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        DataProspek dp2 = bgRealm.where(DataProspek.class).equalTo("id_prospek", idp).findFirst();
                        Konsultan kons2 = (Konsultan) parent.getSelectedItem();
                        dp2.setKonsultan2(String.valueOf(kons2.getId_konsultan()));
                        getDefaultInstance().insertOrUpdate(dp2);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        // buatPesanJendela("Sukses", "Data pemilihan Pendamping Konsultan berhasil diupdate");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        DataProspek dpss = bgRealm.where(DataProspek.class).equalTo("id_prospek", idp).findFirst();
                        if (!tvTgl.getText().toString().matches("") && !tvJam.getText().toString().matches("")) {
                            String tanggal_penyuluhan = tvTgl.getText().toString() + " " + tvJam.getText().toString();
                            // Commons.toDate(tvTgl.getText().toString())
                            dpss.setTgl_penyuluhan(Commons.toDate(tanggal_penyuluhan));
                            dpss.setWaktu_penyuluhan(tvJam.getText().toString());
                        }
                        getDefaultInstance().insertOrUpdate(dpss);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        buatPesanJendela("Update Konsultan", "Data berhasil diupdate.");
                    }
                });
            }
        });

        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.create().show();

    /*
    final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    */

    }

    private ArrayList<String> getKonsultanValue(){
        ArrayList<String> konsultanlist=new ArrayList<>();
        RealmResults<Konsultan> kons=realm.where(Konsultan.class).findAll();
        for(Konsultan s: kons)
        {
            konsultanlist.add(s.getNama_konsultan());
        }
        return konsultanlist;
    }

    private void buatPesanJendela(String judul, String isi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle(judul);
        builder.setMessage(isi)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void updateDataByKoor(final long id_prospek){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Validasi Data oleh Koordinator");
        builder.setMessage("Validasi data ini ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                DataProspek dps = bgRealm.where(DataProspek.class).equalTo("id_prospek", id_prospek).findFirst();
                                if (dps == null) {
                                    dps = new DataProspek();
                                    dps.setStatus_koor(true);
                                }
                                dps.setStatus_koor(true);
                                getDefaultInstance().insertOrUpdate(dps);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                buatPesanJendela("Validasi Koordinator", "Data sudah dicek dan divalidasi oleh Koordinator");
                            }
                        });

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void updateDataByMA(final long id_prospek){
        /*
        if (sharedpreferences.contains(Name)) {
            int tipe_user = sharedpreferences.getInt("tipe_user", "");
        }
        */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Validasi Data oleh Manager Area");
        builder.setMessage("Validasi data ini ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                DataProspek dps = bgRealm.where(DataProspek.class).equalTo("id_prospek", id_prospek).findFirst();
                                if (dps == null) {
                                    dps = new DataProspek();
                                    dps.setStatus_ma(true);
                                }
                                dps.setStatus_ma(true);
                                getDefaultInstance().insertOrUpdate(dps);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                buatPesanJendela("Validasi Manager Area", "Data sudah dicek dan divalidasi oleh Manager Area");
                            }
                        });

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void kirimDataServer(DataProspek dp){
        Commons cmn = new Commons(getContext());
        final long idprospek = dp.getId_prospek();
        final int usid = uid;
        final int guid = dp.getUid();
        // final int kuid = Integer.parseInt(dp.getKonsultan1());
        final String konsul = (dp.getKonsultan1().matches("") || dp.getKonsultan1().matches("null") || dp.getKonsultan1() == null) ? "0" : dp.getKonsultan1();

        final DataProspek dpr = realm.where(DataProspek.class).equalTo("id_prospek", idprospek).findFirst();
        GsonBuilder gsonBuilder = new GsonBuilder().setLenient();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });

        RealmResults<FotoProspek> relfotop = realm.where(FotoProspek.class)
                .equalTo("uuid", dp.getUuid())
                .equalTo("is_dikirim", false)
                .findAll();
        List<FotoProspek> listfoto = realm.copyFromRealm(relfotop);

        RealmResults<FotoPresentasi> relfotopresen = realm.where(FotoPresentasi.class)
                .equalTo("uuid", dp.getUuid())
                .equalTo("is_dikirim", false)
                .findAll();
        List<FotoPresentasi> listfotopresen = realm.copyFromRealm(relfotopresen);

        try {
            gsonBuilder.registerTypeAdapter(Class.forName("io.realm.DataProspekRealmProxy"), new DataProspekSerializer());
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(dpr);
            Log.i(TAG, "Data json yang mau dikirim ke server: " + json);
            if(cmn.isNetworkConnected()){
                if((usid > guid) && (usid > Integer.parseInt(konsul)) && (tipe_user != 4) && (tipe_user != 2)){
                    buatPesanJendela("Error", "Anda tidak berhak mengakses data ini.");
                } else {
                    // upload foto presentasi
                    if(listfotopresen.size() > 0) {
                        int i = 1;
                        for (FotoPresentasi fotos : listfotopresen) {
                            final StringBuilder sb = new StringBuilder()
                                    .append("k_")
                                    .append(String.valueOf(fotos.getId())).append("_")
                                    .append(dp.getUuid())
                                    .append("_")
                                    .append(fotos.getFilename());
                            Log.i(TAG, "String foto presentasi: " + sb.toString());
                            new UploadFile(getContext(), sb.toString(), fotos.getUri_foto(), new UploadFile.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    if(output == null){
                                        Log.i(TAG, "upload file presentasi error");
                                    } else {
                                        final JSONObject jsonObject;
                                        try {
                                            jsonObject = new JSONObject(output);
                                            if(jsonObject.has("success") && jsonObject.getBoolean("success")) {
                                                final String namafile = jsonObject.getString("file");
                                                final String uuid = jsonObject.getString("uuid");
                                                final String fuid = jsonObject.getString("id");
                                                realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm bgRealm) {
                                                        FotoPresentasi fps = getDefaultInstance().where(FotoPresentasi.class).equalTo("id", Integer.parseInt(fuid)).findFirst();
                                                        if(fps != null)
                                                        {
                                                            fps.setIs_dikirim(true);
                                                            fps.setUuid(uuid);
                                                            fps.setFilename(namafile);
                                                            getDefaultInstance().insertOrUpdate(fps);
                                                        }
                                                    }
                                                }, new Realm.Transaction.OnSuccess() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Log.i(TAG, "Sukses upload foto Presentasi.");
                                                    }
                                                });
                                                // Log.i(TAG, "upload file" + sb.toString());
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).execute();
                            i++;
                        }
                    }

                    // upload foto prospek
                    if(listfoto.size() > 0) {
                        int i = 1;
                        for (FotoProspek foto : listfoto) {
                            final StringBuilder sb = new StringBuilder()
                                    .append("p_")
                                    .append(String.valueOf(foto.getId())).append("_")
                                    .append(dp.getUuid())
                                    .append("_")
                                    .append(foto.getFilename());
                            Log.i(TAG, "String foto prospek: " + sb.toString());
                            new UploadFile(getContext(), sb.toString(), foto.getUri_foto(), new UploadFile.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    /*
                                    if(output == null){
                                        Log.i(TAG, "upload file error");
                                    } else if(output.matches("ok")){
                                        Log.i(TAG, "upload file" + sb.toString());
                                    }
                                    */
                                    if(output == null){
                                        Log.i(TAG, "upload file prospek error");
                                    } else {
                                        final JSONObject jsonObject;
                                        try {
                                            jsonObject = new JSONObject(output);
                                            if(jsonObject.has("success") && jsonObject.getBoolean("success")) {
                                                final String namafile = jsonObject.getString("file");
                                                final String uuid = jsonObject.getString("uuid");
                                                final String fuid = jsonObject.getString("id");
                                                realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm bgRealm) {
                                                        FotoProspek fps = getDefaultInstance().where(FotoProspek.class).equalTo("id", Integer.parseInt(fuid)).findFirst();
                                                        if(fps != null)
                                                        {
                                                            fps.setIs_dikirim(true);
                                                            fps.setUuid(uuid);
                                                            fps.setFilename(namafile);
                                                            getDefaultInstance().insertOrUpdate(fps);
                                                        }
                                                    }
                                                }, new Realm.Transaction.OnSuccess() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Log.i(TAG, "Sukses upload foto Prospek.");
                                                    }
                                                });
                                                // Log.i(TAG, "upload file" + sb.toString());
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).execute();
                            i++;
                        }
                    }

                    new AsyncPostJson(getContext(), Config.URL_POST_DATA_CATAT, "pencatatan", json, new AsyncPostJson.AsyncPostResponse() {
                        @Override
                        public void processFinish(String output) {
                            if(!output.matches("")) {
                                try {
                                    final JSONObject jsonObject = new JSONObject(output);
                                    if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                                        realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm bgRealm) {
                                                Log.i(TAG, "idprospek: "+idprospek);
                                                DataProspek dps = getDefaultInstance().where(DataProspek.class).equalTo("id_prospek", idprospek).findFirst();
                                                if (dps == null) {
                                                    dps = new DataProspek();
                                                    dps.setIs_dikirim(true);
                                                }
                                                dps.setIs_dikirim(true);
                                                getDefaultInstance().insertOrUpdate(dps);
                                            }
                                        }, new Realm.Transaction.OnSuccess() {
                                            @Override
                                            public void onSuccess() {
                                                try {
                                                    buatPesanJendela("Sukses", jsonObject.getString("pesan"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } else {
                                        buatPesanJendela("Error", jsonObject.getString("pesan"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                buatPesanJendela("Error", "Server tidak merespon!");
                            }
                        }
                    }).execute();
                }

            } else {
                buatPesanJendela("Error", "Silahkan cek koneksi jaringan Internet anda!");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void updateStatus(DataProspek dp){
        LayoutInflater li = getActivity().getLayoutInflater();
        View promptsView = li.inflate(R.layout.updatestatus, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final int usid = uid;
        final int guid = dp.getUid();
        final String konsul = dp.getKonsultan1();

        String[] strings = new String [] {"Data tidak valid",
                "Dalam Konfirmasi (on-progress)",
                "Jadwal Penyuluhuan Terkonfirmasi (fix)",
                "Sudah dilakukan Penyuluhan",
                "Batal dilakukan penyuluhan (cancel)"
            };

            List<String> stringList = new ArrayList<String>(Arrays.asList(strings));

        RadioGroup rg = (RadioGroup) promptsView.findViewById(R.id.pilihan_status);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb = new RadioButton(getActivity());
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Update Status :: " + dp.getTempat());
        alertDialogBuilder.setIcon(R.drawable.ic_assignment_black_36dp);
        final long idp = dp.getId_prospek();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked)
                {
                    final int position = group.indexOfChild(checkedRadioButton);
                    getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            DataProspek dps = getDefaultInstance().where(DataProspek.class).equalTo("id_prospek", idp).findFirst();
                            if (dps != null) {
                                dps.setStatus_prospek(position + 1);
                            }
                            getDefaultInstance().insertOrUpdate(dps);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            // buatPesanJendela("Sukses", "Status berhasil diupdate");
                            Log.i(TAG, "Status penyuluhan berhasil diupdate.");
                        }
                    });
                }
            }
        });

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                if(konsul.matches("") || konsul.matches("null") || konsul == null)
                {
                    buatPesanJendela("Error", "Silahkan pilih konsultan terlebih dahulu.");
                } else {
                    if(usid > Integer.parseInt(konsul) || tipe_user != 4){
                        buatPesanJendela("Error", "Anda tidak berhak mengupdate status data ini.");
                    } else {
                        buatPesanJendela("Sukses", "Status penyuluhan berhasil diupdate");
                    }
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.create().show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        // Associate searchable configuration with the SearchView
        inflater.inflate(R.menu.options_menu, menu);
        this.menu = menu;

        /*
        getMenuInflater().inflate(R.menu.listview_options, menu);
        menu.setGroupVisible(R.id.group_normal_mode, true);
        menu.setGroupVisible(R.id.group_delete_mode, false);
        */

        MenuItem item = menu.findItem(R.id.search);
        SearchView  searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setQueryHint("tempat, kontak, pendata, konsultan");
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange: " + newText);

                OrderedRealmCollection<DataProspek> result = realm.where(DataProspek.class)
                        .contains("tempat", newText, Case.INSENSITIVE)
                        .or()
                        .contains("nama", newText, Case.INSENSITIVE)
                        .findAll().sort("tgl_catat", Sort.DESCENDING);

                setDataProspek(result);

                adapter.setData(result);
                adapter.notifyDataSetChanged();

                /*
                Error: adapter.getFilter().filter(newText);
                */

                return true;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
              }
          }
        );

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //some operation
                return false;
            }
        });

        /*
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        */
    }

    private void sortirData(){
        LayoutInflater li = getActivity().getLayoutInflater();
        View promptsView = li.inflate(R.layout.updatestatus, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        String[] strings = new String [] {"Tanggal Terakhir Pencatatan",
                "Tanggal pencatatan berdasarkan periode",
                "Jadwal Penyuluhan berdasarkan tanggal",
                "Status Penyuluhan yang terkonfirmasi",
                "Data yang sudah dilakukan penyuluhan",
                "Data pencatatan saya"
        };

        List<String> stringList = new ArrayList<String>(Arrays.asList(strings));

        RadioGroup rg = (RadioGroup) promptsView.findViewById(R.id.pilihan_status);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb = new RadioButton(getActivity());
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Urutkan Data Pencatatan berdasarkan: ");
        alertDialogBuilder.setIcon(R.drawable.ic_filter_list_black_36dp);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {

            }
        });

        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.create().show();

    }

    private void refreshData(){
        String imei = getImei();
        new AmbilData(getContext(), uid, tipe_user, imei, Config.URL_GETDATA_CATAT, new AmbilData.AsyncPostResponse(){
            @Override
            public void processFinish(String output) {
                InputStream stream = null;
                Realm realm = getDefaultInstance();
                realm.beginTransaction();
                Log.i(TAG, "data dari server: "+output);
                stream = new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
                if (stream != null) {
                    try {
                        realm.createOrUpdateAllFromJson(DataProspek.class, stream);
                        realm.commitTransaction();
                    } finally {
                        try {
                            stream.close();
                            adapter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).execute();
    }

    public String getImei() {
        TelephonyManager mngr = (TelephonyManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.refresh:
                refreshData();
                return true;
            case R.id.filter:
                sortirData();
                return true;

            /*
            case R.id.action_start_delete_mode:
                adapter.enableDeletionMode(true);
                menu.setGroupVisible(R.id.group_normal_mode, false);
                menu.setGroupVisible(R.id.group_delete_mode, true);
                return true;
            case R.id.action_end_delete_mode:
                DataHelper.deleteItemsAsync(realm, adapter.getCountersToDelete());
            case R.id.action_cancel_delete_mode:
                adapter.enableDeletionMode(false);
                menu.setGroupVisible(R.id.group_normal_mode, true);
                menu.setGroupVisible(R.id.group_delete_mode, false);
            */
        }
        return super.onOptionsItemSelected(item); // important line
    }

    OrderedRealmCollection<DataProspek> getAllData(){
        RealmResults<DataProspek> data = realm.where(DataProspek.class)
                .findAll()
                .sort("tgl_catat", Sort.DESCENDING);
        return data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        realm.close();
        if (swipeLayout!=null) {
            swipeLayout.setRefreshing(false);
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    private void loadKonsultanFromStream() throws IOException {
        new AsyncTask<Object, Object, Void>() {

            @Override
            protected Void doInBackground(Object... params) {
                InputStream stream = null;
                Realm realm = getDefaultInstance();
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


    private void loadKurirFromStream() throws IOException {
        new AsyncTask<Object, Object, Void>() {

            @Override
            protected Void doInBackground(Object... params) {
                InputStream stream = null;
                Realm realm = getDefaultInstance();
                realm.beginTransaction();
                try {
                    stream = new FileInputStream(new File(Config.FILE_KURIR).getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    realm.createOrUpdateAllFromJson(Kurir.class, stream);
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


    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DataProspek item);
        RealmResults<DataProspek> getDataProspek();
    }

    private class TouchHelperCallback extends ItemTouchHelper.SimpleCallback {

        TouchHelperCallback() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Apakah anda mau menghapus data ini?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            DataHelper.deleteItemAsync(realm, viewHolder.getItemId());
                        }
                    })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });
            final AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    }

    private OrderedRealmCollection<DataProspek> getDataProspek(){
        return dataProspek;
    }

    private void setDataProspek(OrderedRealmCollection<DataProspek> dataProspeks){
        this.dataProspek = dataProspeks;
    }

    public void takePhoto(String filename) throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            Log.i(TAG, "Rencana namafile foto: "+ Config.FOTO_DIR + filename);
            // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            imageUri = Uri.fromFile(tempfile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            getActivity().startActivityForResult(intent, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {

                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    if(bitmap != null) {
                        // new ImageCompressionAsyncTask(getContext()).execute(bitmap.toString(), photo.getAbsolutePath());
                    } else {
                        Toast.makeText(getContext(), "data inten kosong", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    private File createMediaFile(int type) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = type == 1 ? "JPEG_" + timeStamp + "_" : "VID_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                type == 1 ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_MOVIES);
        File file = File.createTempFile(
                fileName,  /* prefix */
                type == 1 ? ".jpg" : ".mp4",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + file.getAbsolutePath();
        Log.d(TAG, "mCurrentPhotoPath: " + mCurrentPhotoPath);
        return file;
    }


    private Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=800;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    class CacheClearAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Glide.get(getActivity()).clearDiskCache();
            return null;
        }

        @Override
        protected void onPostExecute (Void result)    {
            Glide.get(getActivity()).clearMemory();
        }
    }


    private class SpinnerKonsultanAdapter extends BaseAdapter implements SpinnerAdapter {

        /**
         * The internal data (the ArrayList with the Objects).
         */
        private final List<Konsultan> data;

        public SpinnerKonsultanAdapter(List<Konsultan> data){
            this.data = data;
        }

        /**
         * Returns the Size of the ArrayList
         */
        @Override
        public int getCount() {
            return data.size();
        }

        /**
         * Returns one Element of the ArrayList
         * at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Returns the View that is shown when a element was
         * selected.
         */
        @Override
        public View getView(int position, View recycle, ViewGroup parent) {
            TextView text;
            if (recycle != null){
                text = (TextView) recycle;
            } else {
                text = (TextView) getActivity().getLayoutInflater().inflate(
                        android.R.layout.simple_dropdown_item_1line, parent, false
                );
            }
            text.setTextColor(Color.BLACK);
            text.setText(data.get(position).getNama_konsultan());
            return text;
        }
    }


    // spinner kurir adapter

    private class SpinnerKurirAdapter extends BaseAdapter implements SpinnerAdapter {

        /**
         * The internal data (the ArrayList with the Objects).
         */
        private final List<Kurir> data;

        public SpinnerKurirAdapter(List<Kurir> data){
            this.data = data;
        }

        /**
         * Returns the Size of the ArrayList
         */
        @Override
        public int getCount() {
            return data.size();
        }

        /**
         * Returns one Element of the ArrayList
         * at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Returns the View that is shown when a element was
         * selected.
         */
        @Override
        public View getView(int position, View recycle, ViewGroup parent) {
            TextView text;
            if (recycle != null){
                text = (TextView) recycle;
            } else {
                text = (TextView) getActivity().getLayoutInflater().inflate(
                        android.R.layout.simple_list_item_1, parent, false
                );
            }
            text.setTextColor(Color.BLACK);
            text.setText(data.get(position).getNama_kurir());
            return text;
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }


}