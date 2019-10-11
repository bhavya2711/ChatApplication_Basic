package com.example.likhi.inclass06;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Message extends AppCompatActivity {

    TextView name;
    String fname, lname, id, fullname, token, title, status;
    ArrayList<Thread> result = new ArrayList<>();
    ListView threadView;
    ArrayAdapter threadAdapter;
    private final OkHttpClient client = new OkHttpClient();
    Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setTitle("Message Threads");
        if (getIntent() != null && getIntent().getExtras() != null) {
            threadView = findViewById(R.id.threadsList);
            name = findViewById(R.id.name);
            User user = (User) getIntent().getExtras().getSerializable(MainActivity.USER_KEY);
            fname = user.fname;
            lname = user.lname;
            id = user.id;
            fullname = fname + " " + lname;
            token = user.token;
            name.setText(fullname);
            try {
                run();
            } catch (Exception e) {
                Log.d("demo", e.toString());
            }
        }
        findViewById(R.id.quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Message.this, MainActivity.class);
                startActivity(i);
                token = null;
                finish();
            }
        });
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titleTV = findViewById(R.id.title);
                title = titleTV.getText().toString();
                try {
                    if (!title.isEmpty()) {
                        add();
                    } else {
                        Toast.makeText(Message.this, "Title is empty", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Message.this, "Failed to add thread", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void add() throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("title", title)
                .build();

        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/thread/add")
                .addHeader("Authorization", "BEARER " + token)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("demo", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String var = response.body().string();
                String thread_id = null;
                String t;
                Log.d("demo", var);
                try {
                    JSONObject root = new JSONObject(var);
                    status = root.getString("status");
                    if(status.equals("ok")){
                        t = root.getString("thread");
                        JSONObject thread = new JSONObject(t);
                        thread_id= thread.getString("id");
                    }
                 } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String finalThread_id = thread_id;
                Message.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (status.equals("ok")) {
                                thread = new Thread(finalThread_id, title, id);
                                threadAdapter.add(thread);
                                threadAdapter.notifyDataSetChanged();
                                threadView.setAdapter(threadAdapter);
                            }
                        } catch (Exception e) {
                            Log.d("demo", e.toString());
                        }
                    }
                });

            }
        });
    }

    public void run() throws Exception {
        final Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/thread")
                .addHeader("Authorization", "BEARER " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Headers responseHeaders = response.headers();
                    String body = responseBody.string();
                    JSONObject root = new JSONObject(body);
                    JSONArray threads = root.getJSONArray("threads");
                    for (int i = threads.length(); i > -1; i--) {
                        try {
                            JSONObject threadJson = threads.getJSONObject(i);
                            Thread thread = new Thread();
                            thread.user_id = threadJson.getString("user_id");
                            thread.id = threadJson.getString("id");
                            thread.title = threadJson.getString("title");
                            result.add(thread);
                        } catch (Exception e) {
                            Log.d("demo", e.toString());
                        }
                    }
                    Log.d("demo", result.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            threadAdapter = new threadAdapter(Message.this, R.layout.thread_item, result, id, token);
                            threadView.setAdapter(threadAdapter);
                        } catch (Exception e) {
                            Log.d("demo", e.toString());
                        }
                    }
                });
            }
        });
    }

}