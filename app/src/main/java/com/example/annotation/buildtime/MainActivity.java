package com.example.annotation.buildtime;

import android.os.Bundle;

import com.example.annotation.BindView;
import com.example.annotation.Onclick;
import com.example.annotation.R;
import com.example.annotation.buildtime.ioc.ViewInjector;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button1)
    public Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.injectView(this);
        button1.setOnClickListener((view) -> {
            Toast.makeText(this, "编译时:点我1", Toast.LENGTH_SHORT).show();
        });
    }

    @Onclick(R.id.button2)
    public void press1() {
        Toast.makeText(this, "编译时: 点我2", Toast.LENGTH_SHORT).show();
    }
}