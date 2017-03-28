package com.roots;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String USERNAME1 = "miet1";
    private static final String PASSWORD1 = "miet1";
    private static final String USERNAME2 = "miet2";
    private static final String PASSWORD2 = "miet2";

    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        final Button bSignin = (Button) findViewById(R.id.b_signin);
        final Button bSignup = (Button) findViewById(R.id.b_signup);
        bSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Username & Password required.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((username.equals(USERNAME1) && password.equals(PASSWORD1)) ||
                        username.equals(USERNAME2) && password.equals(PASSWORD2)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Please enter correct username and password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        bSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,
                        "Username and Password created as miet2", Toast.LENGTH_SHORT).show();
            }
        });
    }
}