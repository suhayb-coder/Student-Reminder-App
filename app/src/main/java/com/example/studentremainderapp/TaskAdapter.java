package com.example.studentremainderapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private Context context;
    private DatabaseHelper dbHelper;

    public TaskAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
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
        holder.tvDateTime.setText("Due " + task.getDueTime());
        holder.cbCompleted.setChecked(task.isCompleted());

        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            holder.tvDescription.setText(task.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // UI Styling based on priority
        int priority = task.getPriority();
        if (priority == 2) { // High
            holder.priorityIndicator.setBackgroundResource(R.color.high_priority);
            holder.tvPriorityBadge.setText("High Priority");
            holder.tvPriorityBadge.setBackgroundResource(R.drawable.bg_badge_high);
            holder.tvPriorityBadge.setTextColor(context.getColor(R.color.high_priority_text));
            holder.tvPriorityBadge.setVisibility(View.VISIBLE);
        } else if (priority == 1) { // Medium
            holder.priorityIndicator.setBackgroundResource(R.color.medium_priority);
            holder.tvPriorityBadge.setText("Medium Priority");
            holder.tvPriorityBadge.setBackgroundResource(R.drawable.bg_badge_medium);
            holder.tvPriorityBadge.setTextColor(context.getColor(R.color.medium_priority_text));
            holder.tvPriorityBadge.setVisibility(View.VISIBLE);
        } else { // Low
            holder.priorityIndicator.setBackgroundResource(R.color.low_priority);
            holder.tvPriorityBadge.setText("Low Priority");
            holder.tvPriorityBadge.setBackgroundResource(R.drawable.bg_badge_low);
            holder.tvPriorityBadge.setTextColor(context.getColor(R.color.low_priority_text));
            holder.tvPriorityBadge.setVisibility(View.VISIBLE);
        }

        // Completed styling
        if (task.isCompleted()) {
            holder.tvTitle.setTextColor(Color.GRAY);
            holder.tvDateTime.setText("Done yesterday");
            holder.tvTitle.setAlpha(0.6f);
            holder.tvPriorityBadge.setText("Completed");
            holder.tvPriorityBadge.setBackgroundResource(R.drawable.bg_badge_low); // Neutral/Greyish would be better
            holder.tvPriorityBadge.setTextColor(Color.GRAY);
            holder.priorityIndicator.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.tvTitle.setTextColor(Color.parseColor("#333333"));
            holder.tvTitle.setAlpha(1.0f);
        }

        holder.cbCompleted.setOnClickListener(v -> {
            task.setCompleted(holder.cbCompleted.isChecked());
            dbHelper.updateTask(task);
            if (context instanceof MainActivity) {
                ((MainActivity) context).onResume(); // Refresh progress
            }
            notifyItemChanged(position);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTaskActivity.class);
            intent.putExtra("TASK_ID", task.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateTime, tvPriorityBadge, tvDescription;
        CheckBox cbCompleted;
        View priorityIndicator;
        ImageView ivTimeIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPriorityBadge = itemView.findViewById(R.id.tvPriorityBadge);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            ivTimeIcon = itemView.findViewById(R.id.ivTimeIcon);
        }
    }
}