package id.alphamedia.lkki;

import id.alphamedia.lkki.models.DataProspek;
import id.alphamedia.lkki.models.Desa;
import id.alphamedia.lkki.models.FotoPresentasi;
import id.alphamedia.lkki.models.FotoProspek;
import id.alphamedia.lkki.models.JadwalPresentasi;
import id.alphamedia.lkki.models.Kabupaten;
import id.alphamedia.lkki.models.Kecamatan;
import id.alphamedia.lkki.models.Konsultan;
import id.alphamedia.lkki.models.Koordinator;
import id.alphamedia.lkki.models.Kurir;
import id.alphamedia.lkki.models.ManagerArea;
import id.alphamedia.lkki.models.PesertaPresentasi;
import id.alphamedia.lkki.models.Provinsi;
import id.alphamedia.lkki.models.User;
import io.realm.annotations.RealmModule;

@RealmModule(classes = {DataProspek.class, Provinsi.class,
        Kabupaten.class, Kecamatan.class, Desa.class, FotoPresentasi.class,
        FotoProspek.class, JadwalPresentasi.class, Konsultan.class, Koordinator.class,
        Kurir.class, ManagerArea.class, PesertaPresentasi.class, User.class
        })

public class LKKIModule {

}