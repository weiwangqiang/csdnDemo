package com.demo.demoskin.skin;

import android.view.View;

/**
 * class description here
 *
 */

public class BackgroundSkin extends SkinInterface{
    private static final String TAG = "BackgroundSkin";

    public BackgroundSkin(String attrName, int refId, String attrValueName, String attrType) {
        super(attrName, refId, attrValueName, attrType);
    }

    @Override
    public void apply(View view) {
        if("color".equals(attrType)){
            view.setBackgroundColor(SkinManager.getInstance().getColor(refId));
        }else if("drawable".equals(attrType)){
            view.setBackgroundDrawable(SkinManager.getInstance().getDrawable(refId));
        }
    }
}
