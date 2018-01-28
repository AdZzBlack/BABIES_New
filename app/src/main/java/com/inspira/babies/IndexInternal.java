package com.inspira.babies;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import layout.ChangePasswordFragment;
import layout.ChatData;
import layout.ChatFragment;
import layout.ChatMsgContainer;
import layout.ChooseCabangFragment;
import layout.ChooseCustomerProspectingFragment;
import layout.ChooseGroupFragment;
import layout.ChooseJenisFragment;
import layout.ChoosePeriodeFragment;
import layout.ContactFragment;
import layout.DashboardInternalFragment;
import layout.FilterSalesOmzetFragment;
import layout.PenjualanFragment;
import layout.SalesNavigationFragment;
import layout.SalesOrderListFragment;
import layout.SettingFragment;
import layout.StockMonitoringFragment;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public class IndexInternal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "indexInternal";
    private String qwe = "logSave";
    public static GlobalVar global;
    public static JSONObject jsonObject;   //added by Tonny @30-Jul-2017
    public static TextView tvUsername, tvSales, tvTarget;  //modified by Tonny @02-Aug-2017
    public static NavigationView navigationView;
    private static Context context;  //added by Tonny @02-Aug-2017

    public static Socket mSocket;
    public static ChatFragment chatFrag;
    public static List<ChatData> listChatData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index_internal);

        //##SAVECHAT

        //load data dari sharedpref dlu
        loadOldDataChat();

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.connect();



        LibInspira.setShared(GlobalVar.chatPreferences, GlobalVar.chat.chat_menu_position, "indexInternal");

        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("appStart", onAppStart);
        mSocket.on("loadAllRoom", loadAllRoom);
        mSocket.on("loadData",loadData);
        mSocket.on("new message", onNewMessage);
