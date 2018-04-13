package com.min.hybrid.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LocalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        TextView tv=findViewById(R.id.tv);
        String name=getIntent().getStringExtra("name");
        int age=getIntent().getIntExtra("age",-1);
        tv.setText("name="+name+"\nage="+age);
    }
}
