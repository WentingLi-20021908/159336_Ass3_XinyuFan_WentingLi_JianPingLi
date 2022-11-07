package com.example.myapplication.dao;

import android.net.Uri;

import androidx.room.TypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class ImageMapTypeConverter {
    @TypeConverter
    public HashMap<Integer, Uri> stringToMap(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            HashMap<Integer, Uri> map = new HashMap<>();
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                map.put(Integer.parseInt(iterator.next()), (Uri) jsonObject.get(iterator.next()));
            }
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @TypeConverter
    public String mapToString(HashMap<Integer, Uri> map) {
        return new JSONObject(map).toString();
    }
}