//        mSocket.on("ack",onACK);
//
//        mSocket.emit("onACK","test",new Ack(){
//            @Override
//            public void call(Object... args) {
//                Log.d(TAG,"ack from emit "+args[0]);
//            }
//        });

        mSocket.emit("appStart",LibInspira.getShared(global.userpreferences, global.user.nama, ""));
        // get room pakai id_user
        mSocket.emit("loadAllRoom",LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
        //mSocket.emit("loadData",LibInspira.getShared(global.userpreferences, global.user.nomor, ""));

        chatFrag = new ChatFragment();
        chatFrag.setup(this.getApplicationContext());

        // Start Registering FCM
        Intent intent = new Intent(this, MyFirebaseInstanceIDService.class);
        startService(intent);

        global = new GlobalVar(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        context = getApplicationContext();
        LibInspira.AddFragment(this.getSupportFragmentManager(), R.id.fragment_container, new DashboardInternalFragment());
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LibInspira.clearShared(global.salespreferences); //added by Tonny @03-Aug-2017 untuk testing
        RefreshUserData();

        if(!LibInspira.getShared(global.userpreferences,global.user.notification_go_to_fragment,"").equals(""))
        {
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new PenjualanFragment());
        }

        //added by Shodiq @01-Aug-2017
        // Permission for enabling location feature only for SDK Marshmallow | Android 6
        if (Build.VERSION.SDK_INT >= 23)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1600);

        // made by Shodiq @8-aug-2017
        // check GPS status and ask to activate if GPS is disabled
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
//            startService(new Intent(getApplicationContext(), GMSbackgroundTask.class));
//        } else {
//            Runnable commandOk = new Runnable() {
//                @Override
//                public void run() {
//                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(myIntent);
//                }
//            };
//            LibInspira.alertbox("Enable Location", "Your Locations Settings is disabled.\nPlease Enable Location to use this app", this, commandOk, null);
//        }
    }

    // ## CHAT ##
    private Emitter.Listener onAppStart = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            try {
                //Log.d(TAG,data.getString("log")+"");
                data.getString("log");
            } catch (JSONException e) {
                return;
            }
        }
    };

    private Emitter.Listener loadAllRoom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            try {
                //Log.d(TAG,"load all room "+data.getString("log"));
                data.getString("log");
            } catch (JSONException e) {
                return;
            }
        }
    };

    private Emitter.Listener loadData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            List<ChatData.roomInfo> listDataRoom = new ArrayList<>();
            List<ChatMsgContainer> listDataPendingChat = new ArrayList<>();

            try {
                String result = data.getString("dataRoomInfo");
                result = result.replace("\\","");
                Log.d(TAG,result);
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        String[] dataStr = new String[obj.length()];

//                        Log.d(TAG,"id "+obj.getString("id_room_info"));
//                        Log.d(TAG,"uid "+obj.getString("userid"));
//                        Log.d(TAG,"roomName "+obj.getString("roomName"));
//                        Log.d(TAG,"type "+obj.getString("type"));
//                        Log.d(TAG,"creator "+obj.getInt("creator")+"");
//                        Log.d(TAG,"created dt "+obj.getString("created_date"));
//                        Log.d(TAG,"member "+obj.getString("memberInThatRoom"));

                        dataStr[0] = obj.getString("id_room_info");
                        dataStr[1] = obj.getString("userid");
                        dataStr[2] = obj.getString("roomName");
                        dataStr[3] = obj.getString("type");
                        dataStr[4] = obj.getInt("creator")+"";
                        dataStr[5] = obj.getString("created_date");
                        dataStr[6] = obj.getString("memberInThatRoom");

                        listDataRoom.add(new ChatData.roomInfo(dataStr[0],dataStr[1],dataStr[2],dataStr[3],dataStr[4],dataStr[5],dataStr[6]));
                        // lalu set di class
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG,"err load room");
            }

            try {
                String result = data.getString("dataPendingMsg");
                result = result.replace("\\","");
                Log.d(TAG,result);
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        String[] dataStr = new String[obj.length()];

//                        Log.d(TAG,"id "+obj.getInt("id")+"");
//                        Log.d(TAG,"msg type "+obj.getInt("message_type")+"");
//                        Log.d(TAG,"msg data "+obj.getString("message_data"));
//                        Log.d(TAG,"msg data type "+obj.getInt("message_data_type")+"");
//                        Log.d(TAG,"id room info "+obj.getInt("id_room_info")+"");
//                        Log.d(TAG,"status "+obj.getInt("status")+"");
//                        Log.d(TAG,"from "+obj.getInt("from_id")+"");
//                        Log.d(TAG,"nama from "+obj.getString("nama"));
//                        Log.d(TAG,"dtime "+obj.getString("sendTime"));

                        dataStr[0] = obj.getInt("id")+"";
                        dataStr[1] = obj.getInt("message_type")+"";
                        dataStr[2] = obj.getString("message_data");
                        dataStr[3] = obj.getInt("message_data_type")+"";
                        dataStr[4] = obj.getInt("id_room_info")+"";
                        dataStr[5] = obj.getInt("status")+"";
                        dataStr[6] = obj.getInt("from_id")+"";
                        dataStr[7] = obj.getString("from_nama");
                        dataStr[8] = obj.getString("sendTime");

                        // lalu set di class
                        listDataPendingChat.add(new ChatMsgContainer(dataStr[0],dataStr[1],dataStr[2],dataStr[3],
                                dataStr[4],dataStr[5],dataStr[6],dataStr[7],dataStr[8]));
                    }
                }
                else
                {
                    Log.d(TAG,"no pending msg");
                }
            } catch (JSONException e) {
                Log.d(TAG,"err load pending msg");
            }

            //create dan update room tanpa message
            if(listDataRoom.size() > 0)
            {
                Log.d(qwe,"listDataRoom.size() "+listDataRoom.size());
                if(listChatData.size() > 0)
                {
                    //replace data lama
                    for(int i=0;i<listDataRoom.size();i++) {
                        boolean flag = false;
                        for(ChatData.roomInfo temp : listDataRoom) {

                            if (listChatData.get(i).getMroomInfo().getIdRoom().equals(temp.getIdRoom())) {
                                flag = true;
                                listChatData.get(i).replaceRoomInfo(temp);
                                break; // untuk percepat looping aja
                            }
                        }
                        if(!flag)
                        {
                            // karena tidak ketemu id yang sama, berarti data baru
                            listChatData.add(new ChatData(listDataRoom.get(i)));
                        }
                    }
                }
                else
                {
                    for(ChatData.roomInfo temp : listDataRoom) {
                        listChatData.add(new ChatData(temp));
                    }
                }
            }



            // ## old code sebelum save chat
            // kepake buat update data
            Log.d(qwe,"pending chat.size() "+listDataPendingChat.size());
            if(listDataPendingChat.size() > 0) {
                if (listChatData.size() > 0) {
                    // replace - karena sdh ada data
                    Log.d(qwe,"replace msg");
                    for (int i = 0; i < listChatData.size(); i++) {
                        for (ChatData.roomInfo temp : listDataRoom) {
                            if (listChatData.get(i).getMroomInfo().getIdRoom().equals(temp.getIdRoom())) {
                                listChatData.get(i).replaceAllData(temp, listDataPendingChat);
                                break; // percepat looping aja
                            }
                            //klo room id dr
                        }
                    }
                } else {
                    Log.d(qwe,"add msg karna kosong");
                    // langusng add - karena data msh kosong
                    for (ChatData.roomInfo temp : listDataRoom) {
                        listChatData.add(new ChatData(temp, listDataPendingChat));
                    }
                   // Log.d("msglala", listChatData.size() + "");
                }
            }

            Log.d(qwe,"save di loadall");
            saveChatData(listChatData);

            if(listDataPendingChat.size() > 0)
            {
                for(ChatMsgContainer newMsg : listDataPendingChat)
                {
                    if(!newMsg.getFrom_id().equals(LibInspira.getShared(global.userpreferences, global.user.nomor, "")))
                    {
                        String status = "";
                        if (newMsg.getStatus().equals(ChatMsgContainer.statusSend)) {
                            status = ChatMsgContainer.statusDelivered;
                        } else {
                            status = newMsg.getStatus();
                        }
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", newMsg.getId());
                            jsonObject.put("id_room_info", newMsg.getIdRoom());
                            jsonObject.put("from_id", newMsg.getFrom_id());
                            jsonObject.put("from_nama", newMsg.getFrom_nama());
                            jsonObject.put("message_type", newMsg.getType());
                            jsonObject.put("message_data_type", newMsg.getMsgType());
                            jsonObject.put("status", status);
                            jsonObject.put("message_data", newMsg.getMessage());
                            jsonObject.put("sendTime", newMsg.getSendTime());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mSocket.emit("new message", jsonObject.toString());
                    }
                }
            }



