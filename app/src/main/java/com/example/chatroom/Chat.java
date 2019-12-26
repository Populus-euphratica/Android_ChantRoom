package com.example.chatroom;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

public class Chat extends AppCompatActivity {

    private List<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private String name;
    private Socket socket=MyApplication.socket;
    //private StringBuffer buffer=new StringBuffer();
    private InputStream receiveInput;
    private OutputStream sendOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setBackgroundDrawableResource(R.drawable.bg3);
        inputText=findViewById(R.id.input_Chat);
        send=findViewById(R.id.send_Chat);
        msgRecyclerView=findViewById(R.id.msg_recyclerView_Chat);
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
                    receiveInput=socket.getInputStream();
                    sendOutput=socket.getOutputStream();
                }catch (Exception e){
                    e.printStackTrace();
                }
                receiveMsg();
            }
        }).start();




        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMes(inputText.getText().toString());
            }
        });
    }


    public void receiveMsg() {


        byte[] bytes = new byte[1024 * 3];
        int len;
        String recevieMsg;

        while (true) {
            try {
                while ((len = receiveInput.read(bytes)) != -1) {
                    recevieMsg = new String(bytes, 0, len);
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
    public void sendMes(String content){
        try {
            String content1 = content;
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
}
