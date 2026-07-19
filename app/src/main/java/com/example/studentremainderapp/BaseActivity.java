package com.example.studentremainderapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SessionManager sessionManager = new SessionManager(this);
        LocaleHelper.setLocale(this, sessionManager.getLanguage());
        ThemeUtils.applyTheme(sessionManager);
        super.onCreate(savedInstanceState);
    }
}
