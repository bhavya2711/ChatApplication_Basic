package com.example.likhi.inclass06;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import static com.example.likhi.inclass06.MainActivity.USER_KEY;

public class Signup extends AppCompatActivity {
    EditText email,password,rpassword,fname,lname;
    String emailS,passwordS,fnameS,lnameS,rpasswordS,status="",message="failed to signup";
    String user_id,token;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Sign Up");
        email=findViewById(R.id.emails);
        password=findViewById(R.id.password);
        fname = findViewById(R.id.Fname);
        lname = findViewById(R.id.Lname);
        rpassword = findViewById(R.id.rpassword);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Signup.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailS = email.getText().toString();
                passwordS = password.getText().toString();
                rpasswordS = rpassword.getText().toString();
                fnameS = fname.getText().toString();
                lnameS = lname.getText().toString();

                if(TextUtils.isEmpty(fnameS)){
                    fname.setError("First Name Can't be empty");
                }
                else if(TextUtils.isEmpty(lnameS)){
                    lname.setError("Last Name Can't be empty");
                }
                else if(TextUtils.isEmpty(emailS)){
                    email.setError("Email Can't be empty");
                }
                else if(!emailS.matches(emailPattern)){
                    email.setError("Enter Valid Email 'demo@test.com' ");
                }
                else if(TextUtils.isEmpty(passwordS)){
                    password.setError("Password can't be empty");
                }
                else if(passwordS.length()<6){
                    password.setError("Password Should be of 6 or more Charecters");
                }
                else if(TextUtils.isEmpty(rpasswordS)){
                    password.setError("Repeat Password can't be empty");
                }
                else if(!rpasswordS.equals(passwordS)){
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else {

                    try {
                        signUp(emailS, passwordS, fnameS, lnameS);
                    } catch (Exception e) {
                        Log.d("demo", e.toString());
                    }
                }
            }
        });
    }

    private void signUp(String email, String password, String fname, String lname) throws Exception{
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("fname", fname)
                .add("lname", lname)
                .build();

        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String var = response.body().string();
                try {
                    JSONObject root = new JSONObject(var);
                    JSONObject thread = new JSONObject();
                     status = root.getString("status");
                     if(status.equals("error")) {
                         message = root.getString("message");
                     }
                    if(status.equals("ok")){
                        token = root.getString("token");
                        user_id = root.getString("user_id");

                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("demo",e.toString());
                }
                Signup.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status.equals("error")){
                            Toast.makeText(Signup.this, message, Toast.LENGTH_SHORT).show();
                        }
                        if(status.equals("ok")){

                            User user = new User(fnameS, lnameS, token, user_id);
                            Intent intent = new Intent(Signup.this, Message.class);
                            intent.putExtra(USER_KEY, user);
                            intent.addCategory(intent.CATEGORY_DEFAULT);
                            startActivity(intent);
                            finish();

                        }
                    }
                });
            }
        });

    }
}
