package com.demo.demoskin.skin;

import android.view.View;
import android.widget.TextView;

/**
 * class description here
 *
 */

public class TextSkin extends SkinInterface {
    public TextSkin(String attrName, int refId, String attrValueName, String attrType) {
        super(attrName, refId, attrValueName, attrType);
    }

    @Override
    public void apply(View view) {
        if(view instanceof TextView){
            TextView textView = (TextView)view ;
            textView.setTextColor(SkinManager.getInstance().getColor(refId));
        }
    }
}
