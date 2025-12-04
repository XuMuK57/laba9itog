package com.example.taskplanner;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskEditActivity extends Activity {
    EditText title, description, deadlineDate, deadlineTime;
    int editingId = -1;

    private static final long MIN_30 = 30L * 60L * 1000L;
    private static final Pattern TASK_ID_RE = Pattern.compile("\"task_id\"\\s*:\\s*(\\d+)");

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_task_edit);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        deadlineDate = findViewById(R.id.deadline_date);
        deadlineTime = findViewById(R.id.deadline_time);

        if (getIntent().hasExtra("task_id")) {
            editingId = getIntent().getIntExtra("task_id", -1);
            title.setText(getIntent().getStringExtra("title"));
            description.setText(getIntent().getStringExtra("description"));

            String dl = getIntent().getStringExtra("deadline");
            if (dl != null) {
                dl = dl.trim().replace('T', ' ');
                String[] parts = dl.split("\\s+");
                if (parts.length >= 1) deadlineDate.setText(toRuDate(parts[0]));
                if (parts.length >= 2) deadlineTime.setText(parts[1].length() >= 5 ? parts[1].substring(0, 5) : parts[1]);
            }
        }

        Button save = findViewById(R.id.saveBtn);
        save.setOnClickListener(v -> saveTask());
    }

    void saveTask() {
        String t = title.getText().toString().trim();
        String d = description.getText().toString().trim();
        String date = deadlineDate.getText().toString().trim();
        String time = deadlineTime.getText().toString().trim();
        String username = Prefs.getUsername(this);

        if (t.isEmpty() || d.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        long deadlineMillis = parseMillis(date, time);
        String dlForServer = toIsoDateTime(date, time);

        if (editingId == -1) {
            String[] fields = {"username", "title", "description", "deadline_at"};
            String[] data = {username, t, d, dlForServer};

            PutData put = new PutData(Api.CREATE_TASK, "POST", fields, data);

            if (put.startPut() && put.onComplete()) {
                String result = put.getResult();

                int baseId = extractTaskIdOrFallback(result, username, t, deadlineMillis);

                cancelTwoAlarms(baseId);

                scheduleTwoAlarms(baseId, deadlineMillis,
                        "Через 30 минут дедлайн: " + t,
                        "Дедлайн: " + t
                );

                finish();
            }
        }

        else {
            String[] fields = {"username", "task_id", "title", "description", "deadline_at", "status"};
            String[] data = {
                    username,
                    String.valueOf(editingId),
                    t,
                    d,
                    dlForServer,
                    "pending"
            };

            PutData put = new PutData(Api.UPDATE_TASK, "POST", fields, data);

            if (put.startPut() && put.onComplete()) {
                cancelTwoAlarms(editingId);
                scheduleTwoAlarms(editingId, deadlineMillis,
                        "Через 30 минут дедлайн: " + t,
                        "Дедлайн: " + t
                );

                finish();
            }
        }
    }

    private void scheduleTwoAlarms(int baseId, long deadlineMillis, String textBefore, String textExact) {
        long now = System.currentTimeMillis();
        long beforeMillis = deadlineMillis - MIN_30;

        if (beforeMillis > now) {
            scheduleOneAlarm(baseId * 10 + 1, beforeMillis, textBefore);
        }

        if (deadlineMillis > now) {
            scheduleOneAlarm(baseId * 10 + 2, deadlineMillis, textExact);
        }
    }

    private void cancelTwoAlarms(int baseId) {
        cancelOneAlarm(baseId * 10 + 1);
        cancelOneAlarm(baseId * 10 + 2);
    }

    private void scheduleOneAlarm(int requestCode, long triggerAtMillis, String text) {

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (am == null) return;

        Intent i = new Intent(this, DeadlineReceiver.class);
        i.putExtra("text", text);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                requestCode,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        }
    }

    private void cancelOneAlarm(int requestCode) {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (am == null) return;

        Intent i = new Intent(this, DeadlineReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                requestCode,
                i,
                PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        if (pi != null) {
            am.cancel(pi);
            pi.cancel();
        }
    }

    private static long parseMillis(String ruDate, String hm) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("ru", "RU"));
        fmt.setLenient(false);
        try {
            return fmt.parse(ruDate.trim() + " " + hm.trim()).getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis() + 60_000;
        }
    }

    private static String toIsoDateTime(String ruDate, String hm) {
        String dd = ruDate.substring(0, 2);
        String mm = ruDate.substring(3, 5);
        String yyyy = ruDate.substring(6, 10);

        String hh = hm.substring(0, 2);
        String mi = hm.substring(3, 5);

        return yyyy + "-" + mm + "-" + dd + " " + hh + ":" + mi + ":00";
    }

    private static String toRuDate(String date) {
        if (date == null) return "";
        if (date.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) return date;
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String yyyy = date.substring(0, 4);
            String mm = date.substring(5, 7);
            String dd = date.substring(8, 10);
            return dd + "." + mm + "." + yyyy;
        }
        return date;
    }

    private static int extractTaskIdOrFallback(String result, String username, String title, long deadlineMillis) {
        if (result != null) {
            Matcher m = TASK_ID_RE.matcher(result);
            if (m.find()) {
                try { return Integer.parseInt(m.group(1)); } catch (Exception ignored) {}
            }
        }
        return Math.abs((username + "|" + title + "|" + deadlineMillis).hashCode());
    }
}
