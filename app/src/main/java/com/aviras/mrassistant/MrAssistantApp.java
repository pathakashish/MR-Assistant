package com.aviras.mrassistant;

import android.app.Application;

import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.doctors.DoctorEditor;
import com.aviras.mrassistant.ui.doctors.DoctorsList;
import com.aviras.mrassistant.ui.medicines.MedicineEditor;
import com.aviras.mrassistant.ui.medicines.MedicinesList;
import com.aviras.mrassistant.ui.orders.OrderEditor;
import com.aviras.mrassistant.ui.orders.OrdersList;
import com.aviras.mrassistant.ui.units.UnitEditor;
import com.aviras.mrassistant.ui.units.UnitsList;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Application level state
 * <p/>
 * Created by ashish on 16/6/16.
 */
public class MrAssistantApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLogging();
        initRealm();
    }

    private void initRealm() {
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).build());
    }

    public void initPresenters() {
        Realm realm = Realm.getDefaultInstance();

        DoctorsList.sharedInstance().init(realm);
        DoctorEditor.sharedInstance().init(realm);

        MedicinesList.sharedInstance().init(realm);
        MedicineEditor.sharedInstance().init(realm);

        UnitsList.sharedInstance().init(realm);
        UnitEditor.sharedInstance().init(realm);

        OrdersList.sharedInstance().init(realm);
        OrderEditor.sharedInstance().init(realm);
    }

    private void initLogging() {
        Log.setLogFilePath(new File(getExternalCacheDir(), "logs.txt").getAbsolutePath());
    }
}
