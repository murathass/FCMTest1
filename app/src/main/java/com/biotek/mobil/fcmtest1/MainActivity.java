package com.biotek.mobil.fcmtest1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private CustomAdapter adapter;
    private List<MMessage> messages;
    private EditText et;
    private SharedPref pref;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = findViewById(R.id.messagebox);
        pref =new SharedPref(getApplicationContext());
        db = FirebaseDatabase.getInstance();

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),et.getText().toString(),Toast.LENGTH_SHORT).show();
                        new SendTask().execute(et.getText().toString());
            }
        });

        messages = new ArrayList<>();

        listView = findViewById(R.id.list);
        adapter = new CustomAdapter(messages,getApplicationContext());
        listView.setAdapter(adapter);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    Log.e("FCM","token: "+task.getResult().getToken());
                    new MyFirebaseMessagingService(getApplicationContext()).onNewToken(task.getResult().getToken());
                }
            }
        });
    }

    private class SendTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... args) {
            try {
                String message = args[0];
                Log.e("SendTask","SendTask");
                FireMessage fm =new FireMessage(pref.getLoginInfo(),message);
                String result = fm.sendToTopic("global");
                Log.e("result",result);
                if (result.equals("SUCCESS")){
                    db.getReference("message").child(Helper.encodeToken(pref.getLoginInfo())).child("send").child(new Date().toString().substring(0,20)).setValue(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("MyData")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s =intent.getExtras().getString("data");
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            JSONObject obj=null;
            try {
                obj = new JSONObject(s);
                MMessage message = new MMessage();
                message.setContent(obj.getString("message"));
                message.setTowho("global");
                message.setId(UUID.randomUUID().toString());
                message.setAuthor(obj.getString("title"));
                message.setDate(new Date().toString().substring(0,20));
                messages.add(message);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    };
}
