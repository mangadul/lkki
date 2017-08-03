package id.alphamedia.lkki;

/**
 * Created by abdulmuin on 16/07/17.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Download extends Activity {

    ProgressDialog mProgressDialog;
    String outFile, outPath;
    String url = Config.URL_DOWNLOAD;
    String filename = Config.FILE_DOWNLOAD;
    ConnectivityManager connManager;
    NetworkInfo dataConnectionStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog = new ProgressDialog(Download.this);
        mProgressDialog.setMessage("Download Data");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        DownloadFile downloadFile = new DownloadFile();
        outPath = Config.FILE_DIR;
        outFile =  outPath + filename;

        if (isConnected(this))
        {
            downloadFile.execute(url + filename);
        } else
        {
            Toast.makeText(getApplicationContext(), "Kesalahan: Tidak ada jaringan Internet", Toast.LENGTH_LONG).show();
            Log.e("Error","Tidak ada jaringan internet");
        }
    }

    private class DownloadFile extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... url) {
            int count;
            try {
                URL url2 = new URL(url[0]);
                URLConnection conexion = url2.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                InputStream input = new BufferedInputStream(url2.openStream());
                OutputStream output = new FileOutputStream(outFile, true);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int)(total*100/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error download : ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... args){
            mProgressDialog.setProgress(args[0]);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.setMessage("Uncompress file...");
            try {
                unzip(new File(outFile), new File(outPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mProgressDialog.dismiss();
            SystemClock.sleep(5000);
            mProgressDialog.setMessage("Uncompress finish...");
            finish();
        }
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file, true);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
                long time = ze.getTime();
                if (time > 0)
                    file.setLastModified(time);
            }
        } finally {
            zis.close();
        }
    }

    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=null;
        if (connectivityManager != null) {
            networkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!networkInfo.isAvailable()) {
                networkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }

}