//            //##SAVECHAT
//            List<ChatData.roomInfo> listOldDataRoom = new ArrayList<>();
//            List<ChatMsgContainer> listOldDataPendingChat = new ArrayList<>();
//            //save dta room
//            if(LibInspira.getShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_room,"").equals(""))
//            {
////                String tempData = mGson.toJson(listDataRoom);
////                LibInspira.setShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_room,tempData);
//                listOldDataRoom = listDataRoom;
//            }
//            else
//            {
//                //load data lama
//                String tempData = LibInspira.getShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_room,"");
//                Type listType = new TypeToken<ArrayList<ChatData.roomInfo>>(){}.getType();
//                listOldDataRoom = new Gson().fromJson(tempData, listType);
//
//                //bandingkan data lama dan baru
//                for(ChatData.roomInfo temp : listDataRoom)
//                {
//                    int flag = 0;
//                    for(int i=0;i<listOldDataRoom.size();i++)
//                    {
//                        if(listOldDataRoom.get(i).getIdRoom().equals(temp.getIdRoom()))
//                        {
//                            flag = 1;
//                            break;
//                        }
//                    }
//
//                    if(flag == 0)
//                    {
//                        listOldDataRoom.add(temp);
//                    }
//                }
//            }
//            //save data chat
//            if(LibInspira.getShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_chat,"").equals(""))
//            {
//                for(int i=0;i< listDataPendingChat.size();i++)
//                {
//                    listDataPendingChat.get(i).setStatus(ChatMsgContainer.statusDelivered);
//                }
////                String tempData = mGson.toJson(listDataPendingChat);
////                LibInspira.setShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_chat,tempData);
//                listOldDataPendingChat = listDataPendingChat;
//            }
//            else
//            {
//                //load data lama
//                String tempData = LibInspira.getShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_chat,"");
//                Type listType = new TypeToken<ArrayList<ChatMsgContainer>>(){}.getType();
//                listOldDataPendingChat = new Gson().fromJson(tempData, listType);
//
//                //bandingkan data lama dan baru
//                //atau klau msg lansung add aja gpp karena sdh di seleksi di query
//                for(ChatMsgContainer temp : listDataPendingChat)
//                {
//                    int flag = 0;
//                    for(int i=0;i<listOldDataPendingChat.size();i++)
//                    {
//                        if(listOldDataPendingChat.get(i).getId().equals(temp.getId()))
//                        {
//                            flag = 1;
//                            break;
//                        }
//                    }
//
//                    if(flag == 0)
//                    {
//                        temp.setStatus(ChatMsgContainer.statusDelivered);
//                        listOldDataPendingChat.add(temp);
//                    }
//                }
//            }
//
//            String tempData = mGson.toJson(listOldDataPendingChat);
//            LibInspira.setShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_chat,tempData);
//
//            String tempData2 = mGson.toJson(listOldDataRoom);
//            LibInspira.setShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_room,tempData2);
//
//            listChatData.clear();
//            for(ChatData.roomInfo temp : listOldDataRoom) {
//                listChatData.add(new ChatData(temp,listOldDataPendingChat));
//            }

        }
    };


    private Boolean isConnected = true;
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            IndexInternal.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
//                        if(null!=mUsername)
//                            mSocket.emit("add user", mUsername);
                        //mSocket.emit("room", mRoom);
                        //LibInspira.ShowShortToast(con,"connected");
                        isConnected = true;
                        Log.d(TAG,"emit onConnect");
                        // get room pakai id_user
                        mSocket.emit("loadAllRoom",LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            IndexInternal.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    //LibInspira.ShowShortToast(con,"disconnect");
                    Log.d(TAG,"emit disconnect");
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            IndexInternal.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.e(TAG, "Error connecting");
                    //LibInspira.ShowShortToast(getApplicationContext(),"ERR on connect");
                    Log.d(TAG,"emit con err "+args[0].toString());
                }
            });
        }
    };


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            IndexInternal.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onnewmsg","indexmsg");
                    JSONObject obj = (JSONObject) args[0];
                    Log.d(TAG,"new msg : "+obj.toString());
                    String[] dataStr = new String[obj.length()];
