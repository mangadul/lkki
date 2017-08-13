package id.alphamedia.lkki.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;

import id.alphamedia.lkki.Config;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by abdulmuin on 04/08/17.
 */

public class UploadFile extends AsyncTask<String, Void, String>  {

    private static final String TAG = "UploadFile";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    final String filename;
    final String fileabs;
    Context context;
    ProgressDialog dialog;

    final Request request;
    final OkHttpClient client;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public UploadFile(Context context, String filename, String fileabs, AsyncResponse delegate){
        this.context = context;
        this.filename = filename;
        this.fileabs = fileabs;
        this.delegate = delegate;

        dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", filename,
                        RequestBody.create(MEDIA_TYPE_JPG, new File(fileabs)))
                .build();

        request = new Request.Builder()
                .url(Config.URL_UPLOAD)
                .post(requestBody)
                .build();

        /*
        RequestBody formBody = builder.build();
        request = new Request.Builder()
                .url(Config.URL_LOGIN)
                .post(formBody)
                .build();
        */

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // this.dialog.setMessage("Upload file...");
        // this.dialog.show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getApplicationContext());
        builder.setTitle("Error");
        builder.setMessage("GAGAL menyambung ke server, Silahkan cek koneksi jaringan internet anda.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        return null;
    }

}
