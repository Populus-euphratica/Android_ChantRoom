package com.example.chatroom;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MyApplication extends Application {
    private static Context context;
    public static Socket socket=null;
    public static InputStream inputStream;
    public static OutputStream outputStream;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }


    public static void setSocket(String ip,int port) {
        try {
            MyApplication.socket = new Socket(ip,port);
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Context getContext(){
        return context;
    }


}
