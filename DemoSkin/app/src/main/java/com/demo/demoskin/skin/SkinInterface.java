package com.demo.demoskin.skin;

import android.view.View;

/**
 * class description here
 *
 */

public abstract class SkinInterface {
    /**
     * 组件的属性名称，例如 background
     */
    String attrName;

    /**
     * 组件引用资源的id (integer 类型)
     */
    int refId = 0;
    /**
     * 组件引用资源的名称，例如 app_icon
     */
    String attrValueName;

    /**
     * 组件引用资源的类型，例如drawable
     */
    String attrType;

    public SkinInterface(String attrName, int refId, String attrValueName, String attrType) {
        this.attrName = attrName;
        this.refId = refId;
        this.attrType = attrType;
        this.attrValueName = attrValueName;
    }

    /**
     * 执行具体切换工作
     * @param view 作用对象
     */
    public abstract void apply(View view);
}
