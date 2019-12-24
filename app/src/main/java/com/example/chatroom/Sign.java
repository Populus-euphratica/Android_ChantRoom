package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        ip.setText("192.168.43.178");
        port.setText("8564");
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Sign.this,MainActivity.class);
                intent.putExtra("name",name.getText().toString());
                intent.putExtra("port",port.getText().toString());
                intent.putExtra("ip",ip.getText().toString());
                startActivity(intent);
            }
        });

    }


}
