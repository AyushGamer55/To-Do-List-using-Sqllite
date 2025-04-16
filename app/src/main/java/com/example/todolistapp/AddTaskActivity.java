package com.example.todolistapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText editTitle, editDescription;
    private Button saveButton, pickTimeButton, selectCategoryButton;
    private ImageView categoryImage;
    private TextView categoryTitle, selectedTimeText;
    private int hour = -1, minute = -1;
    private String selectedDate;
    private String selectedCategory = "Uncategorized";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTitle = findViewById(R.id.editTextTitle);
        editDescription = findViewById(R.id.editTextDescription);
        saveButton = findViewById(R.id.saveButton);
        pickTimeButton = findViewById(R.id.pickTimeButton);
        selectCategoryButton = findViewById(R.id.selectCategoryButton);
        categoryImage = findViewById(R.id.categoryImage);
        categoryTitle = findViewById(R.id.categoryTitle);
        selectedTimeText = findViewById(R.id.selectedTimeText);

        selectedDate = getIntent().getStringExtra("selectedDate");

        categoryImage.setImageResource(R.drawable.task);
        categoryTitle.setText("No Category Selected");

        registerForContextMenu(selectCategoryButton);
        selectCategoryButton.setOnClickListener(v -> v.showContextMenu());

        pickTimeButton.setOnClickListener(v -> openTimePicker());

        saveButton.setOnClickListener(v -> saveTask());
    }

    private void openTimePicker() {
        pickTimeButton.setEnabled(false);
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build();

        picker.show(getSupportFragmentManager(), "TIME_PICKER");

        picker.addOnPositiveButtonClickListener(v -> {
            hour = picker.getHour();
            minute = picker.getMinute();
            String formattedTime = String.format("%02d:%02d", hour, minute);
            pickTimeButton.setText(formattedTime);
            selectedTimeText.setText("Selected Time: " + formattedTime);
        });

        picker.addOnDismissListener(dialog -> pickTimeButton.setEnabled(true));
    }

    private void saveTask() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty() || selectedDate == null || hour == -1 || minute == -1) {
            Toast.makeText(this, "Title, Date, and Time are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDate(selectedDate);
        task.setHour(hour);
        task.setMinute(minute);
        task.setCompleted(false);
        task.setCategory(selectedCategory);

        TaskDatabaseHelper db = new TaskDatabaseHelper(this);
        long taskId = db.insertTask(task);

        if (taskId != -1) {
            scheduleNotification(taskId, title);
            Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
    }

    // Handle category selection
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        int imageRes = R.drawable.task;

        // Match category IDs to appropriate images using if-else
        if (item.getItemId() == R.id.Groceryshopping) {
            imageRes = R.drawable.p3;
        } else if (item.getItemId() == R.id.Laundry) {
            imageRes = R.drawable.p2;
        } else if (item.getItemId() == R.id.Exercise) {
            imageRes = R.drawable.p5;
        } else if (item.getItemId() == R.id.Reading) {
            imageRes = R.drawable.p11;
        } else if (item.getItemId() == R.id.Projectdeadlines) {
            imageRes = R.drawable.p9;
        } else if (item.getItemId() == R.id.Meetings) {
            imageRes = R.drawable.p13;
        } else if (item.getItemId() == R.id.Reports) {
            imageRes = R.drawable.p10;
        } else if (item.getItemId() == R.id.TeaTime) {
            imageRes = R.drawable.p1;
        } else if (item.getItemId() == R.id.EatingBreak) {
            imageRes = R.drawable.p7;
        } else if (item.getItemId() == R.id.Email) {
            imageRes = R.drawable.p12;
        } else if (item.getItemId() == R.id.Budgeting) {
            imageRes = R.drawable.p15;
        } else if (item.getItemId() == R.id.Yoga) {
            imageRes = R.drawable.p8;
        } else if (item.getItemId() == R.id.Anniversaries) {
            imageRes = R.drawable.p14;
        } else if (item.getItemId() == R.id.WashingClothes) {
            imageRes = R.drawable.p6;
        } else if (item.getItemId() == R.id.Eventplanning) {
            imageRes = R.drawable.p4;
        }

        // Set the category name and image
        categoryTitle.setText(title);
        categoryImage.setImageResource(imageRes);
        return true;
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(long taskId, String title) {
        Intent intent = new Intent(this, ReminderReciever.class);
        intent.putExtra("title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) taskId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String[] parts = selectedDate.split("-");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(parts[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
