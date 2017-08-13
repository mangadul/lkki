package id.alphamedia.lkki.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by abdulmuin on 28/07/17.
 */

public class DataProspekSerializer  implements JsonSerializer<DataProspek> {
    @Override
    public JsonElement serialize(DataProspek src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", src.getUid());
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("imei", src.getImei());
        jsonObject.addProperty("nama_tempat", src.getTempat());
        jsonObject.addProperty("alamat", src.getJalan());
        jsonObject.addProperty("rt", src.getRt());
        jsonObject.addProperty("rw", src.getRw());
        jsonObject.addProperty("kode_kel_desa", src.getDesa());
        jsonObject.addProperty("kode_kec", src.getKecamatan());
        jsonObject.addProperty("kode_kab", src.getKota());
        jsonObject.addProperty("kode_prov", src.getProvinsi());
        jsonObject.addProperty("kontak_nama", src.getNama());
        jsonObject.addProperty("kontak_no_hp", src.getNo_hp());
        jsonObject.addProperty("kontak_nokantor", src.getNo_kantor());
        jsonObject.addProperty("kontak_email", src.getEmail());
        jsonObject.addProperty("kontak_jabatan", src.getJabatan());
        jsonObject.addProperty("lokasi_lat", src.getLokasi_lat());
        jsonObject.addProperty("lokasi_long", src.getLokasi_long());

        jsonObject.addProperty("nama_pencatat", src.getNama_pencatat());
        jsonObject.addProperty("nik_pencatat", src.getNik_pencatat());

        jsonObject.addProperty("tgl_catat", String.valueOf(src.getTgl_catat()));

        jsonObject.addProperty("validasi_koor", src.isStatus_koor());
        jsonObject.addProperty("validasi_ma", src.isStatus_ma());

        jsonObject.addProperty("tgl_penyuluhan", String.valueOf(src.getTgl_penyuluhan()));

        jsonObject.addProperty("jam_penyuluhan", src.getWaktu_penyuluhan());
        jsonObject.addProperty("durasi", src.getDurasi());
        jsonObject.addProperty("realisasi_tgl_penyuluhan", String.valueOf(src.getTgl_penyuluhan())); //baru diserver
        jsonObject.addProperty("is_penyuluhan", src.isStatus_presentasi());
        jsonObject.addProperty("status_presentasi", src.getStatus_prospek());
        jsonObject.addProperty("status_catat", 1);
        jsonObject.addProperty("kata_kunci", src.getKata_kunci());
        jsonObject.addProperty("keterangan", src.getCatatan());
        jsonObject.addProperty("is_kirim_server", src.isIs_dikirim());
        jsonObject.addProperty("konsultan1", src.getKonsultan1()); //baru
        jsonObject.addProperty("konsultan2", src.getKonsultan2()); //baru
        jsonObject.addProperty("kurir", src.getKurir()); // baru
        jsonObject.addProperty("kurir_tgl_kirim", src.getKurir_tgl_kirim()); // baru
        jsonObject.addProperty("jml_order", src.getJmlorder()); // baru
        return jsonObject;
    }

}
