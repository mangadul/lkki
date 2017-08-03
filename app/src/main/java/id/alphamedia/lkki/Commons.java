package id.alphamedia.lkki;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;

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


}
