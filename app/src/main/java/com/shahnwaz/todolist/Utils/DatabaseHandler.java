package com.shahnwaz.todolist.Utils;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.shahnwaz.todolist.AlarmReceiver;
import com.shahnwaz.todolist.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "todoListDatabase";
    private static final String TABLE_TODO = "todo";

    // Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_TASK = "task";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COMPLETION_DATE = "completionDate";
    private static final String KEY_STATUS = "status";

    // Create table SQL query
    private static final String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_TODO + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TASK + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_COMPLETION_DATE + " TEXT,"
            + KEY_STATUS + " INTEGER"
            + ")";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void scheduleAlarm(Context context, long alarmTimeInMillis, String taskTitle) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("task_title", taskTitle); // Pass task title to show in notification
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm one day before the completion date
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis - 86400000, pendingIntent); // 86400000 milliseconds = 1 day
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TODO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);

        // Create tables again
        onCreate(db);
    }

    // Open the database connection
    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    // Close the database connection
    public void closeDatabase() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Insert a task into the database
    public long insertTask(ToDoModel task) {
        ContentValues values = new ContentValues();
        values.put(KEY_TASK, task.getTask());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_COMPLETION_DATE, task.getCompletionDate());
        values.put(KEY_STATUS, task.getStatus());

        // Insert row
        return db.insert(TABLE_TODO, null, values);
    }

    // Retrieve all tasks from the database
    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TODO;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                ToDoModel task = new ToDoModel();
                task.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                task.setTask(cursor.getString(cursor.getColumnIndex(KEY_TASK)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                task.setCompletionDate(cursor.getString(cursor.getColumnIndex(KEY_COMPLETION_DATE)));
                task.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        // Close cursor to release resources
        cursor.close();

        return taskList;
    }

    // Update an existing task
    // Update an existing task
    public int updateTask(int taskId, String taskText, String description, String completionDate) {
        ContentValues values = new ContentValues();
        values.put(KEY_TASK, taskText);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_COMPLETION_DATE, completionDate);

        // Updating row
        return db.update(TABLE_TODO, values, KEY_ID + " = ?", new String[]{String.valueOf(taskId)});
    }


    // Update task status
    public int updateStatus(int taskId, int status) {
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);

        // Updating row
        return db.update(TABLE_TODO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(taskId)});
    }

    // Delete a task from the database
    public void deleteTask(int taskId) {
        db.delete(TABLE_TODO, KEY_ID + " = ?",
                new String[]{String.valueOf(taskId)});
    }
}
