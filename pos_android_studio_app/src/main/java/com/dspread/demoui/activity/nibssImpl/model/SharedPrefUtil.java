package com.dspread.demoui.activity.nibssImpl.model;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;

import com.dspread.demoui.BaseApplication;
import com.google.gson.reflect.TypeToken;
//import com.dspread.demoui.activity.nibssImpl.BaseApplication;
//import com.dspread.demoui.activity.nibssImpl.TermParamInfo;
import com.dspread.demoui.activity.nibssImpl.utils.Globals;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.Map;


public class SharedPrefUtil {
    private static SharedPrefUtil instance;

    public SharedPrefUtil() {
    }

    public static SharedPrefUtil init() {
        if (instance == null){
            instance = new SharedPrefUtil();
        }
        return instance;
    }

    public static SharedPrefUtil getSharedPrefInstance() {
        return instance;
    }

    public void removeValueInPref(String key){
        SharedPreferences.Editor editor = BaseApplication.getSharedPref().edit().remove(key);
        editor.apply();
    }

    public void putStringInPref(String key, String value) {

        SharedPreferences.Editor editor = BaseApplication.getSharedPref().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putIntInPref(String key, int value) {
        SharedPreferences.Editor editor = BaseApplication.getSharedPref().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putBooleanInPref(String key, boolean value) {
        SharedPreferences.Editor editor = BaseApplication.getSharedPref().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putLongInPref(String key, long value) {
        SharedPreferences.Editor editor = BaseApplication.getSharedPref().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putFloatInPref(String key, float value) {
        SharedPreferences.Editor editor = BaseApplication.getSharedPref().edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public String getStringFromPref(String key) {
        return BaseApplication.getSharedPref().getString(key, Globals.NON);
    }

    public String getStringWithDefaultFromPref(String key, String key2) {
        return BaseApplication.getSharedPref().getString(key, key2);
    }



//(TranNetInfo tInfoc,


    public void putTranNetInfoToPref_(Map<String, TranNetInfo> value) {
        Type type = new TypeToken<Map<String, TranNetInfo>>() {}.getType();
       // String json = BaseApplication.getInstance().gson.toJson(value, type);
     //   putStringInPref(Globals.TranNetInfo, json);
    }

//    public Map<String, TranNetInfo> getTranNetInfoFromPref() {
//        String registerRequest = BaseApplication.getSharedPref().getString(Globals.TranNetInfo, "");
//        Type type = new TypeToken<Map<String, TranNetInfo>>() {}.getType();
//        System.out.println("**********registerRequest=>" + registerRequest);
//        return BaseApplication.getInstance().gson.fromJson(registerRequest, type);
//    }


//TermParamInfo

//    public void putTermParamInfoToPref_(Map<String, TermParamInfo> value) {
//        Type type = new TypeToken<Map<String, TermParamInfo>>() {}.getType();
//        String json = BaseApplication.getInstance().gson.toJson(value, type);
//        putStringInPref(Globals.TermParamInfo, json);
//    }
//
//    public Map<String, TermParamInfo> getTermParamInfoFromPref() {
//        String registerRequest = BaseApplication.getSharedPref().getString(Globals.TermParamInfo, "");
//        Type type = new TypeToken<Map<String, TermParamInfo>>() {}.getType();
//        System.out.println("**********registerRequest=>" + registerRequest);
//        return BaseApplication.getInstance().gson.fromJson(registerRequest, type);
//    }






    public static String GetRefNumber( int len) {

        String finalString = "";
        int x = 0;
        char[] stringChars = new char[len];
        for (int i = 0; i < len; i++) //4
        {
            SecureRandom random = new SecureRandom();
            x = random.nextInt(9);

            stringChars[i] = Integer.toString(x).toCharArray()[0];
        }


        finalString = new String(stringChars);
        finalString = finalString;
        return finalString.trim();
    }



    public static void showMessageBox(String msg, Activity actx, String tt) {
        if (!actx.isFinishing() && !actx.isDestroyed()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(actx);
            alertDialogBuilder.setTitle(tt);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

}
