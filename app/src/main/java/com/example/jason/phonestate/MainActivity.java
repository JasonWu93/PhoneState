package com.example.jason.phonestate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, PhoneStateService.class);
        intent.setAction("Phone state");
        startService(intent);
        Toast.makeText(this, "Started Service！",1).show();
    }
}
