package com.demo.demoqqbub;

import android.animation.Animator;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * class description here
 *
 *  仿照 QQ 可以拖拽的小红点
 *
 */

public class BubView extends View {
    private static final String TAG = "PaintView";
    private float MOVE_OFFSET;
    private final int BUBBLE_STATE_DEFAULT = 1;//静止状态
    private final int BUBBLE_STATE_CONNECT = 2;//移动并且相连状态
    private final int BUBBLE_STATE_APART = 3;//分离状态
    private final int BUBBLE_STATE_BOB = 4;//爆炸状态

    private int mBubState = BUBBLE_STATE_DEFAULT;
    //静止状态的小球参数
    private PointF mBubStillCenter;
    private float mBubStillRadius;
    private Paint mBubPaint;
    //移动状态的小球参数
    private PointF mBubMoveCenter;
    private float mBubMoveRadius;
    private Paint mTextPaint;
    private Rect mTextRect;
    private Paint mBurstPaint;
    private Rect mBurstRect;
    private float mMaxDist;
    private float mBubRadius;
    private Path mBerierPath;//画贝塞尔的路径
    private String mTextStr = "13";
    private Bitmap[] mBurstBitMapArray;
    private int[] mBurstDrawableArray = {
            R.drawable.bub1,
            R.drawable.bub2,
            R.drawable.bub3,
            R.drawable.bub4,
            R.drawable.bub5
    };
    private int mCurBitmapIndex = 0;
    public BubView(Context context) {
        super(context);
        init();
    }

