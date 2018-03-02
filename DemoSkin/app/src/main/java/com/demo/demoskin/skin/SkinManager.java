package com.demo.demoskin.skin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * class description here
 *
 */

public class SkinManager {
    public Resources getSkinResource() {
        return skinResource;
    }

    public void setSkinResource(Resources skinResource) {
        this.skinResource = skinResource;
        this.skinPackage = context.getPackageName();
    }

    //外置卡的APP 的 resource
    private Resources skinResource;
    private Context context;
    //插件apk里面的包名
    private String skinPackage;

    public void init(Context context) {
        this.context = context;
        this.skinResource = context.getResources() ;
        this.skinPackage = context.getPackageName();
    }

    private static final SkinManager ourInstance = new SkinManager();

    public static SkinManager getInstance() {
        return ourInstance;
    }

    private SkinManager() {
    }

    /**
     * 目的是获取插件的 resource ，从而获取插件的资源
     * @param path
     */
    public void loadSkin(String path) {
        AssetManager assetManager;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            skinPackage = packageInfo.packageName;

            assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getMethod("addAssetPath", String.class);
            method.invoke(assetManager, path);

            Resources resources = context.getResources();
            skinResource = new Resources(assetManager, resources.getDisplayMetrics(),
                    resources.getConfiguration());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 所有控件拿资源都通过这个方法，无论是本地还是皮肤
     *
     * @param refId 例如 0x0001
     * @return real id
     */
    public int getColor(int refId) {
        if (null == skinResource) {
            return refId;
        }
        String resName = context.getResources().getResourceEntryName(refId);
        int realId = skinResource.getIdentifier(resName, "color", skinPackage);
        int color;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            color = skinResource.getColor(realId, null);
        } else {
            color = skinResource.getColor(realId);
        }
        return color;
    }

    public Drawable getDrawable(@DrawableRes int refId) {
        Drawable drawable = ContextCompat.getDrawable(context, refId);
        if (null == skinResource) {
            return drawable;
        }
        String resName = context.getResources().getResourceEntryName(refId);
        int resId = skinResource.getIdentifier(resName, "drawable", skinPackage);
        return skinResource.getDrawable(resId);
    }
}