//                    String username;
//                    String message;
                    try {
                        dataStr[0] = obj.getString("id");
                        dataStr[1] = obj.getInt("message_type")+"";
                        dataStr[2] = obj.getString("message_data");
                        dataStr[3] = obj.getInt("message_data_type")+"";
                        dataStr[4] = obj.getInt("id_room_info")+"";
                        dataStr[5] = obj.getInt("status")+"";
                        dataStr[6] = obj.getInt("from_id")+"";
                        dataStr[7] = obj.getString("from_nama");
                        dataStr[8] = obj.getString("sendTime");

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    String prevId = "";
                    if(dataStr[0].contains("~")) {
                        String[] id_piece = dataStr[0].trim().split("\\~");
                        if (id_piece.length > 0) {
                            dataStr[0] = id_piece[0];
                            prevId = id_piece[1];
                        }
                    }

                    ChatMsgContainer newMsg = new ChatMsgContainer(
                            dataStr[0],dataStr[1],dataStr[2],dataStr[3],
                            dataStr[4],dataStr[5],dataStr[6],dataStr[7],
                            dataStr[8]
                    );

                    //addMessage(newMsg);
                    // cek misal from dr diri sendiri ga ush di update
                    if(newMsg.getFrom_id().equals(LibInspira.getShared(global.userpreferences, global.user.nomor, "")))
                    {
                        replaceMessage(newMsg,prevId);
//                        if(newMsg.getStatus().equals(ChatMsgContainer.statusSend)) {
//                            //status send dan from yourself
//                            // tinggal replace message
//                            replaceMessage(newMsg,prevId);
//                        }
//                        else
//                        {
//                            replaceMessage(newMsg,prevId);
//                        }
                    }
                    else if(!newMsg.getFrom_id().equals(LibInspira.getShared(global.userpreferences, global.user.nomor, "")))
                    {
                        if(newMsg.getStatus().equals(ChatMsgContainer.statusSend)) {
                            //updatenya ketika terima ack dr server
                            //newMsg.setStatus(ChatMsgContainer.statusDelivered);
                            //replaceMessage(newMsg,prevId);

                            //send dan from other
                            //update jadi deliver
                            replaceMessage(newMsg,prevId);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("id", newMsg.getId());
                                jsonObject.put("id_room_info", newMsg.getIdRoom());
                                jsonObject.put("from_id", newMsg.getFrom_id());
                                jsonObject.put("from_nama", newMsg.getFrom_nama());
                                jsonObject.put("message_type", newMsg.getType());
                                jsonObject.put("message_data_type", newMsg.getMsgType());
                                jsonObject.put("status", ChatMsgContainer.statusDelivered);
                                jsonObject.put("message_data", newMsg.getMessage());
                                jsonObject.put("sendTime", newMsg.getSendTime());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mSocket.emit("new message", jsonObject.toString());
                        }
                        else if(newMsg.getStatus().equals(ChatMsgContainer.statusDelivered))
                        {
                            // sdh deliv tapi blm diread
                            replaceMessage(newMsg,prevId);
                            // jika fragment chat di buka asumsi langusng read
                            Log.d("logasd",LibInspira.getShared(GlobalVar.chatPreferences, GlobalVar.chat.chat_menu_position, ""));
                            if(LibInspira.getShared(GlobalVar.chatPreferences, GlobalVar.chat.chat_menu_position, "").equals("chatFrag"))
                            {
                                int tempPost = 0;
                                boolean flagRoom = false;
                                for(int i=0;i<listChatData.size();i++)
                                {
                                    if(listChatData.get(i).getMroomInfo().getIdRoom().equals(newMsg.getIdRoom()))
                                    {
                                        tempPost = i;
                                        flagRoom = true;
                                        break;
                                    }
                                }

                                if(flagRoom) {
                                    updateStatusToRead(listChatData.get(tempPost));
                                }
                            }
                        }
                        else if(newMsg.getStatus().equals(ChatMsgContainer.statusRead))
                        {
                            replaceMessage(newMsg,prevId);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("id", newMsg.getId());
                                jsonObject.put("id_room_info", newMsg.getIdRoom());
                                jsonObject.put("from_id", newMsg.getFrom_id());
                                jsonObject.put("from_nama", newMsg.getFrom_nama());
                                jsonObject.put("message_type", newMsg.getType());
                                jsonObject.put("message_data_type", newMsg.getMsgType());
                                jsonObject.put("status", ChatMsgContainer.statusReadDelivered);
                                jsonObject.put("message_data", newMsg.getMessage());
                                jsonObject.put("sendTime", newMsg.getSendTime());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mSocket.emit("new message", jsonObject.toString());
                        }
                        else
                        {
                            replaceMessage(newMsg,prevId);
                        }

                    }

                }
            });
        }
    };

    private Emitter.Listener onACK = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            Log.d(TAG,"onACK");
        }
    };


    public static void replaceMessage(ChatMsgContainer newMsgData, String prevId)
    {
        // yg masuk sini sdh data bersih sdh di pisah id nya antara id db dengan id generate android
        // search di list berdasar prev id
        Log.d(TAG, "normal id "+newMsgData.getId() +" previd "+ prevId);
        int tempPost = 0;
        boolean flagRoom = false;
        for(int i=0;i<listChatData.size();i++)
        {
            if(listChatData.get(i).getMroomInfo().getIdRoom().equals(newMsgData.getIdRoom()))
            {
                tempPost = i;
                flagRoom = true;
                break;
            }
        }

        if(flagRoom) {
            int flag = 0;
            for (int i = 0; i < listChatData.get(tempPost).getChatMsgData().size(); i++) {
                if (!prevId.equals("")) {
                    if (listChatData.get(tempPost).getChatMsgData().get(i).getId().equals(prevId)) {

//                        if(listChatData.get(tempPost).getChatMsgData().get(i).getMsgType().equals(ChatMsgContainer.message_data_type_picture)
//                                && ChatMsgContainer.isYou(listChatData.get(tempPost).getChatMsgData().get(i)))
//                        {
//                            listChatData.get(tempPost).getChatMsgData().get(i).copy(newMsgData,ChatMsgContainer.message_data_type_picture);
//                        }
//                        else
//                        {
//                            listChatData.get(tempPost).getChatMsgData().get(i).copy(newMsgData);
//                        }
                        listChatData.get(tempPost).getChatMsgData().get(i).copy(newMsgData);
                        flag = 1;
                        Log.d(TAG, "by previd replace id " + newMsgData.getId());
                        break;
                    }
                } else {
                    if (listChatData.get(tempPost).getChatMsgData().get(i).getId().equals(newMsgData.getId())) {

//                        if(listChatData.get(tempPost).getChatMsgData().get(i).getMsgType().equals(ChatMsgContainer.message_data_type_picture)
//                                && ChatMsgContainer.isYou(listChatData.get(tempPost).getChatMsgData().get(i)))
//                        {
//                            listChatData.get(tempPost).getChatMsgData().get(i).copy(newMsgData,ChatMsgContainer.message_data_type_picture);
//                        }
//                        else
//                        {
//                            listChatData.get(tempPost).getChatMsgData().get(i).copy(newMsgData);
//                        }
                        listChatData.get(tempPost).getChatMsgData().get(i).copy(newMsgData);
                        flag = 1;
                        Log.d(TAG, "by id replace id " + newMsgData.getId());
                        break;
                    }
                }
            }
            if (flag == 0) {
                listChatData.get(tempPost).getChatMsgData().add(newMsgData);
                Log.d(TAG, "add id " + newMsgData.getId());
            }
        }
        //Log.d("msglala","4 size "+listChatData.get(tempPost).getChatMsgData().size()+"");
        //chatFrag.setAdapter(listChatData.get(tempPost));
        saveChatData(listChatData);
    }

    public static void updateStatusToRead(ChatData _mChatData)
    {
        // saran : mungkin nanti biar lbh efisien di search dari bawah ke atas
        // krna asumsi yang atas2 sdh di read dari pada loop lg
        if(_mChatData != null) {
            for (ChatMsgContainer newMsg : _mChatData.getChatMsgData()) {
                if (newMsg.getType().equals(ChatMsgContainer.typeMSG)
                        && newMsg.getStatus().equals(ChatMsgContainer.statusDelivered)
                        && !newMsg.getFrom_id().equals(LibInspira.getShared(global.userpreferences, global.user.nomor, ""))) {
                    // klo other, ketika chat fragment di buka, update jd read, di asumsikan user sdh baca messagenya
                    JSONObject jsonObject = new JSONObject();
                    String status = ChatMsgContainer.statusRead;
                    try {
                        jsonObject.put("id", newMsg.getId());
                        jsonObject.put("id_room_info", newMsg.getIdRoom());
                        jsonObject.put("from_id", newMsg.getFrom_id());
                        jsonObject.put("from_nama", newMsg.getFrom_nama());
                        jsonObject.put("message_type", newMsg.getType());
                        jsonObject.put("message_data_type", newMsg.getMsgType());
                        jsonObject.put("status", status);
                        jsonObject.put("message_data", newMsg.getMessage());
                        jsonObject.put("sendTime", newMsg.getSendTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("new message", jsonObject.toString());
                    //updateStatusOnServer(temp.getId(),ChatMsgContainer.statusRead);
                }
            }
        }
    }

    private void loadOldDataChat()
    {
//        //##SAVECHAT
//        List<ChatData.roomInfo> listOldDataRoom = new ArrayList<>();
//        List<ChatMsgContainer> listOldDataPendingChat = new ArrayList<>();
//
//        String tempData1 = LibInspira.getShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_room,"");
//        Type listType1 = new TypeToken<ArrayList<ChatData.roomInfo>>(){}.getType();
//        listOldDataRoom = new Gson().fromJson(tempData1, listType1);
//
//        String tempData2 = LibInspira.getShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_chat,"");
//        Type listType2 = new TypeToken<ArrayList<ChatMsgContainer>>(){}.getType();
//        listOldDataPendingChat = new Gson().fromJson(tempData2, listType2);
//
//        if(listOldDataRoom != null && listOldDataPendingChat!=null
//                && listOldDataRoom.size() > 0 && listOldDataPendingChat.size() > 0) {
//            for (ChatData.roomInfo temp : listOldDataRoom) {
//                listChatData.add(new ChatData(temp, listOldDataPendingChat));
//            }
//            //return true;
//        }
//        else
//        {
//            Log.d(TAG,"data size null/0");
//            //return false;
//        }

        List<ChatData> oldData = new ArrayList<>();
        String tempData = LibInspira.getShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_all,"");
        Type listType = new TypeToken<ArrayList<ChatData>>(){}.getType();
        oldData= new Gson().fromJson(tempData, listType);

        if(oldData != null && oldData.size() > 0)
        {
            Log.d(qwe,"ada data");
            listChatData.clear();
            listChatData.addAll(oldData);
        }
        else
        {
            Log.d(qwe,"no data");
        }

    }

    public static void saveChatData(List<ChatData> newData)
    {
        if(newData.size() > 0) {
            String data = new Gson().toJson(newData);
            LibInspira.setShared(GlobalVar.chatPreferences, GlobalVar.chat.chat_history_all, data);
            Log.d("save", "save data");
            //Log.d("picass","save data");
        }
    }

//    public void saveDataChatAll(List<ChatData.roomInfo> room,List<ChatMsgContainer> chat)
//    {
//        saveDataChat(chat);
//        saveDataRoom(room);
//    }
//
//    public void saveDataRoom(List<ChatData.roomInfo> room)
//    {
//        String dataRoom = new Gson().toJson(room);
//        LibInspira.setShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_chat,dataRoom);
//    }
//
//    public void saveDataChat(List<ChatMsgContainer> chat)
//    {
//        String dataChat = new Gson().toJson(chat);
//        LibInspira.setShared(GlobalVar.chatPreferences,GlobalVar.chat.chat_history_chat,dataChat);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshUserData();
        LibInspira.setShared(GlobalVar.chatPreferences, GlobalVar.chat.chat_menu_position, "indexInternal");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("appStart", onAppStart);
        mSocket.off("loadAllRoom", loadAllRoom);
        mSocket.off("new message", onNewMessage);
        mSocket.off("ack",onACK);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        //chatFrag.callDisconnect();
    }

    public static void RefreshUserData(){
        View navigationHeader = navigationView.getHeaderView(0);
        tvUsername = (TextView) navigationHeader.findViewById(R.id.tvUsername);
        tvUsername.setText(LibInspira.getShared(global.userpreferences, global.user.nama, "User").toUpperCase());
        tvSales = (TextView) navigationHeader.findViewById(R.id.tvSales);
        tvTarget = (TextView) navigationHeader.findViewById(R.id.tvTarget);
        //modified by Tonny @03-Aug-2017 function untuk get omzet dan target dijadikan satu
        String actionUrl = "Sales/getOmzetTarget/";
        new checkOmzetTarget().execute( actionUrl );
        tvSales = (TextView) navigationHeader.findViewById(R.id.tvSales);
        tvSales.setText("Omzet: " + LibInspira.delimeter(LibInspira.getShared(global.salespreferences, global.sales.omzet, "0"), true));
        tvTarget = (TextView) navigationHeader.findViewById(R.id.tvTarget);
        tvTarget.setText("Target: " + LibInspira.delimeter(LibInspira.getShared(global.salespreferences, global.sales.target, "0"), true));
    }

    /******************************************************************************
        Class     : checkOmzetTarget
        Author    : Tonny
        Date      : 01-Aug-2017
        Function  : Untuk mendapatkan omzet dan target dari sales berdasarkan kode/nomor sales
    ******************************************************************************/
    private static class checkOmzetTarget extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                Log.d("kodesales: ", LibInspira.getShared(global.userpreferences, global.user.nomor_sales, ""));
                jsonObject.put("kodesales", LibInspira.getShared(global.userpreferences, global.user.nomor_sales, ""));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return LibInspira.executePost(context, urls[0], jsonObject);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Log.d("omzettarget", result);  //remarked by Tonny @04-Aug-2017
            try {
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            String success = obj.getString("success");
                            if (success.equals("true")) {
                                LibInspira.setShared(global.salespreferences, global.sales.omzet, obj.getString("omzet"));
                                LibInspira.setShared(global.salespreferences, global.sales.target, obj.getString("target"));
                                tvSales.setText("Omzet: " + LibInspira.delimeter(LibInspira.getShared(global.salespreferences, global.sales.omzet, "0"), true));
                                tvTarget.setText("Target: " + LibInspira.delimeter(LibInspira.getShared(global.salespreferences, global.sales.target, "0"), true));
                            }
                        }else{
                            LibInspira.setShared(global.salespreferences, global.sales.omzet, "0");
                            LibInspira.setShared(global.salespreferences, global.sales.target, "0");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_index_internal_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {  //added by Tonny @30-Jul-2017
            // Handle the camera action
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new SettingFragment());  //added by Tonny @04-Aug-2017
        } else if (id == R.id.action_changepassword) {  //added by Tonny @30-Jul-2017
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ChangePasswordFragment());
        } else if (id == R.id.action_logout) {
            global.clearDataUser();

            Intent intent = new Intent(IndexInternal.this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        LibInspira.clearShared(global.temppreferences);

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new DashboardInternalFragment());  //added by Tonny @01-Aug-2017
        } else if (id == R.id.nav_contact) {
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ContactFragment());  //added by Tonny @01-Aug-2017
        } else if (id == R.id.nav_target) {
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ChoosePeriodeFragment());  //added by Tonny @04-Aug-2017
        } else if (id == R.id.nav_group) {
            LibInspira.setShared(global.sharedpreferences, global.shared.position, "Conversation");
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ChooseGroupFragment());
        } else if (id == R.id.nav_salesorder) {
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new PenjualanFragment()); //added by ADI @24-Aug-2017
        } else if (id == R.id.nav_stockreport) {
            if(LibInspira.getShared(global.userpreferences, global.user.role_crossbranch, "").equals("1")){
                LibInspira.setShared(global.sharedpreferences, global.shared.position, "stockmonitoring");
                LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ChooseCabangFragment());
            }else{
                LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new StockMonitoringFragment());  //modified by Tonny @17-Aug-2017
            }
        } else if (id == R.id.nav_salestracking){
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new SalesNavigationFragment());  //added by Tonny @23-Aug-2017
        } else if (id == R.id.nav_omzet){
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new FilterSalesOmzetFragment());  //added by Tonny @25-Aug-2017
//        } else if (id == R.id.nav_customer_prospecting){
//            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ChooseCustomerProspectingFragment());  //added by Tonny @29-Aug-2017
        } else if (id == R.id.nav_salesorder){
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new PenjualanFragment());  //added by Tonny @01-Sep-2017
        }
        else if(id == R.id.nav_pricelist)
        {
            LibInspira.setShared(global.sharedpreferences, global.shared.position, "pricelist");
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ChooseJenisFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
