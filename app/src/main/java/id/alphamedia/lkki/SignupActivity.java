package id.alphamedia.lkki;

/**
 * Created by abdulmuin on 12/07/17.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.alphamedia.lkki.models.UserDaftar;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    ProgressDialog dialog;

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _repasswordText;
    @BindView(R.id.input_mobile) EditText _mobile;
    @BindView(R.id.input_address) EditText _alamat;
    @BindView(R.id.pendidikan_terakhir) EditText _pendidikan;
    @BindView(R.id.nama_universitas) EditText _universitas;
    @BindView(R.id.tempat_lahir) EditText _tempat_lahir;
    @BindView(R.id.tgllahir) EditText _tgl_lahir;

    @BindView(R.id.input_desa) EditText _desa;
    @BindView(R.id.input_kec) EditText _kecamatan;
    @BindView(R.id.input_kota) EditText _kota;
    @BindView(R.id.input_prov) EditText _provinsi;
    @BindView(R.id.input_kodepos) EditText _kodepos;

    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        String nama = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String alamat = _alamat.getText().toString();
        String tempat_lahir = _tempat_lahir.getText().toString();
        String tgl_lahir = _tgl_lahir.getText().toString();
        String univ = _universitas.getText().toString();
        String pendidikan = _pendidikan.getText().toString();
        String no_hp = _mobile.getText().toString();

        String desa = _desa.getText().toString();
        String kecamatan = _kecamatan.getText().toString();
        String kota = _kota.getText().toString();
        String provinsi = _provinsi.getText().toString();
        String kodepos = _kodepos.getText().toString();
        String imei = getIMEI();

        List<UserDaftar> daftar = new ArrayList<UserDaftar>();
        UserDaftar mdldaftar = new UserDaftar();
        mdldaftar.setNama(nama);
        mdldaftar.setImei(imei);
        mdldaftar.setTempat_lahir(tempat_lahir);
        mdldaftar.setTgl_lahir(tgl_lahir);
        mdldaftar.setPasswd(password);
        mdldaftar.setEmail(email);
        mdldaftar.setNo_hp(no_hp);
        mdldaftar.setAlamat(alamat);
        mdldaftar.setDesa(desa);
        mdldaftar.setKecamatan(kecamatan);
        mdldaftar.setKota(kota);
        mdldaftar.setProvinsi(provinsi);
        mdldaftar.setPendidikan(pendidikan);
        mdldaftar.setPerguruan_tinggi(univ);
        mdldaftar.setKodepos(kodepos);
        daftar.add(mdldaftar);

        // TODO: Implement your own signup logic here.
        new AsynDaftar(SignupActivity.this, daftar, new AsynDaftar.AsyncDaftarResponse() {
            @Override
            public void processFinish(String output) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(output);
                    if(jsonObject.has("success") && jsonObject.getBoolean("success")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                        builder.setTitle("Pendaftaran Sukses");
                        builder.setMessage(jsonObject.getString("pesan"))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        //onSignupSuccess();
                                    }
                                });
                        builder.create();
                        builder.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute();

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Gagal membuat akun baru, isi seluruh isian yang ada!", Toast.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String repassword = _repasswordText.getText().toString();
        String alamat = _alamat.getText().toString();
        String tempat_lahir = _tempat_lahir.getText().toString();
        String tgl_lahir = _tgl_lahir.getText().toString();
        String univ = _universitas.getText().toString();
        String pendidikan = _pendidikan.getText().toString();
        String no_hp = _mobile.getText().toString();

        String desa = _desa.getText().toString();
        String kecamatan = _kecamatan.getText().toString();
        String kota = _kota.getText().toString();
        String provinsi = _provinsi.getText().toString();
        String kodepos = _kodepos.getText().toString();

        if (desa.isEmpty() || desa.length() < 3) {
            _desa.setError("Desa harus diisi");
            valid = false;
        } else {
            _desa.setError(null);
        }

        if (kecamatan.isEmpty() || kecamatan.length() < 3) {
            _kecamatan.setError("Kecamatan harus diisi");
            valid = false;
        } else {
            _kecamatan.setError(null);
        }

        if (kota.isEmpty() || kota.length() < 3) {
            _kota.setError("Kota / Kabupaten harus diisi");
            valid = false;
        } else {
            _kota.setError(null);
        }

        if (provinsi.isEmpty() || provinsi.length() < 3) {
            _provinsi.setError("Provinsi harus diisi");
            valid = false;
        } else {
            _provinsi.setError(null);
        }

        if (kodepos.isEmpty() || kodepos.length() < 5) {
            _kodepos.setError("Kodepos harus diisi");
            valid = false;
        } else {
            _kodepos.setError(null);
        }

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Isian nama harus diisi minimal 3 karakter");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (univ.isEmpty() || univ.length() < 3) {
            _universitas.setError("Isikan perguruan tinggi anda");
            valid = false;
        } else {
            _universitas.setError(null);
        }

        if (pendidikan.isEmpty() || pendidikan.length() < 3) {
            _pendidikan.setError("Pendidikan terakhir harus diisi");
            valid = false;
        } else {
            _pendidikan.setError(null);
        }

        if (tempat_lahir.isEmpty() || tempat_lahir.length() < 3) {
            _tempat_lahir.setError("Tempat lahir harus diisi");
            valid = false;
        } else {
            _tempat_lahir.setError(null);
        }

        if (tgl_lahir.isEmpty() || tgl_lahir.length() < 3) {
            _tgl_lahir.setError("Tanggal Lahir harus diisi");
            valid = false;
        } else {
            _tgl_lahir.setError(null);
        }

        if (alamat.isEmpty() || alamat.length() < 3) {
            _alamat.setError("Minimal 3 karakter");
            valid = false;
        } else {
            _alamat.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Masukan alamat email anda");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("karakter alfanumerik antara 4 sampai 20 karakter");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (repassword.isEmpty() || repassword.length() < 4 || repassword.length() > 20) {
            _repasswordText.setError("karakter alfanumerik antara 4 sampai 20 karakter");
            valid = false;
        } else {
            _repasswordText.setError(null);
        }

        if(!password.equals(repassword)) {
            _repasswordText.setError("Isian Password tidak sama, samakan isian password");
            valid = false;
        } else {
            _repasswordText.setError(null);
        }

        if (no_hp.isEmpty() || no_hp.length() < 6) {
            _mobile.setError("No HP harus diisi");
            valid = false;
        } else {
            _mobile.setError(null);
        }

        return valid;
    }

    private String getIMEI(){
        TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }

}