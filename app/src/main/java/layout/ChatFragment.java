package layout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.inspira.babies.GlobalVar;
import com.inspira.babies.IndexInternal;
import com.inspira.babies.LibInspira;
import com.inspira.babies.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.inspira.babies.IndexInternal.global;
import static com.inspira.babies.IndexInternal.listChatData;
import static com.inspira.babies.IndexInternal.mSocket;

/**
 * Created by Arta on 01-Dec-17.
 */

public class ChatFragment extends Fragment implements View.OnClickListener {
    public ChatFragment()
    {

    }

    ChatItemAdapter mitemListAdapter;
    public void setup(Context con)
    {
        mitemListAdapter = new ChatItemAdapter(con);
    }

    ChatData mChatData;
    public void setAdapter(ChatData data)
    {
        mChatData = data;
        Log.d("msglala","size "+mChatData.getChatMsgData().size()+"");
        mitemListAdapter.reset(mChatData.getChatMsgData());
    }
    String chatRoomName="", mToUserId ="";;
    public void setChatName(String name)
    {
        chatRoomName = name;
        mToUserId = LibInspira.getShared(global.chatPreferences, global.chat.chat_to_id, "");
    }

    private String TAG = "chatFrag";
    //private Socket mSocket;
    private Boolean isConnected = true;
    private String mUsername = "";
    private boolean mTyping = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log("on create");
        setHasOptionsMenu(true);

        LibInspira.setShared(GlobalVar.chatPreferences, GlobalVar.chat.chat_menu_position, "chatFrag");

//        URI coba;
//        try
//        {   coba = new URI(GlobalVar.CHAT_SERVER_URL);
//            Manager manager = new Manager(coba);
//            Socket socket = manager.socket("/lala");
//            socket.connect();
//        }catch (Exception e)
//        {
//        }



        mUserid = LibInspira.getShared(global.userpreferences, global.user.nomor, "");
        mUsername = LibInspira.getShared(global.userpreferences, global.user.nama, "");


//        ChatApplication app = (ChatApplication) getActivity().getApplication();
//        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.on("login", onLogin);



        //mSocket.connect();

        //mSocket.emit("add user", mUsername);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
        mSocket.off("login", onLogin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_main_layout, container, false);
        getActivity().setTitle(chatRoomName);
        log("create view");
        return v;
    }

    EditText mInputMessageView;
    private Handler mTypingHandler = new Handler();
    private static final int TYPING_TIMER_LENGTH = 600;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log("view create");
        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;

                    JSONObject jsonObject;
                    jsonObject = new JSONObject();
                    //---------------------------------------------HEADER-----------------------------------------------------//
                    try {
                        //kirim data diri sendiri
                        jsonObject.put("uname",mUsername);
                        jsonObject.put("id_room_info",mChatData.getMroomInfo().getIdRoom());
                        jsonObject.put("id",mUserid);
                        mSocket.emit("typing",jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });


    }

    private Context con;
    @Override
    public void onAttach(Context context)
    {
        con = context;
        super.onAttach(context);
        log("attach");
    }


    ListView lvChatMsgList;
    TextView tvUserAction;
    //List<ChatMsgContainer> dataMsg = new ArrayList<>();
    String mRoom;
    String mUserid;
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        log("act create");

        lvChatMsgList = (ListView) getView().findViewById(R.id.lvMsgList);
        lvChatMsgList.setAdapter(mitemListAdapter);

        tvUserAction = (TextView) getView().findViewById(R.id.tvUserAction);

        IndexInternal.updateStatusToRead(mChatData); //  asumsi user buka chat berarti sdh baca

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.ibtnSearch)
        {
            search();
        }
    }

    private void search()
    {
//        itemadapter.clear();
//        for(int ctr=0;ctr<list.size();ctr++)
//        {
//            if(etSearch.getText().equals(""))
//            {
//                itemadapter.add(list.get(ctr));
//                itemadapter.notifyDataSetChanged();
//            }
//            else
//            {
//                if(LibInspira.contains(list.get(ctr).getNama(),etSearch.getText().toString() ))
//                {
//                    itemadapter.add(list.get(ctr));
//                    itemadapter.notifyDataSetChanged();
//                }
//            }
//        }
    }

    private void refreshList()
    {
//        itemadapter.clear();
//        list.clear();
//
//        String data = LibInspira.getShared(global.datapreferences, global.data.bentuk, "");
//        String[] pieces = data.trim().split("\\|");
//
//        if(pieces.length==1)
//        {
//            tvNoData.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            tvNoData.setVisibility(View.GONE);
//            for(int i=0 ; i < pieces.length ; i++){
//                Log.d("item", pieces[i] + "a");
//                if(!pieces[i].equals(""))
//                {
//                    String[] parts = pieces[i].trim().split("\\~");
//
//                    String nomor = parts[0];
//                    String nama = parts[1];
//                    String kode = parts[2];
//
//
//                    if(nomor.equals("null")) nomor = "";
//                    if(nama.equals("null")) nama = "";
//                    if(kode.equals("null")) kode = "";
//
//                    ChooseBentukFragment.ItemAdapter dataItem = new ItemAdapter();
//                    dataItem.setNomor(nomor);
//                    dataItem.setNama(nama);
//                    dataItem.setKodeNomor(kode);
//                    list.add(dataItem);
//
//                    itemadapter.add(dataItem);
//                    itemadapter.notifyDataSetChanged();
//                }
//            }
//        }
    }

