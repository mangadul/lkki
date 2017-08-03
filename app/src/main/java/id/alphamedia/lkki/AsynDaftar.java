package id.alphamedia.lkki;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.alphamedia.lkki.models.UserDaftar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class AsynDaftar extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncDaftar";

    public interface AsyncDaftarResponse {
        void processFinish(String output);
    }

    public AsyncDaftarResponse delegate = null;
    public String uname, upass, imei;

    final Request request;
    final OkHttpClient client;

    Context context;

    ProgressDialog dialog;

    List<UserDaftar> reg;

    public AsynDaftar(Context context, List<UserDaftar> daftar, AsyncDaftarResponse delegate){
        this.delegate = delegate;
        this.context = context;

        this.reg = daftar;

        dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tgl_daftar = sdf.format(new Date());

        HashMap<String, String> param = new HashMap<>();
        param.put( "nama", reg.get(0).getNama());
        param.put( "no_hp", reg.get(0).getNo_hp());
        param.put( "email", reg.get(0).getEmail());
        param.put( "imei", reg.get(0).getImei());
        param.put( "passtxt", reg.get(0).getPasswd());
        param.put( "alamat", reg.get(0).getAlamat());
        param.put( "tempat_lahir", reg.get(0).getTempat_lahir());
        param.put( "tgl_lahir", reg.get(0).getTgl_lahir());
        param.put( "desa", reg.get(0).getDesa());
        param.put( "kecamatan", reg.get(0).getKecamatan());
        param.put( "kab_kota", reg.get(0).getKota());
        param.put( "provinsi", reg.get(0).getProvinsi());
        param.put( "pendidikan_terakhir", reg.get(0).getPendidikan());
        param.put( "perguruan_tinggi", reg.get(0).getPerguruan_tinggi());
        param.put( "kodepos", reg.get(0).getKodepos());
        param.put( "tgl_daftar", tgl_daftar);

        FormBody.Builder builder = new FormBody.Builder();

        for(Map.Entry<String, String> entry : param.entrySet() ) {
            builder.add( entry.getKey(), entry.getValue() );
        }

        client = new OkHttpClient();

        RequestBody formBody = builder.build();
        request = new Request.Builder()
                .url(Config.URL_DAFTAR)
                .post(formBody)
                .build();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Menyimpan Data...");
        this.dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Toast.makeText(context, "Periksa jaringan internet anda.", Toast.LENGTH_SHORT).show();
            }
            String ret = response.body().string();
            Log.i(TAG, "Daftar response Server: " + ret);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Log.i(TAG, "Response Server Login: " + result);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        delegate.processFinish(result);
    }

}