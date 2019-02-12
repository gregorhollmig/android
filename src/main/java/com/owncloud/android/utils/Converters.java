package com.owncloud.android.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.owncloud.android.lib.common.Quota;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();

        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();

        return gson.toJson(list);
    }

    @TypeConverter
    public static Quota quotaFromString(String value) {
        Type listType = new TypeToken<Quota>() {
        }.getType();

        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String quotaToString(Quota quota) {
        Gson gson = new Gson();

        return gson.toJson(quota);
    }
}
