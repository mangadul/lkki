package id.alphamedia.lkki;

/**
 * Created by abdulmuin on 16/07/17.
 * https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/AsynchronousGet.java
 */

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsynchronousGet {

    private final OkHttpClient client = new OkHttpClient();
    private final static String TAG = "AsynchronousGet";
    private String respon;

    public AsynchronousGet(String url, String filename) {
        try {
            run(url, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String url, final String fn) throws Exception {

        final Request request = new Request.Builder()
                .url(url)
                .build();

        // client.newCall(request).enqueue(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String resp = response.body().string();
                    writeToFile(resp, fn);
                    // Log.d(TAG, resp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void writeToFile(String data, String fn) {
        try {
            String filename = Config.FILE_DIR + fn;
            FileOutputStream outputStreamWriter = new FileOutputStream(new File(filename).getAbsolutePath(), true);
            outputStreamWriter.write(data.getBytes(StandardCharsets.UTF_8));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String getData(){
        return respon;
    }

    public void setData(String data) {
        this.respon = data;
    }

}