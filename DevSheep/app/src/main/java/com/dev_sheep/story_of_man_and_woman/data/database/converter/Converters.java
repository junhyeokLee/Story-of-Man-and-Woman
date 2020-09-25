package com.dev_sheep.story_of_man_and_woman.data.database.converter;

import androidx.room.TypeConverter;

import com.dev_sheep.story_of_man_and_woman.data.database.entity.ItemImage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<ItemImage> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<ItemImage> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}