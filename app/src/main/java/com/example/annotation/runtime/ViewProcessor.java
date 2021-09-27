package com.example.annotation.runtime;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.example.annotation.runtime.api.FindView;
import com.example.annotation.runtime.api.OnClick;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ViewProcessor {
    private static final String TAG = "BindViewHelper";

    public void inject(Activity activity) {
        try {
            injectId(activity);
            injectOnClick(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void injectOnClick(Activity activity) {
        Class<?> cls = activity.getClass();
        // 获取全部声明的方法
        for (Method method : cls.getDeclaredMethods()) {
            Log.d(TAG, "injectOnClick method is : " + method.getName());
            // 获取该方法上的注解
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (!(annotation instanceof OnClick)) {
                    continue;
                }
                Log.d(TAG, "injectOnClick:onclick ");
                // 找到OnClick注解
                OnClick findView = (OnClick) annotation;
                // 获取OnClick的值
                int id = findView.value();
                // 找到对应的view
                View view = activity.findViewById(id);
                if (view == null) {
                    continue;
                }
                view.setOnClickListener((view1) -> {
                    Log.d(TAG, "injectOnClick: callback");
                    try {
                        // 反射调用该方法
                        method.setAccessible(true);
                        method.invoke(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void injectId(Activity activity) throws IllegalAccessException {
        Class<?> cls = activity.getClass();
        for (Field field : cls.getDeclaredFields()) {
            Log.d(TAG, "injectOnClick filed is : " + field);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (!(annotation instanceof FindView)) {
                    continue;
                }
                // 找到FindView注解
                FindView findView = (FindView) annotation;
                int id = findView.value();
                View view = activity.findViewById(id);
                if (view == null) {
                    continue;
                }
                field.setAccessible(true);
                // 给该域负值
                field.set(activity, view);
            }
        }
    }
}
