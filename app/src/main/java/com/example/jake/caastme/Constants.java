package com.example.jake.caastme;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jake on 2016/11/26.
 */
public class Constants {

    public static String getProperty(String key,Context context) {
        Properties properties = new Properties();;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("constants.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }


    public static final String CAAST_RESULT = "caast_result";

}
