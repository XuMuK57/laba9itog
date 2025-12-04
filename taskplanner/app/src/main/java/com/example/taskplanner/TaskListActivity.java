package com.example.taskplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaskListActivity extends Activity {
    RecyclerView recycler;
    TaskAdapter adapter;
    ArrayList<Task> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_task_list);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(tasks);
        recycler.setAdapter(adapter);

        adapter.setListener(new TaskAdapter.OnTaskClick() {
            @Override
            public void onClick(Task task) {
                Intent intent = new Intent(TaskListActivity.this, TaskEditActivity.class);
                intent.putExtra("task_id", task.id);
                intent.putExtra("title", task.title);
                intent.putExtra("description", task.description);
                intent.putExtra("deadline", task.deadline);
                intent.putExtra("status", task.status);
                startActivity(intent);
            }

            @Override
            public void onLongClick(Task task) {
                new AlertDialog.Builder(TaskListActivity.this)
                        .setTitle("Удалить задачу?")
                        .setMessage(task.title)
                        .setPositiveButton("Удалить", (d, w) -> deleteTask(task))
                        .setNegativeButton("Отмена", null)
                        .show();
            }
        });

        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v ->
                startActivity(new Intent(TaskListActivity.this, TaskEditActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    void loadTasks() {

        String username = Prefs.getUsername(this);
        if (username.isEmpty()) return;

        String[] fields = {"username"};
        String[] data = {username};

        PutData put = new PutData(Api.LIST_TASKS, "POST", fields, data);

        if (put.startPut() && put.onComplete()) {
            try {
                JSONObject resp = new JSONObject(put.getResult());

                if (!resp.getBoolean("success")) {
                    Toast.makeText(this, resp.getString("message"), Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONArray arr = resp.getJSONObject("data").getJSONArray("tasks");
                tasks.clear();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);

                    tasks.add(new Task(
                            o.getInt("id"),
                            o.getString("title"),
                            o.getString("description"),
                            o.optString("deadline_at", ""),
                            o.optString("status", "pending")
                    ));
                }

                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                Toast.makeText(this, "JSON error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    void deleteTask(Task task) {
        String username = Prefs.getUsername(this);

        String[] fields = {"username", "task_id"};
        String[] data = {username, String.valueOf(task.id)};

        PutData put = new PutData(Api.DELETE_TASK, "POST", fields, data);

        if (put.startPut() && put.onComplete()) {
            try {
                JSONObject resp = new JSONObject(put.getResult());

                if (resp.getBoolean("success")) {
                    tasks.remove(task);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, resp.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
