package com.example.annotation.runtime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.annotation.R;
import com.example.annotation.runtime.api.FindView;
import com.example.annotation.runtime.api.OnClick;

public class RuntimeActivity extends AppCompatActivity {

    @FindView(R.id.runtime_button1)
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runtime);
        ViewProcessor bindViewHelper = new ViewProcessor();
        bindViewHelper.inject(this);
        mButton.setOnClickListener((view) -> {
            Toast.makeText(this, "运行时注解 FindView", Toast.LENGTH_SHORT).show();
        });
    }

    @OnClick(R.id.runtime_button2)
    private void onClick2() {
        Toast.makeText(this, "运行时注解 OnClick", Toast.LENGTH_SHORT).show();
    }
}