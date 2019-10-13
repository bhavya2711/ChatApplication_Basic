package com.example.likhi.inclass06;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/*
Assignment: In class 06
File: GroupR1_Inclass06.zip
Group: Group R1
 */

public class MainActivity extends AppCompatActivity {
    static String USER_KEY = "USER";
    Button login, signup;
    EditText email, password;
    String emailLogin, passwordLogin, token, status = "status";
    String message = "Incorrect email and/or password";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        email = findViewById(R.id.eml);
        password = findViewById(R.id.pwd);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin = email.getText().toString();
                passwordLogin = password.getText().toString();
                if (TextUtils.isEmpty(emailLogin)) {
                    email.setError("Email cant be empty");
                }
                else if(!emailLogin.matches(emailPattern)){
                    email.setError("email should be in this form 'demo@test.com' ");
                }
                else if(TextUtils.isEmpty(passwordLogin)){
                    password.setError("Password can't be Empty");
                }
                else{
                    try {
                        login(emailLogin, passwordLogin);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error logging in", Toast.LENGTH_SHORT).show();
                    }
            }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Signup.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void login(String email, String password) throws Exception {
        try {

            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("demo", e.toString());
                    Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String var = response.body().string();
                    try {
                        JSONObject root = new JSONObject(var);
                        status = root.getString("status");
                        if (status.equals("error")) {
                            message = root.getString("message");
                        }
                        String fname;
                        String lname;
                        String id;
                        if (status.equalsIgnoreCase("ok")) {
                            token = root.getString("token");
                            fname = root.getString("user_fname");
                            lname = root.getString("user_lname");
                            id = root.getString("user_id");
                            Log.d("demo", token);
                            User user = new User(fname, lname, token, id);
                            Intent intent = new Intent(MainActivity.this, Message.class);
                            intent.putExtra(USER_KEY, user);
                            intent.addCategory(intent.CATEGORY_DEFAULT);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("demo", e.toString());
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status.equalsIgnoreCase("error")) {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