//    private class getData extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            jsonObject = new JSONObject();
//            return LibInspira.executePost(getContext(), urls[0], jsonObject);
//        }
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//            Log.d("resultQuery", result);
//            try
//            {
//                String tempData= "";
//                JSONArray jsonarray = new JSONArray(result);
//                if(jsonarray.length() > 0){
//                    for (int i = 0; i < jsonarray.length(); i++) {
//                        JSONObject obj = jsonarray.getJSONObject(i);
//                        if(!obj.has("query")){
//                            String nomor = (obj.getString("nomor"));
//                            String nama = (obj.getString("nama"));
//                            String kode = (obj.getString("kode"));
//
//                            if(nomor.equals("")) nomor = "null";
//                            if(nama.equals("")) nama = "null";
//                            if(kode.equals("")) kode = "null";
//
//                            tempData = tempData + nomor + "~" + nama + "~" + kode + "|";
//                        }
//                    }
//                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.bentuk, "")))
//                    {
//                        LibInspira.setShared(
//                                global.datapreferences,
//                                global.data.bentuk,
//                                tempData
//                        );
//                        refreshList();
//                    }
//                }
//                tvInformation.animate().translationYBy(-80);
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//                tvInformation.animate().translationYBy(-80);
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            tvInformation.setVisibility(View.VISIBLE);
//        }
//    }

    private void hideTyping()
    {
        tvUserAction.setVisibility(View.GONE);
    }
    private void showTyping(String uname)
    {
        log("typing");
        tvUserAction.setVisibility(View.VISIBLE);
        tvUserAction.setText(uname+" is Typing");
    }

    private void attemptSend() {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        mTyping = false;

        log("attemp send");

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");

//        this.id = id;
//        this.idRoom = idroom;
//        this.from_id = from_id;
//        this.from_nama = nama;
//        this.type = type;
//        this.msgType = msgType;
//        this.status = status;
//        this.message = message;
//        this.sendTime = sendTime;
        mUserid = LibInspira.getShared(global.userpreferences, global.user.nomor, "");
        mUsername = LibInspira.getShared(global.userpreferences, global.user.nama, "");

        String[] newData = new String[9];
        newData[0] = UUID.randomUUID().toString();
        newData[1] = ChatMsgContainer.typeMSG;
        newData[2] = message;
        newData[3] = ChatMsgContainer.message_data_type_string;
        newData[4] = mChatData.getMroomInfo().getIdRoom();
        newData[5] = ChatMsgContainer.statusPraSend;
        newData[6] = mUserid;
        newData[7] = mUsername;
        newData[8] = "";

        UUID.randomUUID().toString(); // untuk random uuid msg
        ChatMsgContainer newMsg = new ChatMsgContainer(
            newData[0],newData[1],newData[2],newData[3],
            newData[4], newData[5],newData[6],newData[7],
            newData[8]
        );
        addMessage(newMsg);
        //Log.d("indexInternal","chat frag add msg id "+newMsg.getId());

        // perform the sending message attempt.
        JSONObject jsonObject;
        jsonObject = new JSONObject();
        //---------------------------------------------HEADER-----------------------------------------------------//
        try {
            jsonObject.put("id",newMsg.getId());
            jsonObject.put("id_room_info",newMsg.getIdRoom());
            jsonObject.put("from_id",newMsg.getFrom_id());
            jsonObject.put("from_nama",newMsg.getFrom_nama());
            jsonObject.put("message_type",newMsg.getType());
            jsonObject.put("message_data_type",newMsg.getMsgType());
            jsonObject.put("status",newMsg.getStatus());
            jsonObject.put("message_data", newMsg.getMessage());
            jsonObject.put("sendTime",newMsg.getSendTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //log(jsonObject.toString());
        mSocket.emit("new message", jsonObject.toString());
    }


    private void addMessage(ChatMsgContainer newMsgData)
    {
        int tempPost=0;
        boolean flag = false;
        for(int i=0;i<listChatData.size();i++)
        {
            if(listChatData.get(i).getMroomInfo().getIdRoom().equals(newMsgData.getIdRoom()))
            {
                tempPost = i;
                flag = true;
                break;
            }
        }

        if(flag) {
            //Log.d("msglala","2 size "+mChatData.getChatMsgData().size()+"");
            listChatData.get(tempPost).getChatMsgData().add(newMsgData);
            mChatData = listChatData.get(tempPost);
            //Log.d("msglala","3 size "+mChatData.getChatMsgData().size()+"");
            mitemListAdapter.reset(mChatData.getChatMsgData());
        }
//        mChatData = listChatData.get(tempPost);
//        dataMsg = mChatData.getChatMsgData();
        mitemListAdapter.notifyDataSetChanged();
        mitemListAdapter.notifyDataSetInvalidated();
        lvChatMsgList.invalidate();
        scrollToBottom();
    }
    private void replaceMessage(ChatMsgContainer newMsgData, String prevId)
    {
        // yg masuk sini sdh data bersih sdh di pisah id nya antara id db dengan id generate android
        // search di list berdasar prev id
        // replace semua dgn data baru
        int flag = 0;
        for(int i=0; i<mChatData.getChatMsgData().size();i++ )
        {
            if(!prevId.equals("")) {
                if (mChatData.getChatMsgData().get(i).getId().equals(prevId)) {
                    mChatData.getChatMsgData().get(i).copy(newMsgData);
                    flag = 1;
                    break;
                }
            }
            else
            {
                if (mChatData.getChatMsgData().get(i).getId().equals(newMsgData.getId())) {
                    mChatData.getChatMsgData().get(i).copy(newMsgData);
                    flag = 1;
                    break;
                }
            }
        }
        if(flag == 0)
        {
            mChatData.getChatMsgData().add(newMsgData);
        }

        //mitemListAdapter.add(mChatData.getChatMsgData());

        mitemListAdapter.notifyDataSetChanged();
        mitemListAdapter.notifyDataSetInvalidated();
        lvChatMsgList.invalidate();
        scrollToBottom();
    }

    private void scrollToBottom() {
        //lvChatMsgList.smoothScrollByOffset(mitemListAdapter.getCount() - 1);
        lvChatMsgList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lvChatMsgList.setSelection(mitemListAdapter.getCount() - 1);
            }
        });
    }

    private void scrollToPosition(final int pos) {
        //lvChatMsgList.smoothScrollByOffset(mitemListAdapter.getCount() - 1);
        lvChatMsgList.post(new Runnable() {
            @Override
            public void run() {
                lvChatMsgList.setSelection(pos);
            }
        });
    }

    private void addLog(String log)
    {
        //mitemListAdapter.add(new ChatMsgContainer("",log,ChatMsgContainer.typeLOG));
        mChatData.getChatMsgData().add(new ChatMsgContainer("",log,ChatMsgContainer.typeLOG));
        //mitemListAdapter.add(dataMsg);
        mitemListAdapter.notifyDataSetChanged();
        mitemListAdapter.notifyDataSetInvalidated();
        lvChatMsgList.invalidate();
    }

