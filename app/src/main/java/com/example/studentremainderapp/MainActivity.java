package com.example.studentremainderapp;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private DatabaseHelper dbHelper;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private TextView emptyStateText, tvTaskSummary, tvProgressMsg;
    private ProgressBar weeklyProgressBar;
    private TabLayout filterTabs;
    private SessionManager sessionManager;
    private TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        startService(new Intent(this, AppTerminatorService.class));

        dbHelper = new DatabaseHelper(this);
        
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        tvProgressMsg = findViewById(R.id.tvProgressMsg);
        tvGreeting = findViewById(R.id.tvGreeting);
        weeklyProgressBar = findViewById(R.id.weeklyProgressBar);
        filterTabs = findViewById(R.id.filterTabs);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        tvGreeting.setText(getString(R.string.hello, sessionManager.getName()));

        loadSmallProfilePic();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(taskAdapter);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        setupTabs();
        setupNavigation();
        checkPermissions();
    }

    private void loadSmallProfilePic() {
        ImageView btnProfile = findViewById(R.id.btnProfile);
        String uriStr = sessionManager.getProfilePic();
        if (uriStr != null) {
            try {
                btnProfile.setImageURI(Uri.parse(uriStr));
                btnProfile.setPadding(0, 0, 0, 0);
                btnProfile.setColorFilter(null);
            } catch (Exception e) {
                btnProfile.setImageResource(R.drawable.ic_account);
            }
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications won't be shown without permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupNavigation() {
        findViewById(R.id.navCalendar).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }

    private void setupTabs() {
        filterTabs.addTab(filterTabs.newTab().setText(R.string.all));
        filterTabs.addTab(filterTabs.newTab().setText(R.string.pending));
        filterTabs.addTab(filterTabs.newTab().setText(R.string.completed));

        filterTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filter;
                if (tab.getPosition() == 1) filter = "Pending";
                else if (tab.getPosition() == 2) filter = "Completed";
                else filter = "All";
                loadTasks(filter);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int selectedTab = filterTabs.getSelectedTabPosition();
        String filter = "All";
        if (selectedTab == 1) filter = "Pending";
        else if (selectedTab == 2) filter = "Completed";
        
        loadTasks(filter);
        updateProgress();
    }

    private void loadTasks(String filter) {
        int userId = sessionManager.getUserId();
        List<Task> tasks;
        if (filter.equals("Pending")) {
            tasks = dbHelper.getTasksByStatus(userId, false);
        } else if (filter.equals("Completed")) {
            tasks = dbHelper.getTasksByStatus(userId, true);
        } else {
            tasks = dbHelper.getAllTasks(userId);
        }

        if (tasks.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            taskAdapter.updateTasks(tasks);
        }
    }

    private void updateProgress() {
        int userId = sessionManager.getUserId();
        List<Task> allTasks = dbHelper.getAllTasks(userId);
        if (allTasks.isEmpty()) {
            tvProgressMsg.setText(R.string.start_adding_tasks);
            weeklyProgressBar.setProgress(0);
            return;
        }

        int completedCount = 0;
        for (Task t : allTasks) {
            if (t.isCompleted()) completedCount++;
        }

        int percent = (completedCount * 100) / allTasks.size();
        weeklyProgressBar.setProgress(percent);

        if (percent == 100) {
            tvProgressMsg.setText(R.string.all_completed);
        } else {
            tvProgressMsg.setText(getString(R.string.progress_status, percent));
        }
    }

}