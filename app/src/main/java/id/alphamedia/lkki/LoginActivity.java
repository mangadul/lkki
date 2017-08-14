package id.alphamedia.lkki;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.alphamedia.lkki.models.Constants;
import id.alphamedia.lkki.models.UserLogin;

import static id.alphamedia.lkki.MainActivity.hasPermissions;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    String user, password;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    ProgressDialog dialog;
    SharedPreferences sharedpreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sharedpreferences = getSharedPreferences(Config.PREFSDATA,
                Context.MODE_PRIVATE);

        _loginButton.setEnabled(true);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // login();
                validate();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.VIBRATE,
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.CONTROL_LOCATION_UPDATES,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.GET_ACCOUNTS,
                android.Manifest.permission.WAKE_LOCK,
                android.Manifest.permission.WRITE_SETTINGS,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_CALENDAR,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.WRITE_SETTINGS};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        // cek jika user pernah login dan tersimpan
        sharedpreferences = getSharedPreferences(Config.PREFSDATA,
                Context.MODE_PRIVATE);
        Boolean islogin = sharedpreferences.getBoolean("islogin", false);
        if(islogin) {
            Intent mainactivity =  new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainactivity);
            finish();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();

        _loginButton.setEnabled(true);
    }

    public void validate() {

        boolean valid = true;

        // _loginButton.setEnabled(false);

        user = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        // !android.util.Patterns.EMAIL_ADDRESS.matcher(user).matches()
        if (user.isEmpty() || user.matches("") ) {
            _emailText.setError("masukan nama user anda");
            // Toast.makeText(getApplicationContext(), "masukan nama user anda", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("password antara 4-20 karakter alphanumerik");
            // Toast.makeText(getApplicationContext(), "password antara 4-20 karakter alphanumerik", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        String imei = getIMEI();

        if(isNetworkConnected())
        {
            new AsyncLogin(LoginActivity.this, user, password, imei, new AsyncLogin.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        Log.i(TAG, "Response server: " + output);
                        if(!output.matches("")) {
                            JSONObject jsonObject = new JSONObject(output);
                            ArrayList<UserLogin> usermodel = new ArrayList<UserLogin>();
                            if(jsonObject.has("success") && jsonObject.getBoolean("success")) {
                                JSONObject data = jsonObject.getJSONObject(Constants.TAG_DATA);
                                UserLogin mdlUser = new UserLogin();
                                mdlUser.setUid(data.getInt("uid"));
                                mdlUser.setUname(data.getString("uname"));
                                mdlUser.setNama(data.getString("nama"));
                                mdlUser.setNik(data.getString("nik"));
                                mdlUser.setTipe_user(data.getInt("tipe_user"));
                                mdlUser.setSession(data.getString("session"));
                                mdlUser.setKode_kab(data.getString("kode_kab"));
                                mdlUser.setKode_prov(data.getString("kode_prov"));
                                mdlUser.setFoto_profil(data.getString("foto_profil"));
                                mdlUser.setUtipe(data.getString("utipe"));
                                mdlUser.setUwilayah(data.getString("uwilayah"));
                                mdlUser.setIs_login(true);
                                usermodel.add(mdlUser);

                                Log.i(TAG, "Cek data model user: " + mdlUser.getNama());
                                Intent mainactivity =  new Intent(LoginActivity.this, MainActivity.class);
                                mainactivity.putExtra("uid", mdlUser.getUid());
                                mainactivity.putExtra("uname", mdlUser.getUname());
                                mainactivity.putExtra("nama", mdlUser.getNama());
                                mainactivity.putExtra("foto_profil", mdlUser.getFoto_profil());
                                mainactivity.putExtra("nik", mdlUser.getNik());
                                mainactivity.putExtra("tipe_user", mdlUser.getTipe_user());
                                mainactivity.putExtra("kode_prov", mdlUser.getKode_prov());
                                mainactivity.putExtra("kode_kab", mdlUser.getKode_kab());
                                mainactivity.putExtra("utipe", mdlUser.getUtipe());
                                mainactivity.putExtra("uwilayah", mdlUser.getUwilayah());
                                mainactivity.putExtra("is_login", mdlUser.isIs_login());

                                // simpan data di preference
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putInt("uid", mdlUser.getUid());
                                editor.putInt("tipe_user", mdlUser.getTipe_user());
                                editor.putBoolean("islogin", true);
                                editor.putBoolean("userlogged", true);
                                editor.putString("u_name", mdlUser.getUname());
                                editor.putString("u_nama", mdlUser.getNama());
                                editor.putString("foto_profil", mdlUser.getFoto_profil());
                                editor.putString("u_nik", mdlUser.getNik());
                                editor.putString("kode_prov", mdlUser.getKode_prov());
                                editor.putString("kode_kab", mdlUser.getKode_kab());
                                editor.putString("u_tipe", mdlUser.getUtipe());
                                editor.putString("u_wilayah", mdlUser.getUwilayah());
                                editor.commit();

                                startActivity(mainactivity);
                                finish();

                            } else if(jsonObject.has("success") && !jsonObject.getBoolean("success")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Error");
                                builder.setMessage("Nama user atau password anda salah!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.create();
                                builder.show();
                            }
                        } else Toast.makeText(getApplicationContext(), "Server response kosong, cek jaringan anda.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute();

        } else {
            new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("It looks like your internet connection is off. Please turn it " +
                        "on and try again")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }

    }

    private String getIMEI(){
        TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }

    private void dialogError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Nama user atau password anda salah!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

}