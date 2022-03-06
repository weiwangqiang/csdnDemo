package com.example.canvasdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class CanvasDemo extends View {

    public CanvasDemo(Context context) {
        super(context);
        init();
    }

    public CanvasDemo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CanvasDemo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Path path = new Path();

    Bitmap scrBitmap;
    Bitmap desBitmap;


    private static final String TAG = "CanvasDemo";

    private void init() {
        scrBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hero);
        desBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.destination);
        paint.setColor(getResources().getColor(android.R.color.black));
        paint.setStyle(Paint.Style.STROKE);
        paint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.NORMAL));
        paint.setStrokeWidth(getResources().getDimensionPixelOffset(R.dimen.paint_width));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    float[] line = new float[]{};

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = getWidth() >> 1;
        float y = getHeight() >> 1;
        int width = 200;
//        paint.setColorFilter(new LightingColorFilter(0x00ffff, 0));
        Rect rect = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(scrBitmap, 0, 0, paint);
//        canvas.drawCircle(x, y, 200, paint);
    }
}