//    private Emitter.Listener onAppStart = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            JSONObject data = (JSONObject) args[0];
//
//            try {
//                log(data.getString("socketid")+"");
//            } catch (JSONException e) {
//                return;
//            }
//        }
//    };


    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            log("on login "+numUsers);
        }
    };


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(null!=mUsername)
                            mSocket.emit("add user", mUsername);
                        //mSocket.emit("room", mRoom);
                        //LibInspira.ShowShortToast(con,"connected");
                        isConnected = true;
                        log("emit onConnect");
                        //addLog("join room c : "+ mRoom);
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    //LibInspira.ShowShortToast(con,"disconnect");
                    log("emit disconnect");
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    LibInspira.ShowShortToast(con,"ERR on connect");
                    log("emit discon con err");
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("onnewmsg","chat fragment msg");
                    // repalce data di jalankan di service/ activity parentnya, supaya chat ttp masuk meskipun ga buka chat fragement nya
//                    JSONObject obj = (JSONObject) args[0];
//                    String[] dataStr = new String[obj.length()];
////                    String username;
////                    String message;
//                    try {
//                        dataStr[0] = obj.getString("id");
//                        dataStr[1] = obj.getInt("message_type")+"";
//                        dataStr[2] = obj.getString("message_data");
//                        dataStr[3] = obj.getInt("message_data_type")+"";
//                        dataStr[4] = obj.getInt("id_room_info")+"";
//                        dataStr[5] = obj.getInt("status")+"";
//                        dataStr[6] = obj.getInt("from_id")+"";
//                        dataStr[7] = obj.getString("from_nama");
//                        dataStr[8] = obj.getString("sendTime");
//
//                    } catch (JSONException e) {
//                        Log.e(TAG, e.getMessage());
//                        return;
//                    }
//                    log("new message");
//                    hideTyping();
////                    removeTyping(username);
//
//                    String prevId = "";
//                    if(dataStr[0].contains("~")) {
//                        String[] id_piece = dataStr[0].trim().split("\\~");
//                        if (id_piece.length > 0) {
//                            dataStr[0] = id_piece[0];
//                            prevId = id_piece[1];
//                        }
//                    }
//
//                    ChatMsgContainer newMsg = new ChatMsgContainer(
//                            dataStr[0],dataStr[1],dataStr[2],dataStr[3],
//                            dataStr[4],dataStr[5],dataStr[6],dataStr[7],
//                            dataStr[8]
//                    );
//
//                    //addMessage(newMsg);
//                    // cek misal from dr diri sendiri ga ush di update
//                    if(newMsg.getStatus().equals(ChatMsgContainer.statusSend) && newMsg.getFrom_id().equals(mUserid)) {
//                        //status send dan from yourself
//                        // tinggal replace message
//                        replaceMessage(newMsg,prevId);
//                    }
//                    else if(newMsg.getStatus().equals(ChatMsgContainer.statusSend) && !newMsg.getFrom_id().equals(mUserid))
//                    {
//                        newMsg.setStatus(ChatMsgContainer.statusDelivered);
//                        replaceMessage(newMsg,prevId);
//                        //send dan from other
//                        JSONObject jsonObject = new JSONObject();
//                        try {
//                            jsonObject.put("id",newMsg.getId());
//                            jsonObject.put("id_room_info",newMsg.getIdRoom());
//                            jsonObject.put("from_id",newMsg.getFrom_id());
//                            jsonObject.put("from_nama",newMsg.getFrom_nama());
//                            jsonObject.put("message_type",newMsg.getType());
//                            jsonObject.put("message_data_type",newMsg.getMsgType());
//                            jsonObject.put("status",newMsg.getStatus());
//                            jsonObject.put("message_data", newMsg.getMessage());
//                            jsonObject.put("sendTime",newMsg.getSendTime());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        mSocket.emit("new message", jsonObject.toString());
//
//                    }
//                    else
//                    {
//                        replaceMessage(newMsg,prevId);
//                    }

                    mitemListAdapter.notifyDataSetChanged();
                    mitemListAdapter.notifyDataSetInvalidated();
                    lvChatMsgList.invalidate();

                    if(mitemListAdapter.isUnreadLog())
                    {
                        Log.d(TAG,"pos item : "+mitemListAdapter.unreadLogPosition());
                        scrollToPosition(mitemListAdapter.unreadLogPosition());
                    }
                    else {
                        scrollToBottom();
                    }

                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                        mRoom = data.getString("room");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    log("emit on user join");

                    addLog("user join "+username);
//                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    log("emit user left");
                    addLog("user left "+username);
//                    addLog(getResources().getString(R.string.message_user_left, username));
//                    addParticipantsLog(numUsers);
//                    removeTyping(username);
                    hideTyping();
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username,id;
                    try {
                        username = data.getString("username");
                        id = data.getInt("id")+"";
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    log("emit on typing");
                    // cek apakah sesuai dengan orang yang sedang di chat
                    if(mToUserId.equals(id))
                    {showTyping(username);}
//                    addTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    log("emit stop typing");
                    hideTyping();
//                    removeTyping(username);
                }
            });
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };

    private void log(String log)
    {
        String pos = "pos : ";
        Log.d(TAG,pos+log);
    }
}
