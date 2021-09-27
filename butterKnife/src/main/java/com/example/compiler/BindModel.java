package com.example.compiler;

import java.util.Objects;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class BindModel {
    // 成员变量Element
    private VariableElement mViewFieldElement;
    // 成员变量类型
    private TypeMirror mViewFieldType;
    // View的资源Id
    private int mResId;

    public BindModel(Element element, int resId) {
        // 校验Element是否是成员变量
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException("element is not FIELD");
        }
        // 成员变量Element
        mViewFieldElement = (VariableElement) element;
        // 成员变量类型
        mViewFieldType = element.asType();
        // 获取注解的值
        mResId = resId;
    }

    public int getResId() {
        return mResId;
    }

    public String getViewFieldName() {
        return mViewFieldElement.getSimpleName().toString();
    }

    public TypeMirror getViewFieldType() {
        return mViewFieldType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindModel bindModel = (BindModel) o;
        return mResId == bindModel.mResId &&
                Objects.equals(mViewFieldElement, bindModel.mViewFieldElement) &&
                Objects.equals(mViewFieldType, bindModel.mViewFieldType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mViewFieldElement, mViewFieldType, mResId);
    }
}
