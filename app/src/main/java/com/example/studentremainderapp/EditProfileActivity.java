package com.example.studentremainderapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail;
    private ImageView ivProfilePic;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private String currentEmail;
    private String selectedImageUri = null;
    private static final int PICK_IMAGE_REQUEST = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        LocaleHelper.setLocale(this, sessionManager.getLanguage());
        ThemeUtils.applyTheme(sessionManager);
        
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        
        currentEmail = sessionManager.getEmail();
        etName.setText(sessionManager.getName());
        etEmail.setText(currentEmail);
        
        loadProfilePic();

        findViewById(R.id.cardProfilePic).setOnClickListener(v -> openImagePicker());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnSave).setOnClickListener(v -> saveProfile());
    }

    private void loadProfilePic() {
        String uriStr = sessionManager.getProfilePic();
        if (uriStr != null) {
            try {
                ivProfilePic.setImageURI(Uri.parse(uriStr));
                ivProfilePic.setPadding(0, 0, 0, 0);
                ivProfilePic.setColorFilter(null);
            } catch (Exception e) {
                ivProfilePic.setImageResource(R.drawable.ic_account);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    selectedImageUri = uri.toString();
                    ivProfilePic.setImageURI(uri);
                    ivProfilePic.setPadding(0, 0, 0, 0);
                    ivProfilePic.setColorFilter(null);
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.valid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isEmailExists(email, currentEmail)) {
            Toast.makeText(this, R.string.email_in_use, Toast.LENGTH_SHORT).show();
            return;
        }

        int result = dbHelper.updateUser(currentEmail, name, email);
        if (result > 0) {
            sessionManager.updateSession(name, email);
            if (selectedImageUri != null) {
                sessionManager.setProfilePic(selectedImageUri);
            }
            Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.failed_update, Toast.LENGTH_SHORT).show();
        }
    }
}
