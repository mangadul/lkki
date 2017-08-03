package id.alphamedia.lkki;

import android.content.Context;

import id.alphamedia.lkki.models.DataProspek;
import io.realm.RealmResults;

/**
 * Created by abdulmuin on 20/07/17.
 */

public class RealmDataAdaper extends RealmModelAdapter<DataProspek> {

    public RealmDataAdaper(Context context, RealmResults<DataProspek> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
