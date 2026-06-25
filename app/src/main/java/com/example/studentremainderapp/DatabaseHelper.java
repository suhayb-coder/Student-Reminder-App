package com.example.studentremainderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_tasks.db";
    private static final int DATABASE_VERSION = 1;

    // Table and Column Names
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_DUE_TIME = "due_time";
    public static final String COLUMN_PRIORITY = "priority"; // 0: Low, 1: Medium, 2: High
    public static final String COLUMN_IS_COMPLETED = "is_completed";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Create Table Query
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_DUE_DATE + " TEXT NOT NULL,"
            + COLUMN_DUE_TIME + " TEXT NOT NULL,"
            + COLUMN_PRIORITY + " INTEGER DEFAULT 0,"
            + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

// --- CRUD METHODS ---

    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_DUE_TIME, task.getDueTime());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_DUE_TIME, task.getDueTime());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        return db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " ORDER BY " + COLUMN_DUE_DATE + " ASC, " + COLUMN_DUE_TIME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    public Task getTaskById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Task task = cursorToTask(cursor);
            cursor.close();
            return task;
        }
        return null;
    }

    public List<Task> getTasksByStatus(boolean isCompleted) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_IS_COMPLETED + "=?",
                new String[]{isCompleted ? "1" : "0"}, null, null, COLUMN_DUE_DATE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    public List<Task> getTasksByDate(String date) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_DUE_DATE + "=?",
                new String[]{date}, null, null, COLUMN_DUE_TIME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
        task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
        task.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)));
        task.setDueTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_TIME)));
        task.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)));
        task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1);
        return task;
    }
}