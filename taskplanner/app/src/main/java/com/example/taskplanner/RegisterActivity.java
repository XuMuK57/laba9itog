package com.example.taskplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class RegisterActivity extends Activity {

    EditText fullname, email, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        findViewById(R.id.registerBtn).setOnClickListener(v -> registerUser());
    }

    void registerUser() {
        String f = fullname.getText().toString();
        String e = email.getText().toString();
        String u = username.getText().toString();
        String p = password.getText().toString();

        if (f.isEmpty() || e.isEmpty() || u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] fields = {"fullname", "email", "username", "password"};
        String[] data = {f, e, u, p};

        PutData putData = new PutData(Api.SIGNUP, "POST", fields, data);

        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();

                if (result.equals("Sign Up Success")) {

                    Prefs.saveUsername(this, u);

                    Toast.makeText(this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();

                } else {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
