package com.example.taskplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public interface OnTaskClick {
        void onClick(Task task);
        void onLongClick(Task task);
    }

    private List<Task> tasks;
    private OnTaskClick listener;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setListener(OnTaskClick l) {
        this.listener = l;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time;
        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.taskText);
            date  = view.findViewById(R.id.deadlineDate);
            time  = view.findViewById(R.id.deadlineTime);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task t = tasks.get(position);

        holder.title.setText(nullToEmpty(t.title));

        String dl = nullToEmpty(t.deadline);
        holder.date.setText(formatDateRu(dl));
        holder.time.setText(formatTime(dl));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(t);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(t);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }
    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String formatDateRu(String s) {
        if (s == null) return "";
        String x = s.trim().replace('T', ' ');

        int space = x.indexOf(' ');
        String datePart = (space > 0) ? x.substring(0, space) : x;

        if (datePart.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            return datePart;
        }

        if (datePart.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String yyyy = datePart.substring(0, 4);
            String mm = datePart.substring(5, 7);
            String dd = datePart.substring(8, 10);
            return dd + "." + mm + "." + yyyy;
        }

        return datePart;
    }

    private static String formatTime(String s) {
        if (s == null) return "";
        String x = s.trim().replace('T', ' ');

        int space = x.indexOf(' ');
        if (space < 0 || space + 1 >= x.length()) return "";

        String timeAndZone = x.substring(space + 1);
        if (timeAndZone.length() >= 5) {
            return timeAndZone.substring(0, 5);
        }
        return timeAndZone;
    }
}
