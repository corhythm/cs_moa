package com.mju.csmoa.util;

import static android.content.SharedPreferences.Editor;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

// 곧 deprecated 될 예정
public final class SharedPreferencesManager {

    private static SharedPreferencesManager INSTANCE;

    private SharedPreferencesManager() { }

    public static SharedPreferencesManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SharedPreferencesManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new SharedPreferencesManager();
            }
        }
        // Return the instance
        return INSTANCE;
    }

    private final String SHARED_RECENT_SEARCH_HISTORY = "SHARED_RECENT_SEARCH_HISTORY";
    private final String KEY_RECENT_SEARCH_HISTORY = "KEY_RECENT_SEARCH_HISTORY";

    public void addRecentSearchHistory(String search, String date) {

        // get SharedPreferences
        SharedPreferences sharedPreferences = MyApplication
                .getInstance().getSharedPreferences(SHARED_RECENT_SEARCH_HISTORY, Context.MODE_PRIVATE);

        // shared에서 가져온 JSON
        String historyJson = sharedPreferences.getString(KEY_RECENT_SEARCH_HISTORY, null);

        LinkedHashMap<String, String> recentHistorySet = null;

        try {
            if (historyJson == null) { // shared에 아무것도 저장되어 있지 않으면
                recentHistorySet = new LinkedHashMap<>();
            } else {
                recentHistorySet = new ObjectMapper()
                        .readValue(historyJson, new TypeReference<LinkedHashMap<String, String>>() {});
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        recentHistorySet.put(search, date);
        String updatedHistoryJson = new Gson().toJson(recentHistorySet);

        // get SharedPreference editor
        Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RECENT_SEARCH_HISTORY, updatedHistoryJson);
        editor.apply(); // save
    }

    public LinkedHashMap<String, String> getRecentSearchHistory() {
        // get SharedPreferences
        SharedPreferences sharedPreferences = MyApplication
                .getInstance().getSharedPreferences(SHARED_RECENT_SEARCH_HISTORY, Context.MODE_PRIVATE);

        // shared에서 가져온 JSON
        String historyJson = sharedPreferences.getString(KEY_RECENT_SEARCH_HISTORY, null);

        if (historyJson != null) {
            try {
                return new ObjectMapper()
                        .readValue(historyJson, new TypeReference<LinkedHashMap<String, String>>() {});
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public String removeRecentSearchHistory(String search) {
        SharedPreferences sharedPreferences = MyApplication
                .getInstance().getSharedPreferences(SHARED_RECENT_SEARCH_HISTORY, Context.MODE_PRIVATE);

        // shared에서 가져온 JSON
        String historyJson = sharedPreferences.getString(KEY_RECENT_SEARCH_HISTORY, null);
        Map<String, String> recentHistorySet = null;

        if (historyJson != null) {
            try {
                recentHistorySet =  new ObjectMapper()
                        .readValue(historyJson, new TypeReference<LinkedHashMap<String, String>>() {});

                // remove specific search history
                return recentHistorySet.remove(search);

            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public void clearAllRecentSearchHistory() {
        MyApplication.getInstance()
                .getSharedPreferences(SHARED_RECENT_SEARCH_HISTORY, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
