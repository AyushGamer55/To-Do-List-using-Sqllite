package com.example.todolistapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private List<Task> tasks;
    private final TaskDatabaseHelper dbHelper;

    public TaskAdapter(Context context, List<Task> initialTasks) {
        this.context = context;
        this.tasks = initialTasks;
        this.dbHelper = new TaskDatabaseHelper(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());

        String time = String.format("%02d:%02d", task.getHour(), task.getMinute());
        holder.tvTime.setText(time);

        // ✅ Avoid checkbox listener reuse triggering events
        holder.cbTask.setOnCheckedChangeListener(null);
        holder.cbTask.setChecked(task.isCompleted());

        // ✅ Checkbox interaction & DB update
        holder.cbTask.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            task.setCompleted(isChecked);
            dbHelper.updateTaskCompletion(task.getId(), isChecked); // ✅ Persist in DB
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public void updateList(List<Task> newTaskList) {
        this.tasks.clear();
        this.tasks.addAll(newTaskList);
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTime;
        CheckBox cbTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvTime = itemView.findViewById(R.id.tv_time);
            cbTask = itemView.findViewById(R.id.cb_task);
        }
    }
}
