package com.example.studentremainderapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.util.Calendar;
import java.util.Locale;

public class AddEditTaskActivity extends BaseActivity {

    private EditText etTitle, etDescription, etDate, etTime;
    private MaterialButtonToggleGroup priorityToggleGroup;
    private TextView tvHeaderTitle;
    private ImageView btnDelete;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Task currentTask;
    private boolean isEditMode = false;

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

        setContentView(R.layout.activity_add_edit_task);

        dbHelper = new DatabaseHelper(this);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        priorityToggleGroup = findViewById(R.id.priorityToggleGroup);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        btnDelete = findViewById(R.id.btnDelete);
        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnSave = findViewById(R.id.btnSave);

        if (getIntent().hasExtra("TASK_ID")) {
            isEditMode = true;
            int taskId = getIntent().getIntExtra("TASK_ID", -1);
            currentTask = dbHelper.getTaskById(taskId);
            tvHeaderTitle.setText(R.string.edit_task);
            btnDelete.setVisibility(View.VISIBLE);
            populateFields();
        } else {
            isEditMode = false;
            tvHeaderTitle.setText(R.string.add_task_title);
            btnDelete.setVisibility(View.GONE);
            priorityToggleGroup.check(R.id.btnLow); // Default
        }

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AddEditTaskActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        btnDelete.setOnClickListener(v -> deleteTask());
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(v -> saveTask());
    }

    private void populateFields() {
        if (currentTask == null) return;
        etTitle.setText(currentTask.getTitle());
        etDescription.setText(currentTask.getDescription());
        etDate.setText(currentTask.getDueDate());
        etTime.setText(currentTask.getDueTime());
        
        int priority = currentTask.getPriority();
        if (priority == 0) priorityToggleGroup.check(R.id.btnLow);
        else if (priority == 1) priorityToggleGroup.check(R.id.btnMedium);
        else if (priority == 2) priorityToggleGroup.check(R.id.btnHigh);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            etDate.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            etTime.setText(time);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void deleteTask() {
        if (currentTask != null) {
            dbHelper.deleteTask(currentTask.getId());
            AlarmHelper.cancelTaskAlarms(this, currentTask.getId());
            Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, R.string.fill_required, Toast.LENGTH_SHORT).show();
            return;
        }

        int priority = 0;
        int checkedId = priorityToggleGroup.getCheckedButtonId();
        if (checkedId == R.id.btnHigh) priority = 2;
        else if (checkedId == R.id.btnMedium) priority = 1;

        if (isEditMode) {
            currentTask.setTitle(title);
            currentTask.setDescription(desc);
            currentTask.setDueDate(date);
            currentTask.setDueTime(time);
            currentTask.setPriority(priority);
            dbHelper.updateTask(currentTask);
        } else {
            int userId = sessionManager.getUserId();
            currentTask = new Task(userId, title, desc, date, time, priority);
            long id = dbHelper.insertTask(currentTask);
            currentTask.setId((int) id);
        }

        AlarmHelper.scheduleTaskAlarms(this, currentTask, true);
        finish();
    }

}