package com.example.chatroom;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
//    private String ip;
//    private String port;
    private Socket socket=MyApplication.socket;
    //private StringBuffer buffer=new StringBuffer();
    private InputStream receiveInput;
    private OutputStream sendOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMsg();
        getWindow().setBackgroundDrawableResource(R.drawable.bg3);
        inputText=findViewById(R.id.input);
        send=findViewById(R.id.send);
        msgRecyclerView=findViewById(R.id.msg_recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        Intent intent=getIntent();
        name=intent.getStringExtra("name");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    receiveInput=MyApplication.inputStream;
                    sendOutput=MyApplication.outputStream;
                }catch (Exception e){
                    e.printStackTrace();
                }
                receiveMsg();
            }
        }).start();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String content1 = inputText.getText().toString();
                    if (!"".equals(content1)) {
                        //String date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
//                        buffer.append(content1).append("\n\n").append("来自:").append(name).append(date);
//
                        content1+="\n";
                        content1=content1+"来自:"+name;
//                        content1 = buffer.toString();
                        final String str=content1;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                               try {
                                   sendOutput.write(str.getBytes());
                               }catch (Exception e){
                                   e.printStackTrace();
                               }
                            }
                        }).start();
//                        buffer.delete(0, buffer.length());
                        final Msg msg = new Msg(content1, Msg.TYPE_SEND);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                msgList.add(msg);
                                adapter.notifyItemInserted(msgList.size() - 1);
                                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                            }
                        });
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
    //接受服务器传来的信息
    public void receiveMsg() {
            byte[] bytes = new byte[1024];
            int len;
            String recevieMsg;
            String sign;
            boolean istrue=true;
            while (istrue) {
                try {
                    while ((len = receiveInput.read(bytes)) != -1) {
                        recevieMsg = new String(bytes, 0, len);
//                        sign=recevieMsg.substring(0,5);
//                        if (sign.equals("#chat")){
//                            Intent intent=new Intent(this,Chat.class);
//                            startActivity(intent);
//                            istrue=false;
//                            break;
//                        }
                        final Msg msg = new Msg(recevieMsg, Msg.TYPE_RECEIVED);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                msgList.add(msg);
                                adapter.notifyItemInserted(msgList.size() - 1);
                                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


    }
}
