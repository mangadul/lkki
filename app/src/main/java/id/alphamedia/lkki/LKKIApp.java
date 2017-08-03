package id.alphamedia.lkki;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.mapbox.mapboxsdk.Mapbox;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LKKIApp extends Application {

    private static LKKIApp instance;

    @Override
    public void onCreate() {
        super.onCreate();

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        Mapbox.getInstance(this, getString(R.string.com_mapbox_mapboxsdk_accessToken));

        Realm.init(this);

        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        /*
        Realm realm = Realm.getDefaultInstance();
        PrimaryKeyFactory.init(realm);
        */

        /*
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        */

        instance = this;

    }

    public static LKKIApp getInstance(){
        return instance;
    }

}