package id.alphamedia.lkki;

/**
 * Created by abdulmuin on 13/08/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AmbilFotoProspek  extends AsyncTask<String, Void, String> {

    private static final String TAG = "AmbilFotoProspek";
    private OkHttpClient client = new OkHttpClient();
    private String respon;


    public interface AsyncPostResponse {
        void processFinish(String output);
    }

    public AsyncPostResponse delegate = null;

    final Request request;

    Context context;

    public AmbilFotoProspek(Context context, String uuid, String kode, String url, AsyncPostResponse delegate) {
        this.delegate = delegate;
        this.context = context;

        HashMap<String, String> param = new HashMap<>();
        param.put("uuid", uuid);
        param.put("kode", kode);
        param.put("hash", "5FCDA4A5922933C7D9422D04DA4E1BE2A00854F6EF187156D76C03BEA8AD365D");

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
        delegate.processFinish(result);
    }

}