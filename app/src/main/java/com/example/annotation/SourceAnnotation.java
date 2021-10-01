package com.example.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SourceAnnotation {

    public static final int LEVE_1 = 1;
    public static final int LEVE_2 = 2;

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef({LEVE_1, LEVE_2})
    public @interface Level {

    }

    public static void main(String[] args) {
        SourceAnnotation sourceAnnotation = new SourceAnnotation();
//        sourceAnnotation.setLeve(0); 报错
//        sourceAnnotation.setLeve(1); 报错
        sourceAnnotation.setLeve(LEVE_1);
    }

    public void setLeve(@Level int level) {
        System.out.println("level " + level);
    }
}
