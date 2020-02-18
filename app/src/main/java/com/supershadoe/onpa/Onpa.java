package com.supershadoe.onpa;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class Onpa extends AppCompatActivity {

    private boolean playstate = false;
    private OnpaThread playThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar header = findViewById(R.id.Header);
        setSupportActionBar(header);

       final FloatingActionButton play = findViewById(R.id.Play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playstate) {
                    playstate = true;
                    play.setImageResource(R.drawable.pause);
                    if(playThread != null){
                        playThread.Terminate();
                        playThread = null;
                    }
                    final EditText server = findViewById(R.id.Ipaddr);
                    final EditText port = findViewById(R.id.port);
                    playThread = new OnpaThread(server.getText().toString(), port.getText().toString());
                    new Thread(playThread).start();
                } else {
                    playstate = false;
                    play.setImageResource(R.drawable.play);
                    if(playThread != null){
                        playThread.Terminate();
                        playThread = null;
                    }
                }
            }
        });
    }
}