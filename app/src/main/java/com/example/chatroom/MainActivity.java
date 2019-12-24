package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private String name;
    private String ip;
    private String port;
    private Socket socket=null;
    private StringBuffer buffer=new StringBuffer();
    private InputStream receiveInput;
    private OutputStream sendOutput;
    private static int UPDATE_TOAST=1;
    private static int UPDATE_MSG=0;
    public Message message;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_MSG:
                    msgList.add((Msg) msg.obj);
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    break;
                case UPDATE_TOAST:
                    Toast.makeText(MyApplication.getContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message=new Message();
        initMsg();
        inputText=(EditText)findViewById(R.id.input);
        send=(Button)findViewById(R.id.send);
        msgRecyclerView=(RecyclerView)findViewById(R.id.msg_recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        Intent intent=getIntent();
        name=intent.getStringExtra("name");
        ip=intent.getStringExtra("ip");
        port=intent.getStringExtra("port");
        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
       //StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket=new Socket(ip,Integer.parseInt(port));
                    receiveInput=socket.getInputStream();
                    sendOutput=socket.getOutputStream();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (socket==null){

                    message.obj="登入失败";
                    message.what=UPDATE_TOAST;
                    handler.sendMessage(message)
                    finish();
                }else {
                    message.obj="登入成功";
                    message.what=UPDATE_TOAST;
                    handler.sendMessage(message)
                   // Toast.makeText(MyApplication.getContext(),"登入成功",Toast.LENGTH_SHORT).show();
                }
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bytes=new byte[1024*3];
                    int len;
                    String recevieMsg;
                    while (true){
                        while ((len=receiveInput.read(bytes))!=-1){
                            recevieMsg=new String(bytes,0,len);
                            final Msg msg=new Msg(recevieMsg,Msg.TYPE_RECEIVED);

                            message.what=UPDATE_MSG;
                            message.obj=msg;
                            handler.sendMessage(message)
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    msgList.add(msg);
//                                    adapter.notifyItemInserted(msgList.size()-1);
//                                    msgRecyclerView.scrollToPosition(msgList.size()-1);
//                                }
//                            });
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String content = inputText.getText().toString();
                    if (!"".equals(content)) {
                        String date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
                        buffer.append(content).append("\n\n").append("来自:").append(name).append(date);
                        content = buffer.toString();
                        sendOutput.write(content.getBytes());
                        buffer.delete(0, buffer.length());
                        Msg msg = new Msg(content, Msg.TYPE_SEND);
                        msgList.add(msg);
                        adapter.notifyItemInserted(msgList.size() - 1);
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);
                        inputText.setText("");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void initMsg(){
        Msg msg1=new Msg("你好",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2=new Msg("你好",Msg.TYPE_SEND);
        msgList.add(msg2);
        Msg msg3=new Msg("我是曹子浩",Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }
}
