package id.alphamedia.lkki;

/**
 * Created by abdulmuin on 15/07/17.
 */

import android.os.Environment;

public class Config {
    public static final String BASE_URL = "http://devs.alphamedia.id/lkki/";
    public static final String FILE_UPLOAD_URL = "http://devs.alphamedia.id/lkki/lkki_upload.php";
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    public static final String APP_DIR = Environment.getExternalStorageDirectory().getPath() + "/LKKI";
    public static final String FILE_DIR = APP_DIR + "/file/";
    public static final String FOTO_DIR = APP_DIR + "/foto/";
    public static final String DATA_DIR = APP_DIR + "/file/data.json";
    public static final String CEK_KONTAK = "http://devs.alphamedia.id/lkki/cekkontak.php";
    public static final String URL_UPLOAD = "http://devs.alphamedia.id/lkki/uploads123.php";
    public static final String URL_KONTAK = "http://devs.alphamedia.id/lkki/kontak.php";
    public static final String JSON_PROVINSI = "http://devs.alphamedia.id/lkki/data.php?tipe=provinsi";
    public static final String JSON_KOTA = "http://devs.alphamedia.id/lkki/data.php?tipe=kabupaten";
    public static final String JSON_KECAMATAN = "http://devs.alphamedia.id/lkki/data.php?tipe=kecamatan";
    public static final String JSON_DESA = "http://devs.alphamedia.id/lkki/data.php?tipe=desa";
    public static final String JSON_KONSULTAN = "http://devs.alphamedia.id/lkki/data.php?tipe=konsultan";
    public static final String JSON_KURIR = "http://devs.alphamedia.id/lkki/data.php?tipe=kurir";
    public static final String URL_DOWNLOAD = "http://devs.alphamedia.id/lkki/download/";
    public static final String FILE_DOWNLOAD = "data.zip";
    public static final String FILE_PROVINSI = FILE_DIR + "provinsi.json";
    public static final String FILE_KABUPATEN = FILE_DIR + "kabupaten.json";
    public static final String FILE_KECAMATAN = FILE_DIR + "kecamatan.json";
    public static final String FILE_KONSULTAN = FILE_DIR + "konsultan.json";
    public static final String FILE_KURIR = FILE_DIR + "kurir.json";
    public static final String FILE_DESA = FILE_DIR + "desa.json";
    public static final String URL_LOGIN = "http://devs.alphamedia.id/lkki/login.php";
    public static final String URL_DAFTAR = "http://devs.alphamedia.id/lkki/reguser.php";
    public static final String URL_POST_DATA_CATAT = "http://devs.alphamedia.id/lkki/postdata.php";
    public static final String PREFSDATA = "prefslkki";
    public static final String URL_IMAGES = "http://devs.alphamedia.id/lkki/uploads/";

    public static final String GET_FOTO = "http://devs.alphamedia.id/lkki/getfoto.php";

    public static final String URL_GETDATA_CATAT = "http://devs.alphamedia.id/lkki/getdata.php";

    public static final String UID = "uid";
    public static final String TIPE_USER = "tipe_user";
    public static final String NAMA_PENCATAT = "nama_pencatat";
    public static final String NIK_PENCATAT = "nik_pencatat";
    public static final String KODE_KABUPATEN = "kode_kabupaten";
    public static final String KODE_ROVINSI = "kode_provinsi";

}