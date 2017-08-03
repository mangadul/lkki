package id.alphamedia.lkki.models;

import java.util.Collection;

import io.realm.Realm;

public class DataHelper {

    public static void deleteItemAsync(Realm realm, final long id) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DataProspek dp = realm.where(DataProspek.class).equalTo("id_prospek", id).findFirst();
                if (dp != null) {
                    dp.deleteFromRealm();
                }
            }
        });
    }

    public static void deleteItemsAsync(Realm realm, Collection<Long> ids) {
        // Create an new array to avoid concurrency problem.
        final Integer[] idsToDelete = new Integer[ids.size()];
        ids.toArray(idsToDelete);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (long id : idsToDelete) {
                    DataProspek dp = realm.where(DataProspek.class).equalTo("id_prospek", id).findFirst();
                    if (dp != null) {
                        dp.deleteFromRealm();
                    }
                }
            }
        });
    }

}