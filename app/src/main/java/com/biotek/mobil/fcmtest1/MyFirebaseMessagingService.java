package com.biotek.mobil.fcmtest1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;


public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private Context c;
    private SharedPref pref;
    private FirebaseDatabase db;

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        pref = new SharedPref(this);
    }

    public MyFirebaseMessagingService(){ }

    public MyFirebaseMessagingService(Context c){
        this.c = c;
        this.db = FirebaseDatabase.getInstance();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("FCM", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM", "Message data payload: " + remoteMessage.getData());
            Intent i = new Intent();
            i.setAction("MyData");
            i.putExtra("data",remoteMessage.getData().toString());
            FirebaseDatabase.getInstance().getReference("message").child(Helper.encodeToken(pref.getLoginInfo())).child("recieve").child(new Date().toString().substring(0,20)).setValue(remoteMessage.getData().toString());

            broadcaster.sendBroadcast(i);

            if (true) {
                //TODO scheduleJob();
            } else {
                //TODO handleNow();"
            }
        }

        if (remoteMessage.getNotification() != null) {
            String mes = remoteMessage.getNotification().getBody();
            Log.d("FCM", "Message Notification Body123: " + mes);

        }
    }

    @Override
    public void onNewToken(String token) {

        Log.e("FCM Token", "Refreshed token: " + token);
        SendRegistrationToServer(token);
        
    }

    private void SendRegistrationToServer(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String tokencode = Helper.encodeToken(token);
        String email = new SharedPref(c).getLoginInfo();
        String emailcode = Helper.encodeToken(email);
        Log.e("emailcode",emailcode);
        Log.e("tokencode",tokencode);
         db.getReference("user").child(emailcode).child("token").setValue(tokencode).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     Log.e("SendRegistToServer","Tokken Send Succesfull");
                 }
             }
         });
        FirebaseMessaging.getInstance().subscribeToTopic("global");
    }

}
