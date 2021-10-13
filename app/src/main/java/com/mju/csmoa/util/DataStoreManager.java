package com.mju.csmoa.util;

import android.os.Handler;
import android.os.Looper;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

// no use
public final class DataStoreManager {

    private static DataStoreManager INSTANCE = null;
    private final RxDataStore<Preferences> dataStore;
    private static final Handler dataStoreManagerHandler = new Handler(Looper.getMainLooper());

    private DataStoreManager() {
        // should call this before use
        dataStore = new RxPreferenceDataStoreBuilder(MyApplication.getInstance(), "recent_search.datastore").build();
    }

    public static DataStoreManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DataStoreManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new DataStoreManager();
            }
        }
        // Return the instance
        return INSTANCE;
    }


    // save String type
    public void saveValue(String keyName, String value) {
//        Preferences.Key<String> key = new Preferences.Key<>(keyName);
        Preferences.Key<String> key = PreferencesKeys.stringKey(keyName);

        dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            String currentKey = preferences.get(key);

            if (currentKey == null) {
                /*
                    Preferences.Key always return null when first time
                    so call saveValue again, anyone know how to fix it...?
                 */
                saveValue(keyName, value);
            }
            mutablePreferences.set(key, currentKey != null ? value : ""); // default value: ""
            return Single.just(mutablePreferences);
        }).subscribe();
    }

    public void getStringValue(String keyName, StringValueDelegate stringValueDelegate) {
        Preferences.Key<String> key = new Preferences.Key<>(keyName);

        dataStore.data().map(preferences -> preferences.get(key))
                .subscribeOn(Schedulers.newThread())
                .subscribe(new FlowableSubscriber<String>() {
                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        s.request(1);
                    }

                    @Override
                    public void onNext(String s) {
                        dataStoreManagerHandler.post(() -> stringValueDelegate.onGetValue(s));
                    }

                    @Override
                    public void onError(Throwable t) { }

                    @Override
                    public void onComplete() { }
                });
    }

    public interface StringValueDelegate {
        void onGetValue(String s);
    }

}
