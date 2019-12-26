package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.Socket;

public class Sign extends AppCompatActivity {

    private EditText name;
    private EditText ip;
    private EditText port;
    private Button sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        name=(EditText)findViewById(R.id.name_input);
        ip=(EditText)findViewById(R.id.ip_input);
        port=(EditText)findViewById(R.id.port_input);
        sign=(Button)findViewById(R.id.enter);
        ip.setText("192.168.137.1");
        port.setText("8564");
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MyApplication.setSocket(ip.getText().toString(), Integer.parseInt(port.getText().toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
//                if (MyApplication.socket.getKeepAlive()) {
//                    Toast.makeText(MyApplication.getContext(), "登入失败", Toast.LENGTH_SHORT).show();
//                    finish();
//                } else {
//                    Toast.makeText(MyApplication.getContext(), "登入成功", Toast.LENGTH_SHORT).show();
//
//                }
                Intent intent = new Intent(Sign.this, Choose.class);
                intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

    }


}
