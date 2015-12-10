package com.blogspot.thanhcs.realtimechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    EditText editText;
    TextView tvDsUser;
    Button btDK , btchat;
    ListView lv, lvChat;
    ArrayAdapter<String> arrString, arrAdapterChat;
    ArrayList<String> listUserName , listChat; //danh-sach-user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        tvDsUser = (TextView)findViewById(R.id.tvusernum);
        lv = (ListView)findViewById(R.id.listViewUserName);
        lvChat = (ListView)findViewById(R.id.listViewChat);
        btDK = (Button)findViewById(R.id.buttonDK);
        btchat = (Button)findViewById(R.id.buttonCHAT);
        Random ran = new Random();
        editText.setText("user" + ran.nextInt(10000));
        try {
            mSocket = IO.socket("http://192.168.1.15:3000");
            mSocket.on("ket-qua-dang-ki", onDangKi);
            mSocket.on("danh-sach-user", onDSUserName);
            mSocket.on("server-gui-tin-chat", onDSChat);
        } catch (URISyntaxException e) {
            Log.d("error", "bug here");
        }
        mSocket.connect();

       btDK.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String txt = editText.getText().toString();
               if (txt.equalsIgnoreCase("")) {
                   Toast.makeText(MainActivity.this, "please fill that blank !!", Toast.LENGTH_LONG).show();
               } else {
                   mSocket.emit("client-gui-username", editText.getText().toString());
                   editText.setText("");
               }
           }
       });


        btchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = editText.getText().toString();
                if (txt.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, "please fill that blank !!", Toast.LENGTH_LONG).show();
                } else {
                    mSocket.emit("client-gui-tin-chat", editText.getText().toString());
                    editText.setText("");
                }
            }
        });





        listUserName = new ArrayList<>();
        listChat = new ArrayList<>();
        arrAdapterChat = new ArrayAdapter<String>(MainActivity.this , android.R.layout.simple_list_item_1, listChat);
        arrString = new ArrayAdapter<String>(MainActivity.this , android.R.layout.simple_list_item_1, listUserName);

        lv.setAdapter(arrString);
        lvChat.setAdapter(arrAdapterChat);

    }



    private Emitter.Listener onDSChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;
                    try {
                        noidung = data.getString("tinchat");
                        if(!noidung.equalsIgnoreCase("")){
                            listChat.add(noidung);
                            arrAdapterChat.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }


    };


    private Emitter.Listener onDSUserName = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray arrUser;
                    listUserName.clear();
                    try {
                        arrUser = data.getJSONArray("danhsachuser");
                        tvDsUser.setText(arrUser.length() + " user online");
                        tvDsUser.setAllCaps(true);
                        for(int i = 0 ;i<arrUser.length();i++){
                            listUserName.add(arrUser.get(i).toString());
                        }
                        arrString.notifyDataSetChanged();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }


    };




    private Emitter.Listener onDangKi = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;
                    try {
                        noidung = data.getString("noidung");
                        if(noidung=="true"){
                            Toast.makeText(MainActivity.this , "Welcome to new planet.", Toast.LENGTH_LONG).show();
                            btDK.setVisibility(View.GONE);
                            btchat.setEnabled(true);
                        }else
                            Toast.makeText(MainActivity.this , "Pick another name , please.", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }


    };
}
