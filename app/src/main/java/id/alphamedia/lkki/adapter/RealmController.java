package id.alphamedia.lkki.adapter;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import id.alphamedia.lkki.models.DataProspek;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by abdulmuin on 20/07/17.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    //Refresh the realm istance
    public void refresh() {
        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {
        /*
        realm.beginTransaction();
        realm.delete(DataProspek.class);
        realm.commitTransaction();
        */
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<DataProspek> result = realm.where(DataProspek.class).findAll();
                result.deleteAllFromRealm();
            }
        });
    }

    //find all objects in the Book.class
    public RealmResults<DataProspek> getData() {

        return realm.where(DataProspek.class).findAll();
    }

    //query a single item with the given id
    public DataProspek getBook(String id) {

        return realm.where(DataProspek.class).equalTo("id", id).findFirst();
    }

    //check if Book.class is empty
    public boolean hasData() {
        // return !realm.allObjects(DataProspek.class).isEmpty();
        return !realm.where(DataProspek.class).findAll().isEmpty();
    }

    //query example
    public RealmResults<DataProspek> queryedData() {
        return realm.where(DataProspek.class)
                .contains("nama", "Author 0")
                .or()
                .contains("tempat", "Realm")
                .findAll();

    }
}