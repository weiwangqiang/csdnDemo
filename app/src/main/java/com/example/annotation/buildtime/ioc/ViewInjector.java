package com.example.annotation.buildtime.ioc;

import android.app.Activity;
import android.view.View;

public class ViewInjector {

    private static final String SUFFIX = "_ViewBind";

    public static void injectView(Activity activity) {
        findProxyActivity(activity);
    }

    /**
     * 通过反射创建要使用的类的对象
     */
    private static void findProxyActivity(Object activity) {
        try {
            Class<?> clazz = activity.getClass();
            String newClass = clazz.getName() + SUFFIX;
            Class<?> injectorClazz = Class.forName(newClass);
            injectorClazz.getConstructor(clazz).newInstance(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
