package id.alphamedia.lkki.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import id.alphamedia.lkki.R;
import id.alphamedia.lkki.models.DataProspek;
import id.alphamedia.lkki.models.Desa;
import id.alphamedia.lkki.models.Kabupaten;
import id.alphamedia.lkki.models.Kecamatan;
import id.alphamedia.lkki.models.Konsultan;
import id.alphamedia.lkki.models.Provinsi;
import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

// import id.alphamedia.lkki.ListDataFragment.OnListFragmentInteractionListener;


public class MyListDataRecyclerViewAdapter
        extends RealmRecyclerViewAdapter<DataProspek, MyListDataRecyclerViewAdapter.ViewHolder>
        implements Filterable {

    private static final String TAG = "MyListDataRecyclerView";

    OrderedRealmCollection<DataProspek> mValues;

    private boolean inDeletionMode = false;

    Context context;

    Realm realm;
    int posisi;

    private Set<Long> countersToDelete = new HashSet<Long>();

    SharedPreferences sharedpreferences;

    AlertDialog.Builder alertDialogBuilder;

    public MyListDataRecyclerViewAdapter(Context context, Realm realm, OrderedRealmCollection<DataProspek> data) {
        super(data, true, true);
        this.mValues = data;
        this.realm = realm;
        this.context = context;
        setHasStableIds(true);
    }

    public void setData(OrderedRealmCollection<DataProspek> data) {
        this.mValues = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_listdata, parent, false);

        alertDialogBuilder = new AlertDialog.Builder(context.getApplicationContext());

        /*
        sharedpreferences = context.getApplicationContext().getSharedPreferences(Config.PREFSDATA,
                Context.MODE_PRIVATE);
           */

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final DataProspek dp = mValues.get(position);
        posisi = position;

        Desa desa_kel = realm.where(Desa.class).equalTo("id", Long.parseLong(dp.getDesa())).findFirst();
        Kecamatan kecamatan = realm.where(Kecamatan.class).equalTo("id", Integer.parseInt(dp.getKecamatan())).findFirst();
        Kabupaten kab = realm.where(Kabupaten.class).equalTo("id", Integer.parseInt(dp.getKota())).findFirst();
        Provinsi provinsi = realm.where(Provinsi.class).equalTo("id", Integer.parseInt(dp.getProvinsi())).findFirst();

        if(dp.getKonsultan1() == null || dp.getKonsultan1().matches("")) {
            holder.konsultan.setText("Konsultan: <konsultan_blm_dipilih>");
        } else {
            Konsultan konsul = realm.where(Konsultan.class).equalTo("id_konsultan", Integer.parseInt(dp.getKonsultan1())).findFirst();

            if(dp.getKonsultan2() == null || dp.getKonsultan2().matches("")) {
            } else {
                Konsultan konsul2 = realm.where(Konsultan.class).equalTo("id_konsultan", Integer.parseInt(dp.getKonsultan2())).findFirst();
            }

            // konsultan
            if(dp.getKonsultan1().matches("")) {
                holder.konsultan.setTextColor(Color.RED);
            } else {
                holder.konsultan.setTextColor(Color.BLACK);
            }

            String kons1 = "";

            if(konsul == null)
            {
                kons1 = "<konsultan_blm_dipilih>";
            } else {
                kons1 = (konsul.getNama_konsultan().matches("") || konsul.getNama_konsultan().matches("null"))
                        ?  "<konsultan_blm_dipilih>"
                        : konsul.getNama_konsultan();
            }

            StringBuilder sbKon = new StringBuilder()
                    .append("Konsultan:: ")
                    .append(kons1);

            holder.konsultan.setText(sbKon);
        }

        String tjalan = (dp.getJalan() == null || dp.getJalan().matches("null") || dp.getJalan().matches("")) ? "<jln_kosong>": dp.getJalan();
        StringBuilder lokasi = new StringBuilder();
        lokasi.append(tjalan);

        String tdesa = "<desa_kosong>";
        StringBuilder desa = new StringBuilder();
        if(desa_kel != null) {
            tdesa = (desa_kel.getName() == null || desa_kel.getName().matches("null") || desa_kel.getName().matches("")) ? "<desa_kosong>": desa_kel.getName();
        }
        desa.append(tdesa);

        String tkec = "<kec_kosong>";
        StringBuilder kec = new StringBuilder();
        if(kecamatan != null) {
            tkec = (kecamatan.getName() == null || kecamatan.getName().matches("null") || kecamatan.getName().matches("")) ? "<kec_kosong>": kecamatan.getName();
        }
        kec.append(tkec);

        String tkab = "<kab_kosong>";
        if(kab != null) {
            tkab = (kab.getName() == null || kab.getName().matches("null") || kab.getName().matches("")) ? "<kab_kosong>": kab.getName();
        }

        String tprov = "<prov_kosong>";
        if(provinsi != null) {
            tprov = (provinsi.getNama_prov() == null || provinsi.getNama_prov().matches("null") || provinsi.getNama_prov().matches("")) ? "<prov_kosong>": kab.getName();
        }

        StringBuilder kab_prov = new StringBuilder();
        kab_prov.append(tkab)
                .append(", ")
                .append(tprov);

        holder.mItem = getData().get(position); // mValues.get(position);

        String uid = dp.getId_prospek() + "/" +dp.getUuid();
        holder.uuid.setText(uid.substring(0, 30));
        String nokantor = (dp.getNo_kantor() == null) ? "": " / " + dp.getNo_kantor();

        holder.tempat.setText(dp.getTempat());
        holder.jabatan.setText(dp.getJabatan());
        holder.kontak.setText(dp.getNama() + " (" + dp.getNo_hp() +")" + nokantor);
        holder.alamat.setText(lokasi);
        holder.desa_kel.setText(desa);
        holder.kecamatan.setText(kec);

        StringBuilder sbPend = new StringBuilder()
                .append("Pendata: ")
                .append(dp.getNama_pencatat());

        holder.pendata.setText(sbPend);

        holder.kecamatan.setText(kec);

        holder.kab_provinsi.setText(kab_prov);
        holder.tgl_catat.setText(String.valueOf(dp.getTgl_catat()));

        String is_online = (dp.isIs_dikirim()) ? "[online]" : "[offline]";

        if(dp.isIs_dikirim()) {
            holder.is_online.setTextColor(Color.BLUE);
            holder.is_online.setText(is_online);
        } else {
            holder.is_online.setTextColor(Color.GRAY);
            holder.is_online.setText(is_online);
        }

        String tgl_presen = (dp.getTgl_penyuluhan() == null || String.valueOf(dp.getTgl_penyuluhan()).matches("")) ? "<tgl_penyuluhan_belum_disi>" : String.valueOf(dp.getTgl_penyuluhan());
        String jam_presentasi = (dp.getWaktu_penyuluhan() == null || dp.getWaktu_penyuluhan().matches("")) ? "<jam_belum_disi>": dp.getWaktu_penyuluhan();

        StringBuilder presentasi = new StringBuilder()
                .append(tgl_presen)
                .append(" ")
                .append(jam_presentasi);

        holder.tgl_presentasi.setText(presentasi);

        String status_koordinator = dp.isStatus_koor() ? "[DIVALIDASI KOORDINATOR]" : "[BELUM DICEK KOOR]";
        String status_ma = dp.isStatus_ma() ? "[DIVALIDASI MANAGER AREA]" : "[BELUM DICEK MA]";

        // String status_presentasi = dp.isStatus_presentasi() ? "[SUDAH]" : "[BELUM]";
        String status_presentasi = (dp.getStatus_prospek() == 4) ? "[SUDAH]" : "[BELUM]";

        StringBuilder latlng = new StringBuilder()
                .append("Lat/Long: ")
                .append(dp.getLokasi_lat())
                .append(",")
                .append(dp.getLokasi_long());

        // status presentasi
        if(dp.getStatus_prospek() == 4) {
            holder.status_presentasi.setTextColor(Color.BLUE);
            holder.status_presentasi.setText(status_presentasi);
        } else {
            holder.status_presentasi.setTextColor(Color.RED);
            holder.status_presentasi.setText(status_presentasi);
        }

        // status koordinator
        if(dp.isStatus_koor()) {
            holder.status_koor.setTextColor(Color.BLUE);
            holder.status_koor.setText(status_koordinator);
        } else {
            holder.status_koor.setTextColor(Color.RED);
            holder.status_koor.setText(status_koordinator);
        }

        // status status MA
        if(dp.isStatus_ma()) {
            holder.status_ma.setTextColor(Color.BLUE);
            holder.status_ma.setText(status_ma);
        } else {
            holder.status_ma.setTextColor(Color.RED);
            holder.status_ma.setText(status_ma);
        }

    }

    public List<DataProspek> getList() {
        return getData();
    }

    /*
    @Override
    public DataProspek getItem(int position){
        return mValues.get(position);
    }
    */

    @Override
    public OrderedRealmCollection<DataProspek> getData(){
        return mValues;
    }

    private void setDataList(OrderedRealmCollection<DataProspek> data){
        this.mValues = data;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Set<Long> getCountersToDelete() {
        return countersToDelete;
    }

    public void enableDeletionMode(boolean enabled) {
        inDeletionMode = enabled;
        if (!enabled) {
            countersToDelete.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        DataProspekFilter filter = new DataProspekFilter(this);
        return filter;
    }

    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        Log.i(TAG, "teks yang dicari: " + text);
        if(text == null || "".equals(text)) {
            updateData(realm.where(DataProspek.class).findAll().sort("tempat", Sort.ASCENDING));
        } else {
            updateData(realm.where(DataProspek.class)
                    .contains("tempat", text, Case.INSENSITIVE)
                    .or()
                    .contains("nama", text, Case.INSENSITIVE)
                    .findAll()
                    .sort("tempat", Sort.ASCENDING));
        }
    }

    // filter class
    private class DataProspekFilter extends Filter
    {
        private final MyListDataRecyclerViewAdapter adapter;

        private DataProspekFilter(MyListDataRecyclerViewAdapter adapter)
        {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tempat;
        public final TextView alamat;
        public final TextView kontak;
        public final TextView tgl_presentasi;
        public final TextView status_presentasi;
        public final TextView desa_kel;
        public final TextView kecamatan;
        public final TextView status_koor;
        public final TextView status_ma;
        public final TextView kab_provinsi;
        public final TextView tgl_catat;
        public final TextView uuid;
        public final TextView jabatan;
        public final TextView is_online;

        public final TextView pendata;
        public final TextView konsultan;

        public DataProspek mItem;
        public final CheckBox deletedCheckBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            uuid = (TextView) view.findViewById(R.id.id_prospek);
            is_online = (TextView) view.findViewById(R.id.is_online);
            tempat = (TextView) view.findViewById(R.id.tempat);
            jabatan = (TextView) view.findViewById(R.id.jabatan);
            alamat = (TextView) view.findViewById(R.id.alamat);
            desa_kel = (TextView) view.findViewById(R.id.desa_kelurahan);
            kecamatan = (TextView) view.findViewById(R.id.kecamatan);
            kab_provinsi = (TextView) view.findViewById(R.id.kab_provinsi);
            kontak = (TextView) view.findViewById(R.id.kontak);
            tgl_presentasi = (TextView) view.findViewById(R.id.tgl_presentasi);
            status_presentasi = (TextView) view.findViewById(R.id.status_presentasi);
            status_koor = (TextView) view.findViewById(R.id.status_koor);
            status_ma = (TextView) view.findViewById(R.id.status_ma);
            tgl_catat = (TextView) view.findViewById(R.id.tgl_catat);
            pendata = (TextView) view.findViewById(R.id.pendata);
            konsultan = (TextView) view.findViewById(R.id.konsultan);
            deletedCheckBox = (CheckBox) view.findViewById(R.id.checkBox);

        }

        @Override
        public String toString() {
            return super.toString();
        }

    }

}
