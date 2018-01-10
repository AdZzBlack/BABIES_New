package com.inspira.babies;

import android.app.Application;
import android.util.Log;

import com.inspira.babies.GlobalVar;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class ChatApplication extends Application {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(GlobalVar.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            Log.d("indexInternal","chat app : "+e);
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
