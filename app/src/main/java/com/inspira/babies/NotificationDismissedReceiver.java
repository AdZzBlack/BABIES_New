package com.inspira.babies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Arta on 16-Nov-17.
 */

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // reset go_to_fragment
        int notificationId = intent.getExtras().getInt("com.inspira.babies.notificationId");
        //Log.d("OnNotificationSwipe","notif swipeee");
        GlobalVar global = new GlobalVar(context);
        LibInspira.setShared(global.userpreferences,global.user.notification_go_to_fragment,"");
    }
}