package com.demo.demoskin.skin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;

public abstract class SkinBaseActivity extends Activity {
    private SkinFactory skinFactory  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        skinFactory = new SkinFactory();
        //设置当前activity解析xml的工厂类
        LayoutInflaterCompat.setFactory(getLayoutInflater(),skinFactory );//LayoutInflaterFactory
    }
    //手动更换皮肤
    public void upDate(){
        skinFactory.upDate();
    }

}

