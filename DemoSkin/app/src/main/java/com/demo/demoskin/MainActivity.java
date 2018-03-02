package com.demo.demoskin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.demo.demoskin.skin.SkinBaseActivity;
import com.demo.demoskin.skin.SkinManager;
import com.demo.demoskin.skin.SkinSecond;

import java.io.File;

public class MainActivity extends SkinBaseActivity {

    private static final String TAG = "MainActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().init(this);
        setContentView(R.layout.activity_main);
        Button
    }

    public void jump(View view) {
        startActivity(new Intent(this,SkinSecond.class));
    }

    public void change(View view) {
        String path = new File(Environment.getExternalStorageDirectory(), "skin.apk").getAbsolutePath();
        SkinManager.getInstance().loadSkin(path);
        upDate();
    }

    public void back(View view) {
        SkinManager.getInstance().setSkinResource(getResources());
        upDate();
    }
}
