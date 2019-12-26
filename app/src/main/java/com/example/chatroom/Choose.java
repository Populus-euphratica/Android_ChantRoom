package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;

public class Choose extends AppCompatActivity implements View.OnClickListener {
    private Button people;
    private Button chat;
    private String name;
    private EditText ip_Input;
    private String ip;
    private Intent intent1;
    private InputStream receiveInput;
    private OutputStream sendOutPut;
    private Thread thread;
    private static Semaphore semaphore = new Semaphore(5);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        people = findViewById(R.id.people_Chat);
        people.setOnClickListener(this);
        chat = findViewById(R.id.chat);
        chat.setOnClickListener(this);
        ip_Input = findViewById(R.id.ip_chat);
        ip_Input.setText("192.168.43.");
        intent1 = getIntent();
        name = intent1.getStringExtra("name");
        try {
            receiveInput = MyApplication.inputStream;
            sendOutPut = MyApplication.outputStream;
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    receiveMsg();
                    System.out.println(semaphore.availablePermits()+"16531361+++++++++++++++++++++++++++++++++");
                    return;
                }
            });
            thread.run();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void receiveMsg() {
        byte[] bytes = new byte[1024];
        int len;
        String recevieMsg;
        String sign;
        boolean istrue=true;
        while (istrue) {
            try {
                semaphore.acquire();

                while ((len = receiveInput.read(bytes)) != -1) {
                    recevieMsg = new String(bytes, 0, len);
                    System.out.println(recevieMsg);
                    System.out.println(semaphore.availablePermits()+"+++++++++++++++++++++++++++++++++");
                    sign = recevieMsg.substring(0, 5);
                    if (sign.equals("#chat")) {
                        recevieMsg = "#accp" + recevieMsg.substring(5, len);
                        sendOutPut.write(recevieMsg.getBytes());
                        Intent intent = new Intent(this, Chat.class);
                        intent.putExtra("name", name);
                        semaphore.acquire();
//                        closeStream();
                        startActivity(intent);
                        break;
                    }
                }
                semaphore.release();
                if (semaphore.availablePermits()<=4) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("我出来了");
    }


    //接受服务器传来的信息


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.people_Chat:
//                    closeStream();
                Intent intent = new Intent(Choose.this, MainActivity.class);
                intent.putExtra("name", name);
                try {
                    semaphore.acquire();
                    thread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");

                startActivity(intent);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.chat:
                ip = ip_Input.getText().toString();
                if (ip.equals("")) {
                    Toast.makeText(MyApplication.getContext(), "请输入IP", Toast.LENGTH_SHORT).show();
                    break;
                }

//                sendMes("#chat" + ip);
                final String sign="#chat" + ip;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("Choose", "run: "+sign);
                            sendOutPut.write(sign.getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Intent intent2 = new Intent(Choose.this, Chat.class);
                intent2.putExtra("name", name);
                startActivity(intent2);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void closeStream() {
        try {
            receiveInput.close();
            sendOutPut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMes(final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendOutPut.write(content.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
