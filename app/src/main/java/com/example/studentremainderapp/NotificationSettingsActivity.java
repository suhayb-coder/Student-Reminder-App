package com.example.studentremainderapp;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationSettingsActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvCurrentSound;
    private static final int REQUEST_CODE_SOUND_PICKER = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        LocaleHelper.setLocale(this, sessionManager.getLanguage());
        ThemeUtils.applyTheme(sessionManager);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_notification_settings);

        tvCurrentSound = findViewById(R.id.tvCurrentSound);
        SwitchMaterial switchNotifications = findViewById(R.id.switchNotifications);

        switchNotifications.setChecked(sessionManager.isNotificationsEnabled());
        updateSoundText();

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setNotificationsEnabled(isChecked);
        });

        findViewById(R.id.layoutSound).setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_sound));
            
            String currentUri = sessionManager.getNotificationSound();
            if (currentUri != null) {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(currentUri));
            } else {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            }
            
            startActivityForResult(intent, REQUEST_CODE_SOUND_PICKER);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void updateSoundText() {
        String uriString = sessionManager.getNotificationSound();
        if (uriString == null) {
            tvCurrentSound.setText(R.string.default_sound);
        } else {
            Uri uri = Uri.parse(uriString);
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            if (ringtone != null) {
                tvCurrentSound.setText(ringtone.getTitle(this));
            } else {
                tvCurrentSound.setText(R.string.default_sound);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SOUND_PICKER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                sessionManager.setNotificationSound(uri.toString());
            } else {
                sessionManager.setNotificationSound(null);
            }
            updateSoundText();
        }
    }
}
