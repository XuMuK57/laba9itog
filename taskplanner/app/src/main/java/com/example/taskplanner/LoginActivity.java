package com.example.taskplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class LoginActivity extends Activity {

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        findViewById(R.id.loginBtn).setOnClickListener(v -> loginUser());

        findViewById(R.id.registerLink).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    void loginUser() {
        String u = username.getText().toString();
        String p = password.getText().toString();

        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] fields = {"username", "password"};
        String[] data = {u, p};

        PutData putData = new PutData(Api.LOGIN, "POST", fields, data);

        if (putData.startPut() && putData.onComplete()) {
            String result = putData.getResult();

            if (result.equals("Login Success")) {
                Prefs.saveUsername(this, u);
                startActivity(new Intent(this, TaskListActivity.class));
                finish();
            } else {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
