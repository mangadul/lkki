package id.alphamedia.lkki;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by abdulmuin on 29/07/17.
 */

public class Commons {

    private Context context;

    public Commons(Context context){
        this.context = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    public String getIMEI(){
        TelephonyManager mngr = (TelephonyManager) context.getApplicationContext().getSystemService(context.getApplicationContext().TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }

    public void alertMessage(String judul, String isi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(judul);
        builder.setMessage(isi)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }


    public boolean isEmpty(String text){
        return (text == null || text.matches("") || text.length() <= 0);
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

    public static Date toDate(String dateString) {
        Date date = null;
        String formatted = "";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("Print result: ", String.valueOf(date));
        return date;
    }


    public static String toMySQLDate(String date){
        /*
        String formatted = "";
        SimpleDateFormat format_tgl = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        */

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = formatter.format(date);

        /*
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        try {
            Date tanggal = (Date) formatter.parse(date);
            formatted = format_tgl.format(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        */
        return String.valueOf(format);
    }

}