    public BubView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged: ");
        initView(w, h);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mBubState != BUBBLE_STATE_BOB) {
                    mCenterDist = (float) Math.hypot(event.getX() - mBubStillCenter.x,
                            event.getY() - mBubStillCenter.y);
                    if (mCenterDist < mBubMoveRadius) {//点中了圆，处于相连状态
                        mBubState = BUBBLE_STATE_CONNECT;
                    } else {//没有点中，处于默认状态
                        mBubState = BUBBLE_STATE_DEFAULT;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //如果处于默认状态就直接返回
                if (mBubState == BUBBLE_STATE_DEFAULT) {
                    return true;
                }
                //更新移动小圆位置
                mCenterDist = (float) Math.hypot(event.getX() - mBubStillCenter.x,
                        event.getY() - mBubStillCenter.y);
                mBubMoveCenter.set(event.getX(), event.getY());
                if (mBubState == BUBBLE_STATE_CONNECT) {//如果处于连接状态
                    if (mCenterDist < mMaxDist - MOVE_OFFSET) {//减去MOVE_OFFSET是避免 mBubStillRadius == 0
                        //减少mBubStillRadius
                        mBubStillRadius = mBubRadius - (mBubRadius * mCenterDist / mMaxDist );
                    } else {
                        //处于分离状态
                        mBubState = BUBBLE_STATE_APART;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mBubState == BUBBLE_STATE_CONNECT) {
                    //气泡还原
                    startBubResetAnim();
                } else if (mBubState == BUBBLE_STATE_APART) {
                    if (mCenterDist < 2 * mBubRadius) {
                        //气泡还原
                        startBubResetAnim();
                    } else {
                        //气泡爆炸消失
                        startBubBurstAnim();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    //启动气泡破的动画
    private void startBubBurstAnim() {
        mBubState = BUBBLE_STATE_BOB;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mBurstBitMapArray.length - 1);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurBitmapIndex = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
        //监听动画，及时还原
        valueAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                initView(getWidth(), getHeight());
                invalidate();
                mBubState = BUBBLE_STATE_DEFAULT;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    //气泡还原动画
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startBubResetAnim() {
        mBubState = BUBBLE_STATE_DEFAULT;

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointFEvaluator(),
                new PointF(mBubMoveCenter.x, mBubMoveCenter.y),
                new PointF(mBubStillCenter.x, mBubStillCenter.y));
        valueAnimator.setInterpolator(new OvershootInterpolator(5));
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBubMoveCenter = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
        protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: ");
        //移动的大圆没有消失
        if (mBubState != BUBBLE_STATE_BOB) {
            canvas.drawCircle(mBubMoveCenter.x, mBubMoveCenter.y, mBubMoveRadius, mBubPaint);
        }
        //处于相连状态
        if(mBubState == BUBBLE_STATE_CONNECT){
            drawConnectState(canvas);
        }
        //画字体
        if(mBubState != BUBBLE_STATE_BOB){
            if (!TextUtils.isEmpty(mTextStr)) {
                canvas.drawText(mTextStr,
                        mBubMoveCenter.x - mTextRect.width() / 2,
                        mBubMoveCenter.y + mTextRect.height() / 2,
                        mTextPaint);
            }
        }
        //开始爆炸，就画爆炸效果
        if (mBubState == BUBBLE_STATE_BOB) {
            mBurstRect.set((int) (mBubMoveCenter.x - mBubMoveRadius), (int) (mBubMoveCenter.y - mBubMoveRadius)
                    , (int) (mBubMoveCenter.x + mBubMoveRadius), (int) (mBubMoveCenter.y + mBubMoveRadius));
            canvas.drawBitmap(mBurstBitMapArray[mCurBitmapIndex], null, mBurstRect, mBurstPaint);
        }

    }
    //将sp转为px
    public int spToPx(float spVal){
        float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spVal*scale + 0.5f);
    }
    private PointF A;
    private PointF B;
    private PointF C;
    private PointF D;
    private float mCenterDist;

    //画相连的状态
    private void drawConnectState(Canvas canvas) {
        //画静止的小球
        canvas.drawCircle(mBubStillCenter.x, mBubStillCenter.y, mBubStillRadius, mBubPaint);
        //画贝塞尔曲线
        //iAnchorX,iAnchorY 是两个圆心的中点
        float iAnchorX = (mBubStillCenter.x + mBubMoveCenter.x) / 2;
        float iAnchorY = (mBubStillCenter.y + mBubMoveCenter.y) / 2;

        float AbsX = Math.abs(mBubStillCenter.x - mBubMoveCenter.x);
        float AbsY = Math.abs(mBubStillCenter.y - mBubMoveCenter.y);

        mCenterDist = (float) Math.sqrt(AbsX * AbsX + AbsY * AbsY);
        float sin1 = (mBubMoveCenter.y - mBubStillCenter.y) / mCenterDist;
        float cos1 = (mBubMoveCenter.x - mBubStillCenter.x) / mCenterDist;

        //A
        float iBubStillStartX = mBubStillCenter.x - mBubStillRadius * sin1;
        float iBubStillStartY = mBubStillCenter.y + mBubStillRadius * cos1;
        A = new PointF(iBubStillStartX, iBubStillStartY);
        //B
        float iBubMoveEndX = mBubMoveCenter.x - mBubMoveRadius * sin1;
        float iBubMoveEndY = mBubMoveCenter.y + mBubMoveRadius * cos1;
        B = new PointF(iBubMoveEndX, iBubMoveEndY);
        //C
        float iBubMoveStartX = mBubMoveCenter.x + mBubMoveRadius * sin1;
        float iBubMoveStartY = mBubMoveCenter.y - mBubMoveRadius * cos1;
        C = new PointF(iBubMoveStartX, iBubMoveStartY);
        //D
        float iBubStillEndX = mBubStillCenter.x + mBubStillRadius * sin1;
        float iBubStillEndY = mBubStillCenter.y - mBubStillRadius * cos1;
        D = new PointF(iBubStillEndX, iBubStillEndY);

        mBerierPath.reset();
        mBerierPath.moveTo(A.x, A.y);
        mBerierPath.quadTo(iAnchorX, iAnchorY, B.x, B.y);//画曲线
        mBerierPath.lineTo(C.x, C.y);//画直线
        mBerierPath.quadTo(iAnchorX, iAnchorY, D.x, D.y);
        mBerierPath.close();//整个曲线闭合
        canvas.drawPath(mBerierPath, mBubPaint);//根据path画出图形
    }

    private void initView(int w, int h) {
        if (mBubStillCenter == null) {
            mBubStillCenter = new PointF(w / 2, h / 2);
        } else {
            mBubStillCenter.set(w / 2, h / 2);
        }
        if (mBubMoveCenter == null) {
            mBubMoveCenter = new PointF(w / 2, h / 2);
        } else {
            mBubMoveCenter.set(w / 2, h / 2);
        }
    }

    private void init() {
        mBubPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBubPaint.setColor(getResources().getColor(R.color.colorAccent));
        mBubPaint.setStyle(Paint.Style.FILL);

        mTextRect = new Rect();
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(spToPx(15));
        mTextPaint.getTextBounds(mTextStr, 0, mTextStr.length(), mTextRect);

        //贝塞尔曲线
        mBerierPath = new Path();

        mBurstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBurstPaint.setFilterBitmap(true);
        mBurstRect = new Rect();
        //初始化气泡资源
        mBurstBitMapArray = new Bitmap[mBurstDrawableArray.length];
        for (int i = 0; i < mBurstBitMapArray.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mBurstDrawableArray[i]);
            mBurstBitMapArray[i] = bitmap;
        }

        mBubRadius = Math.max(mTextRect.height(),mTextRect.width());
        mBubStillRadius = mBubRadius;
        mBubMoveRadius = mBubRadius;
        mMaxDist = 8 * mBubRadius;
        MOVE_OFFSET = mMaxDist / 4;
    }
}
