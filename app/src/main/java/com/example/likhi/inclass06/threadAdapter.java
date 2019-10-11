package com.example.likhi.inclass06;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class threadAdapter extends ArrayAdapter<Thread> {
    String user_id;
    String token;
    String thread_id;
    String title;
    List<Thread> objects;
    private final OkHttpClient client = new OkHttpClient();

    public threadAdapter(@NonNull Context context, int resource, @NonNull List<Thread> objects, String id, String token) {
        super(context, resource, objects);
        this.objects = objects;
        user_id = id;
        this.token = token;

    }

    @NonNull
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Thread thread = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.thread_item, parent, false);
        }
        title = thread.title;
        thread_id = thread.id;
        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(thread.title);
        ImageView delete = (ImageView) convertView.findViewById(R.id.deleteThread);
        delete.setVisibility(convertView.GONE);
        try {
            if (user_id.equals(thread.user_id)) {
                delete.setVisibility(convertView.VISIBLE);
            }
        } catch (Exception e) {
            Log.d("demo", e.toString());
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final Thread thread = getItem(position);
                    String thread_id = thread.id;
                    Log.d("demo",thread.toString());
                    delete(thread_id);
                    objects.remove(position);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }

    public void delete(String thread_id) throws Exception {
        Log.d("demo", "this thread's id " + thread_id);
        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/thread/delete/" + thread_id)
                .addHeader("Authorization", "BEARER " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("demo", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String var = response.body().string();
                Log.d("demo", var);
            }
        });
    }
}
