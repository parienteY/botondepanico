package com.example.mibotondepanico.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.example.mibotondepanico.Login;
import com.example.mibotondepanico.MapsActivity;
import com.example.mibotondepanico.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Fcm extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from= remoteMessage.getFrom();
        Log.e("TAG","mensaje recibido de: "+from);
        if(remoteMessage.getData().size()>0){
            Log.e("TAG","mi titulo es: "+remoteMessage.getData().get("titulo"));
            Log.e("TAG","mi detalle es: "+remoteMessage.getData().get("detalle"));

            String titulo= remoteMessage.getData().get("titulo");
            String detalle= remoteMessage.getData().get("detalle");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                mayorqueoreo(titulo,detalle);
            }
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ){
                menorqueoreo(titulo, detalle);
            }
        }

    }



    private void mayorqueoreo(String titulo, String detalle){
        String id="mensaje";
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,id);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nc = new NotificationChannel(id,"nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nc.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            nm.createNotificationChannel(nc);

        }
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.drawable.baseline_sms_24)
                .setContentText(detalle)
                .setContentIntent(clicknoti())
                .setContentInfo("nuevo")
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(detalle));
        Random random = new Random();
        int idNotify=random.nextInt(8000);

        assert nm != null;
        nm.notify(idNotify,builder.build());

    }
    private void menorqueoreo(String titulo, String detalle) {
        String id="mensaje";
        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext(),id);
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.drawable.baseline_sms_24)
                .setContentText(detalle)
                .setContentIntent(clicknoti())
                .setContentInfo("nuevo")
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(detalle));



        Random random = new Random();
        int idNotify=random.nextInt(8000);
        NotificationManagerCompat nm= NotificationManagerCompat.from(getApplicationContext());
        //NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        assert nm != null;
        nm.notify(idNotify,builder.build());
    }
    public PendingIntent clicknoti(){
        Intent nf = new Intent(getApplicationContext(), Login.class);
        nf.putExtra("noticlick",true);
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,0,nf,0);
    }
}
