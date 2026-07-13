package com.example.studentremainderapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HelpSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SessionManager sessionManager = new SessionManager(this);
        LocaleHelper.setLocale(this, sessionManager.getLanguage());
        ThemeUtils.applyTheme(sessionManager);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_help_support);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupContactOptions();
    }

    private void setupContactOptions() {
        // Email Option
        View emailOption = findViewById(R.id.optionEmail);
        ((TextView) emailOption.findViewById(R.id.tvSettingTitle)).setText(R.string.send_email);
        ((ImageView) emailOption.findViewById(R.id.ivSettingIcon)).setImageResource(android.R.drawable.ic_dialog_email);
        emailOption.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@suhaybcoder.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Student Remainder App");
            startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
        });

        // Report Bug Option
        View bugOption = findViewById(R.id.optionBug);
        ((TextView) bugOption.findViewById(R.id.tvSettingTitle)).setText(R.string.report_bug);
        ((ImageView) bugOption.findViewById(R.id.ivSettingIcon)).setImageResource(android.R.drawable.ic_delete); // Using alert icon if available, or just standard one
        bugOption.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@suhaybcoder.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report - Student Remainder App");
            startActivity(Intent.createChooser(intent, getString(R.string.report_bug)));
        });
    }
}
