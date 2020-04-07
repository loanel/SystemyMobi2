package com.example.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class SimpleChatActivity extends AppCompatActivity {

    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    private ListView chatListView;
    String ip;
    String nick;
    Handler myHandler;

    MqttClient sampleClient = null;

    private void startMQTT() {
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            String broker = "tcp://" + ip + ":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", topic);
                    b.putString("MSG", new String(mqttMessage.getPayload()));
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            System.out.println("Connecting to broker : " + broker);
            sampleClient.connect(connectOptions);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        nick = getIntent().getStringExtra(MainActivity.NICK);
        ip = getIntent().getStringExtra(MainActivity.IP);

        listItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        chatListView = findViewById(R.id.chatListView);
        chatListView.setAdapter(adapter);

        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                System.out.println("[" + msg.getData().getString("NICK") + "]" + msg.getData().getString("MSG"));
                listItems.add("[" + msg.getData().getString("NICK") + "]" + msg.getData().getString("MSG"));
                adapter.notifyDataSetChanged();
                chatListView.setSelection(listItems.size() - 1);
            }
        };

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        });
        t.start();


        final EditText messageEditText = findViewById(R.id.editText3);
        Button send = findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MqttMessage message = new MqttMessage();
                message.setPayload(messageEditText.getText().toString().getBytes());
                try {
                    sampleClient.publish(nick, message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sampleClient != null) {
            try {
                sampleClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}

