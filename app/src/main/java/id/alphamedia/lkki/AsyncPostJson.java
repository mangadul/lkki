package id.alphamedia.lkki;

/**
 * Created by abdulmuin on 29/07/17.
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

public class AsyncPostJson extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncPostJson";

    public interface AsyncPostResponse {
        void processFinish(String output);
    }

    public AsyncPostResponse delegate = null;

    final Request request;
    final OkHttpClient client;

    Context context;

    ProgressDialog dialog;
    String url;

    public AsyncPostJson(Context context, String url, String paramurl, String json, AsyncPostResponse delegate){
        this.delegate = delegate;
        this.context = context;
        this.url = url;

        dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tgl_kirim = sdf.format(new Date());

        final Commons cmn = new Commons(context);

        HashMap<String, String> param = new HashMap<>();
        param.put("data", json);
        param.put("tgl_kirim", tgl_kirim);
        param.put("imei", cmn.getIMEI());
        param.put("param", paramurl);

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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Memuat data...");
        this.dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                // Toast.makeText(context, "Periksa jaringan internet anda.", Toast.LENGTH_SHORT).show();
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