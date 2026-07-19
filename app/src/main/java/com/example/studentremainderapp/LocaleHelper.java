package com.example.studentremainderapp;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Context context, String language) {
        if (language == null) language = "en";
        
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        
        // Also update application context to be safe
        context.getApplicationContext().getResources().updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
