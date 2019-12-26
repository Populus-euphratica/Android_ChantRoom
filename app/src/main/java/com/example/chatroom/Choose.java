package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;

public class Choose extends AppCompatActivity implements View.OnClickListener {
    private Button people;
    private Button chat;
    private String name;
    private EditText ip_Input;
    private String ip;
    private Intent intent1;
    private InputStream receiveInput;
    private OutputStream sendOutPut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        people=findViewById(R.id.people_Chat);
        people.setOnClickListener(this);
        chat=findViewById(R.id.chat);
        chat.setOnClickListener(this);
        ip_Input=findViewById(R.id.ip_chat);
        ip_Input.setText("192.168.43.");
        intent1=getIntent();
        name=intent1.getStringExtra("name");
        try {
            receiveInput=MyApplication.inputStream;
            sendOutPut=MyApplication.outputStream;
        }catch (Exception e){
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiveMsg();
            }
        }).start();


    }

    //接受服务器传来的信息
    public void receiveMsg() {


        byte[] bytes = new byte[1024 * 3];
        int len;
        String recevieMsg;
        String sign;
        while (true) {
            try {
                while ((len = receiveInput.read(bytes)) != -1) {
                    recevieMsg = new String(bytes, 0, len);

                    sign=recevieMsg.substring(0,5);
                    if (sign.equals("#chat")){
                            recevieMsg="#accp"+recevieMsg.substring(5,len);
                            sendMes(recevieMsg);
                        Intent intent=new Intent(this,Chat.class);
                        intent.putExtra("name",name);
                        startActivity(intent);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public void onClick(View v) {

            switch (v.getId()){
                case R.id.people_Chat:
//                    closeStream();
                    Intent intent=new Intent(Choose.this,MainActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                    break;
                case R.id.chat:
                    ip=ip_Input.getText().toString();
                    if (ip.equals("")){
                        Toast.makeText(MyApplication.getContext(), "请输入IP", Toast.LENGTH_SHORT).show();
                        break;
                    }
//                    closeStream();
                    sendMes("#chat"+ip);
                    Intent intent2=new Intent(Choose.this,Chat.class);
                    intent2.putExtra("name",name);
                    startActivity(intent2);
                    break;
            }
    }
    public void closeStream(){
        try {
            receiveInput.close();
            sendOutPut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMes(final String content){
        try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendOutPut.write(content.getBytes());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e){
            e.printStackTrace();
        }
    }


}
