package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText ipAddressEditText;
    private EditText nickNameEditText;
    public static String IP = "ip";
    public static String NICK = "nick";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipAddressEditText = findViewById(R.id.editText1);
        nickNameEditText = findViewById(R.id.editText2);
        Button loginButton = findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SimpleChatActivity.class);
                intent.putExtra(IP, ipAddressEditText.getText().toString());
                intent.putExtra(NICK, nickNameEditText.getText().toString());
                startActivity(intent);
            }
        });
    }
}
