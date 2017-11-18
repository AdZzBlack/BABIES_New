package com.inspira.babies;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by shoma on 7/26/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        GlobalVar global = new GlobalVar(this);

        String massage = remoteMessage.getData().get("message");
        String title = ""; title += remoteMessage.getData().get("title");
        String fragment = ""; fragment+= remoteMessage.getData().get("fragment");
        Log.d("notifasd",fragment);


        if(!fragment.equals(""))
        {
            LibInspira.setShared(global.userpreferences,global.user.notification_go_to_fragment,fragment);
        }

        Intent intent;
        intent = new Intent(this,Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("BABIES : "+title);
        notificationBuilder.setContentText(massage);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.gms_launcher);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setDeleteIntent(createOnDismissedIntent(this,0)); //0 = notificationID;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
//        if(!fragment.equals("")) {
//            notificationManager.notify(Integer.parseInt(fragment), notificationBuilder.build());
//        }
//        else
//        {
//            notificationManager.notify(0, notificationBuilder.build());
//        }

        super.onMessageReceived(remoteMessage);
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("com.inspira.babies.notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }


//    @Override
//    public void onTaskRemoved(Intent rootIntent){
//        Log.d("onSwipe","lala");
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//
//        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 1000,
//                restartServicePendingIntent);
//
//        super.onTaskRemoved(rootIntent);
//    }

//    @Override
//    public void onDestroy() {
//        Log.d("onSwipe","dest");
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//
//        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 1000,
//                restartServicePendingIntent);
//
//        super.onDestroy();
//    }
}
