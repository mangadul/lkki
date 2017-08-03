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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.alphamedia.lkki.adapter.DividerItemDecoration;
import id.alphamedia.lkki.adapter.ItemClickSupport;
import id.alphamedia.lkki.adapter.MyListDataRecyclerViewAdapter;
import id.alphamedia.lkki.models.DataHelper;
import id.alphamedia.lkki.models.DataProspek;
import id.alphamedia.lkki.models.DataProspekSerializer;
import id.alphamedia.lkki.models.FotoProspek;
import id.alphamedia.lkki.models.Konsultan;
import id.alphamedia.lkki.tools.GlideToFile;
import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

import static id.alphamedia.lkki.BaseFragment.ARGS_INSTANCE;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListDataFragment extends Fragment {

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
        realm = Realm.getDefaultInstance();
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
            recyclerView.setLayoutManager(mLayoutManager);

            RealmResults<Konsultan> konsultanRealmResults =  realm.where(Konsultan.class).findAll();
            if(konsultanRealmResults.size() == 0)
            {
                try {
                    loadKonsultanFromStream();
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
                                    jadwalPenyuluhan(dp);
                                    break;
                                case 2:
                                    break; // lihatData
                                case 3:
                                    kirimDataServer(dp);
                                    break;
                                case 4:
                                    updateDataByKoor(dp.getId_prospek());
                                    break;
                                case 5:
                                    updateDataByMA(dp.getId_prospek());
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

    private void ambilFoto(final DataProspek dp) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String currentDateandTime = sdf.format(new Date()) + ".jpg";
        File photo = new File(Config.FOTO_DIR,  currentDateandTime);
        photo.createNewFile();
        tempfile = createMediaFile(MODE_FOTO);
        final String foto_lok = photo.getAbsolutePath();
        final String temp_foto = tempfile.getAbsolutePath();
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
                            getContext().deleteFile(tmpfile.getAbsolutePath());
                        }
                    }
                }, 500);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Number maxValue = bgRealm.getDefaultInstance().where(FotoProspek.class).max("id");
                int pk = ((maxValue == null) && (String.valueOf(maxValue).contains("null")) ) ? 1 : maxValue.intValue() + 1;
                Log.i(TAG, "nilai uuid di ambilFoto: " + uuid);
                FotoProspek fotoProspek = new FotoProspek();
                fotoProspek.setId(pk);
                fotoProspek.setUuid(uuid);
                fotoProspek.setIs_dikirim(false);
                fotoProspek.setUri_foto(currentDateandTime);

                // insert update foto
                realm.getDefaultInstance().insert(fotoProspek);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                buatPesanJendela("Sukses", "Foto baru telah ditambahkan dengan nama file "+currentDateandTime);
            }
        });


    }

    private void jadwalPenyuluhan(final DataProspek dp){

        LayoutInflater li = getActivity().getLayoutInflater();
        View promptsView = li.inflate(R.layout.pilih_penyuluh, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle(dp.getTempat());
        alertDialogBuilder.setIcon(R.drawable.ic_account_box_black_36dp);
        final Spinner konsul1 = (Spinner) promptsView.findViewById(R.id.konsultan1);
        final Spinner konsul2 = (Spinner) promptsView.findViewById(R.id.konsultan2);

        final TextView tvTgl =(TextView) promptsView.findViewById(R.id.tgl_presentasi);
        final TextView tvJam =(TextView) promptsView.findViewById(R.id.jam_presentasi);

        final Button btnDatePicker=(Button) promptsView.findViewById(R.id.btn_datep);
        final Button btnTimePicker=(Button) promptsView.findViewById(R.id.btn_timep);

        final DataProspek dps = dp;
        final long idp = dps.getId_prospek();

        String tgl_penyuluh = (dp.getTgl_penyuluhan()== null || dp.getTgl_penyuluhan().matches("")) ? "" : dp.getTgl_penyuluhan();
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
                                tvTgl.setText(mFormat.format(Double.valueOf(dayOfMonth)) + "-" + String.valueOf(bul) + "-" + year);
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
                        tvJam.setText( new DecimalFormat("00").format(Double.valueOf(selectedHour)) + ":" + new DecimalFormat("00").format(Double.valueOf(selectedMinute)));
                    }
                }, mHour, mMinute, true);
                mTimePicker.setTitle("Jam Penyuluhan");
                mTimePicker.show();
            }
        });

        // spinner konsultan
        // List<Konsultan> spinkonsul = new ArrayList<>();
        // RealmResults<Konsultan> resultKonsul = null;
        RealmResults<Konsultan> resultKonsul = realm.getDefaultInstance().where(Konsultan.class).findAll();
        List<Konsultan> spinkonsul = realm.getDefaultInstance().copyFromRealm(resultKonsul);

        SpinnerKonsultanAdapter isiadapter = new SpinnerKonsultanAdapter(spinkonsul);
        konsul1.setAdapter(isiadapter);
        isiadapter.notifyDataSetChanged();


        // ArrayAdapter<Konsultan> adapter1 = new ArrayAdapter<Konsultan>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, spinkonsul);
            // adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        konsul1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, final long id) {
                realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        DataProspek dp1 = bgRealm.where(DataProspek.class).equalTo("id_prospek", idp).findFirst();
                        Konsultan kons = (Konsultan) parent.getSelectedItem();
                        dp1.setKonsultan1(String.valueOf(kons.getId_konsultan()));
                        realm.getDefaultInstance().insertOrUpdate(dp1);
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
                realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        DataProspek dp2 = bgRealm.where(DataProspek.class).equalTo("id_prospek", idp).findFirst();
                        Konsultan kons2 = (Konsultan) parent.getSelectedItem();
                        dp2.setKonsultan2(String.valueOf(kons2.getId_konsultan()));
                        realm.getDefaultInstance().insertOrUpdate(dp2);
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
                realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        DataProspek dpss = bgRealm.where(DataProspek.class).equalTo("id_prospek", idp).findFirst();
                        if (!tvTgl.getText().toString().matches("") && !tvJam.getText().toString().matches("")) {
                            dpss.setTgl_penyuluhan(tvTgl.getText().toString());
                            dpss.setWaktu_penyuluhan(tvJam.getText().toString());
                        }
                        realm.getDefaultInstance().insertOrUpdate(dpss);
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
                        realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                DataProspek dps = bgRealm.where(DataProspek.class).equalTo("id_prospek", id_prospek).findFirst();
                                if (dps == null) {
                                    dps = new DataProspek();
                                    dps.setStatus_koor(true);
                                }
                                dps.setStatus_koor(true);
                                realm.getDefaultInstance().insertOrUpdate(dps);
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
                        realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                DataProspek dps = bgRealm.where(DataProspek.class).equalTo("id_prospek", id_prospek).findFirst();
                                if (dps == null) {
                                    dps = new DataProspek();
                                    dps.setStatus_ma(true);
                                }
                                dps.setStatus_ma(true);
                                realm.getDefaultInstance().insertOrUpdate(dps);
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

        try {
            gsonBuilder.registerTypeAdapter(Class.forName("io.realm.DataProspekRealmProxy"), new DataProspekSerializer());
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(dpr);
            Log.i(TAG, "Data json yang mau dikirim ke server: " + json);
            if(cmn.isNetworkConnected()){
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
                                            DataProspek dps = bgRealm.getDefaultInstance().where(DataProspek.class).equalTo("id_prospek", idprospek).findFirst();
                                            if (dps == null) {
                                                dps = new DataProspek();
                                                dps.setIs_dikirim(true);
                                            }
                                            dps.setIs_dikirim(true);
                                            bgRealm.getDefaultInstance().insertOrUpdate(dps);
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
            } else {
                buatPesanJendela("Error", "Silahkan cek koneksi jaringan Internet anda!");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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
        searchView.setQueryHint("Tempat, Kontak");
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
                        .findAll().sort("tempat", Sort.ASCENDING);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
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
                .sort("tempat", Sort.ASCENDING);
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


}
