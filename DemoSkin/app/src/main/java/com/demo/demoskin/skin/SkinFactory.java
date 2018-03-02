package com.demo.demoskin.skin;

import android.app.Fragment;
import android.content.Context;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class description here
 * 通过SkinFactory 解析xml文件，并保存相应的view和view的属性如background，textColor等
 */

public class SkinFactory implements LayoutInflaterFactory {

    private static final String[] preFixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };  //这些都是系统组件
    private static final String TAG = "SkinFactory";
    private Map<View, SkinItem> map = new HashMap<>();

    public void upDate() {
        for(View view : map.keySet()){
            if(null == view){
                continue;
            }
            map.get(view).apply();
        }
    }

    class SkinItem {
        public SkinItem(List<SkinInterface> attrList, View view) {
            this.attrList = attrList;
            this.view = view;
        }

        public List<SkinInterface> attrList;
        public View view;
        //更新组件资源，调用skinInterface 的实现类
        public void apply() {
            for (SkinInterface skinInterface : attrList) {
                skinInterface.apply(view);
            }
        }
    }

    private static final String[] preFixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };  //这些都是系统组件
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = null;
        Fragment
        if (name.indexOf(".") == -1) {
            //系统控件
            for (String prix : preFixList) {
                view = createView(context, attrs, prix + name);
                if (null != view) {
                    break;
                }
            }
        } else {
            //自定义控件
            view = createView(context, attrs, name);
        }
        if (null != view) {
            parseSkinView(view, context, attrs);
        }
        return view;
    }

    //找到需要换肤的控件
    private void parseSkinView(View view, Context context, AttributeSet attrs) {
        List<SkinInterface> attrList = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //拿到属性名
            String attrName = attrs.getAttributeName(i);
            String attrValue = attrs.getAttributeValue(i);
            int id =-1;
            String entryName ="";

            String typeName ="";

            SkinInterface skinInterface = null ;

            switch (attrName) {
                case "background"://需要进行换肤
                    id = Integer.parseInt(attrValue.substring(1));
                    entryName = context.getResources().getResourceEntryName(id);
                    typeName = context.getResources().getResourceTypeName(id);
                    skinInterface = new BackgroundSkin(attrName,id,entryName,typeName);
                    break;
                case "textColor":
                    id = Integer.parseInt(attrValue.substring(1));
                    entryName = context.getResources().getResourceEntryName(id);
                    typeName = context.getResources().getResourceTypeName(id);
                    skinInterface = new TextSkin(attrName,id,entryName,typeName);
                    break;
                default:
                    break;
            }
            if(null != skinInterface){
                attrList.add(skinInterface);
            }

        }
        SkinItem skinItem = new SkinItem(attrList,view);
        map.put(view,skinItem);
        //在这里进行应用，判断是皮肤资源还是本地资源
        skinItem.apply();
    }

    //创建一个view
    private View createView(Context context, AttributeSet attrs, String name) {
        try {
            //实例化一个控件
            Class clarr = context.getClassLoader().loadClass(name);
            Constructor<? extends View> constructor =
                    clarr.getConstructor(new Class[]{Context.class, AttributeSet.class});
            constructor.setAccessible(true);
            return constructor.newInstance(context, attrs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
