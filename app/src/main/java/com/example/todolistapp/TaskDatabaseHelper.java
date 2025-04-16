package com.example.todolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tasks_db";
    private static final int DB_VERSION = 3;

    private static final String TABLE_NAME = "tasks";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESC = "description";
    private static final String COL_DATE = "date";
    private static final String COL_HOUR = "hour";
    private static final String COL_MINUTE = "minute";
    private static final String COL_COMPLETED = "completed";
    private static final String COL_CATEGORY = "category";

    public TaskDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_DESC + " TEXT, "
                + COL_DATE + " TEXT, "
                + COL_HOUR + " INTEGER, "
                + COL_MINUTE + " INTEGER, "
                + COL_COMPLETED + " INTEGER DEFAULT 0, "
                + COL_CATEGORY + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_COMPLETED + " INTEGER DEFAULT 0");
        }
        if (oldV < 3) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_CATEGORY + " TEXT");
        }
    }

    // Insert a task with a specified category
    public long insertTask(String title, String description, String date, int hour, int minute, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_DESC, description);
        values.put(COL_DATE, date);
        values.put(COL_HOUR, hour);
        values.put(COL_MINUTE, minute);
        values.put(COL_COMPLETED, 0);
        values.put(COL_CATEGORY, category);  // Store category here
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }

    // Insert a Task object (with category support)
    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, task.getTitle());
        values.put(COL_DESC, task.getDescription());
        values.put(COL_DATE, task.getDate());
        values.put(COL_HOUR, task.getHour());
        values.put(COL_MINUTE, task.getMinute());
        values.put(COL_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COL_CATEGORY, task.getCategory());  // Store category from task
        db.insert(TABLE_NAME, null, values);
        db.close();
        return 0;
    }

    // Get tasks by specific date
    public List<Task> getTasksByDate(String date) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, COL_DATE + "=?",
                    new String[]{date}, null, null, COL_HOUR + "," + COL_MINUTE);
            while (cursor.moveToNext()) {
                taskList.add(buildTaskFromCursor(cursor));
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return taskList;
    }

    // Update task completion status
    public void updateTaskCompletion(int taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMPLETED, isCompleted ? 1 : 0);
        db.update(TABLE_NAME, values, COL_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    // Delete task by ID
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Helper method to convert a cursor row into a Task object
    private Task buildTaskFromCursor(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC)));
        task.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
        task.setHour(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HOUR)));
        task.setMinute(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MINUTE)));
        task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COL_COMPLETED)) == 1);
        task.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));  // Add category here
        return task;
    }
}
