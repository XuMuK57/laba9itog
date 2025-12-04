package com.example.taskplanner;

import org.json.JSONObject;

public class Json {

    public static int getInt(JSONObject o, String key) {
        try { return o.getInt(key); } catch (Exception e) { return 0; }
    }

    public static String getString(JSONObject o, String key) {
        try { return o.getString(key); } catch (Exception e) { return ""; }
    }

    public static boolean getBool(JSONObject o, String key) {
        try { return o.getBoolean(key); } catch (Exception e) { return false; }
    }
}
