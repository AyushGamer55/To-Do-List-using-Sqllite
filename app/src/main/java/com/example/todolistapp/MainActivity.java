package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskDatabaseHelper dbHelper;
    private CalendarView calendarView;
    private String selectedDate;
    private final List<Task> taskList = new ArrayList<>(); // Prevent NPE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        dbHelper = new TaskDatabaseHelper(this);

        // Initialize views
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this, taskList);
        recyclerView.setAdapter(adapter);

        // Load today's tasks by default
        selectedDate = Utils.getTodayDate();
        loadTasksForDate(selectedDate);

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < taskList.size()) {
                    Task task = taskList.get(position);
                    dbHelper.deleteTask(task.getId());
                    taskList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        }).attachToRecyclerView(recyclerView);

        // FAB click listener
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });

        // Calendar change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = Utils.formatDate(year, month, dayOfMonth);
            loadTasksForDate(selectedDate);
        });
    }

    private void loadTasksForDate(String date) {
        List<Task> newTasks = dbHelper.getTasksByDate(date);
        if (newTasks != null) {
            taskList.clear();
            taskList.addAll(newTasks);
            adapter.updateList(newTasks);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasksForDate(selectedDate);
    }
}