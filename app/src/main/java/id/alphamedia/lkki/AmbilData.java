package id.alphamedia.lkki;

/**
 * Created by abdulmuin on 05/08/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AmbilData  extends AsyncTask<String, Void, String> {

    private static final String TAG = "AmbilData";
    private OkHttpClient client = new OkHttpClient();
    private String respon;


    public interface AsyncPostResponse {
        void processFinish(String output);
    }

    public AsyncPostResponse delegate = null;

    final Request request;

    Context context;

    ProgressDialog dialog;
    int uid;

    public AmbilData(Context context, int uid, int utipe, String imei, String url, AsyncPostResponse delegate) {
        this.uid = uid;
        this.delegate = delegate;
        this.context = context;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tgl_kirim = sdf.format(new Date());

        dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        HashMap<String, String> param = new HashMap<>();
        param.put("tgl_kirim", tgl_kirim);
        param.put("imei", imei);
        param.put("uid", String.valueOf(uid));
        param.put("param", "60402199bfc8dac3b15304b47dcdf99d1f65547b662bdf4b7f5d85944e36ac79");

        FormBody.Builder builder = new FormBody.Builder();
        for(Map.Entry<String, String> entry : param.entrySet() ) {
            builder.add( entry.getKey(), entry.getValue() );
        }

        client = new OkHttpClient();

        RequestBody formBody = builder.build();
        request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

    }

    public String getData(){
        return respon;
    }

    public void setData(String data) {
        this.respon = data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Memuat data dari server...");
        this.dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return null;
            }
            String ret = response.body().string();
            Log.i(TAG, "Response Server: " + ret);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        delegate.processFinish(result);
    }

}