package com.example.studentremainderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper dbHelper;
    private TextView tvSelectedDate, tvNoTasks;
    private SessionManager sessionManager;

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

        setContentView(R.layout.activity_calendar);

        dbHelper = new DatabaseHelper(this);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.rvCalendarTasks);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvNoTasks = findViewById(R.id.tvNoTasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(taskAdapter);

        // Set current date as default
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        loadTasksForDate(today);
        updateDateHeader(today);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            loadTasksForDate(selectedDate);
            updateDateHeader(selectedDate);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        loadSmallProfilePic();

        setupNavigation();
    }

    private void loadSmallProfilePic() {
        android.widget.ImageView btnProfile = findViewById(R.id.btnProfile);
        String uriStr = sessionManager.getProfilePic();
        if (uriStr != null) {
            try {
                btnProfile.setImageURI(android.net.Uri.parse(uriStr));
                btnProfile.setPadding(0, 0, 0, 0);
                btnProfile.setColorFilter(null);
            } catch (Exception e) {
                btnProfile.setImageResource(R.drawable.ic_account);
            }
        }
    }

    private void loadTasksForDate(String date) {
        List<Task> tasks = dbHelper.getTasksByDate(date);
        if (tasks.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoTasks.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoTasks.setVisibility(View.GONE);
            taskAdapter.updateTasks(tasks);
        }
    }

    private void updateDateHeader(String dateStr) {
        try {
            SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputSdf.parse(dateStr);
            SimpleDateFormat outputSdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            if (date != null) {
                tvSelectedDate.setText(outputSdf.format(date));
            }
        } catch (Exception e) {
            tvSelectedDate.setText(dateStr);
        }
    }

    private void setupNavigation() {
        findViewById(R.id.navTasks).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        // Current page is Calendar
        findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }
}