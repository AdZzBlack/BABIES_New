package com.inspira.babies;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by shoma on 7/26/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();

        Log.d("REG_TOKEN3", recent_token);
        GlobalVar global= new GlobalVar(this);
        LibInspira.setShared(global.installPreferences, global.install.installed_token,recent_token);
        Log.d("token_login","tokenfb : "+LibInspira.getShared(global.installPreferences, global.install.installed_token, ""));

//        SharedPreferences sharedPreferences = getApplicationContext().
//                getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
//        SharedPreferences.Editor  editor = sharedPreferences.edit();
//        editor.putString(getString(R.string.FCM_TOKEN), recent_token);
//        editor.commit();
    }

}
