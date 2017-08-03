package id.alphamedia.lkki;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class AsyncLogin extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncLogn";

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    public String uname, upass, imei;

    final Request request;
    final OkHttpClient client;

    Context context;

    ProgressDialog dialog;
    Handler handler;

    public AsyncLogin(Context context, String user, String pass, String imei, AsyncResponse delegate){
        this.delegate = delegate;
        this.uname = user;
        this.upass = pass;
        this.imei = imei;
        this.context = context;


        dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);


        HashMap<String, String> param = new HashMap<>();
        param.put( "user", uname );
        param.put( "password", upass);
        param.put( "imei", imei);

        FormBody.Builder builder = new FormBody.Builder();

        for(Map.Entry<String, String> entry : param.entrySet() ) {
            builder.add( entry.getKey(), entry.getValue() );
        }

        client = new OkHttpClient();

        RequestBody formBody = builder.build();
        request = new Request.Builder()
                .url(Config.URL_LOGIN)
                .post(formBody)
                .build();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Login ke server...");
        this.dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                dialogError();
            }
            String ret = response.body().string();
            Log.i(TAG, "Server: " + ret);
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

    private String dialogError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage("GAGAL menyambung ke server, Silahkan cek koneksi jaringan internet anda.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();

        return null;
    }

}