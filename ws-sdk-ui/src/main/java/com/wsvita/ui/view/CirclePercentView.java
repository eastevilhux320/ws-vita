package com.wsvita.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


import com.wsvita.ui.R;

import java.math.BigDecimal;

import ext.NumberExt;
import ext.ViewExt;

/**
 * 圆环百分比view
 */
public class CirclePercentView extends View {
    // 圆画笔
    private Paint circlePaint;
    // 圆环画笔
    private Paint ringPaint;
    // 百分数画笔
    private Paint textPaint;
    //提示文本画笔
    private Paint tipsTextPaint;

    private float percent = 0.0f;//设一个初始的百分比，超过0的话，能在Android studio layout文件下直接看到效果

    //百分比值小数保留位数
    private int percentDigit;

    private String tipsText;

    //控件宽度
    private int mWidth;
    //画圆环需要的RectF,因为只需要生成一次，假如刷新界面的话，他就会一直生成，虽然不影响，但是把它拿出来全局
    private RectF rectF;
    //文字大小
    private float textSize;
    //圆环宽度
    private int stroke;

    /**
     * 圆环百分比颜色
     */
    private int circlePercentColor;
    /**
     * 圆环颜色，即非百分比的颜色
     */
    private int circleNorColor;

    /**
     * 百分比文字颜色
     */
    private int percentTextColor;

    private boolean percentAnimFlag;

    private int tipsTextColor;
    private float tipsTextSize;

    public CirclePercentView(Context context) {
        super(context);
    }

    public CirclePercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CirclePercentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CirclePercentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CirclePercentView);
        circlePercentColor = ta.getColor(R.styleable.CirclePercentView_circle_percent_color,
                Color.parseColor("#0528AF"));
        circleNorColor = ta.getColor(R.styleable.CirclePercentView_circle_nor_color,
                Color.parseColor("#03DAC5"));
        percentTextColor = ta.getColor(R.styleable.CirclePercentView_percent_text_color,
                Color.parseColor("#101010"));
        textSize = ta.getDimension(R.styleable.CirclePercentView_percent_text_size, 10f);
        percent = ta.getFloat(R.styleable.CirclePercentView_percent,0.00f);
        percentDigit = ta.getInt(R.styleable.CirclePercentView_percent_digit,2);
        percentAnimFlag = ta.getBoolean(R.styleable.CirclePercentView_percent_anim_flag,false);
        tipsText = ta.getString(R.styleable.CirclePercentView_tips_text);
        if(tipsText == null){
            tipsText = "";
        }
        tipsTextColor = ta.getColor(R.styleable.CirclePercentView_tips_text_color,
                Color.parseColor("#999999"));
        tipsTextSize = ta.getDimension(R.styleable.CirclePercentView_tips_text_size,8f);
        ta.recycle();

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);//设置抗锯齿
        circlePaint.setStyle(Paint.Style.STROKE);//设置绘画风格为边框
        //这个是个dp转px的方法，不严格的话可以随便写个10,12,15随意
        stroke = ViewExt.INSTANCE.dip2px(10);
        circlePaint.setStrokeWidth(stroke);//设置边框宽度
        circlePaint.setColor(circleNorColor);//设置颜色，可以替换成Color.parseColor("#xxxxxx")

        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(stroke);
        ringPaint.setColor(circlePercentColor);//可以替换成Color.parseColor("#xxxxxx")

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);//设置风格为充满
        textPaint.setAntiAlias(true);
        textPaint.setColor(percentTextColor);
        textPaint.setTextSize(textSize);


        tipsTextPaint = new Paint();
        tipsTextPaint.setStyle(Paint.Style.FILL);//设置风格为充满
        tipsTextPaint.setAntiAlias(true);
        tipsTextPaint.setColor(tipsTextColor);
        tipsTextPaint.setTextSize(tipsTextSize);

        if(percentAnimFlag && percent > 0){
            addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    // 判断布局是否发生变化，且绘制完成
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        // 执行动画效果
                        startAnimator(percent);
                    }
                }
            });
        }
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
    }

    public String getTipsText() {
        return tipsText;
    }

    public void setTipsText(String tipsText) {
        this.tipsText = tipsText;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        int mHeight = getHeight();
        if(mWidth > mHeight){
            mWidth = mHeight;//获取最短的一边
        }
        rectF = new RectF();//rectF为画扇形所需要的矩形，扇形会在矩形内
        //rectF长宽为right-left或bottom-top，因为是正方形，都一样
        //因为画笔使用了stroke，他半径不为控件的一半了，不设置stroke的话长宽都是是圆半径*2,
        // 长宽 = mWidth - stroke，
        //因为rectF范围不是满控件了，所以位置不能是0 0不然画出来，会偏左上角，
        // 为了使rectF居中，都往右下角偏移了stroke/2,所以也要少减stroke/2,
        rectF.left = stroke/2;
        rectF.top = stroke/2;
        //知道了长宽，通过左上可以计算右下
        rectF.right = mWidth - stroke/2;
        rectF.bottom = mWidth - stroke/2;
        //可以自己设置成rectF.left = 0; rectF.top = 0; rectF.right = mWidth - stroke; rectF.bottom = mWidth - stroke
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        circlePaint.setColor(circleNorColor);
        ringPaint.setColor(circlePercentColor);
        //画外层圆环   当画笔设置了 StrokeWidth 时，圆的半径=内圆的半径+StrokeWidth/2
        canvas.drawCircle(mWidth/2, mWidth/2, (mWidth/2 - stroke) + stroke/2, circlePaint);
        float point = percent*360/100;
        if (point > 360) {
            point = 360;
        }
        //rectF扇形范围，-90,从上开始绘画，比如改成0，就是从左开始绘画，顺时针，point绘画多少角度
        // false是去掉扇形的半径绘画，ringPaint画笔
        canvas.drawArc(rectF, -90, point, false, ringPaint);
        String str = NumberExt.INSTANCE.formatDecimalPlaces(percent,percentDigit) + "%";//需要自定义文字的话，就定义一个全局str，从外面传进来
        if(tipsText.isEmpty()){
            float textWid = textPaint.measureText(str);
            //str文字，第二个和第三个参数是为了使他居中mWidth/2 - textWid/2：从x轴的哪里开始绘画，应该很好计算
            //mWidth/2 + textSize/2 之所以是+是因为文字是从左下角开始绘制的，开始以为是从上到下，写成了-，就偏上了
            canvas.drawText(str, mWidth/2 - textWid/2, mWidth/2 + textSize/2, textPaint);
        }else{
            float tipsWid = textPaint.measureText(tipsText);
            float textWid = textPaint.measureText(str);
            //画tips文本
            canvas.drawText(tipsText, mWidth/2 - tipsWid/2, mWidth/2 - textSize/4, tipsTextPaint);

            //画percent文本
            //str文字，第二个和第三个参数是为了使他居中mWidth/2 - textWid/2：从x轴的哪里开始绘画，应该很好计算
            //mWidth/2 + textSize/2 之所以是+是因为文字是从左下角开始绘制的，开始以为是从上到下，写成了-，就偏上了
            canvas.drawText(str, mWidth/2 - textWid/2, mWidth/2 + textSize, textPaint);
        }
    }

    private void startAnimator(float process) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, process);//从百分0到process的过程
        animator.setDuration(800);//持续时间
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (Float) animation.getAnimatedValue();//当这个动画过程进行过程中，获取过程中的数值
                BigDecimal b = new BigDecimal((Float) animation.getAnimatedValue());
                percent = b.setScale(2, BigDecimal.ROUND_DOWN).floatValue();//保留两位小数
                invalidate();//重绘，会重新走onDraw，onDraw里是根据percent绘画的，所以界面会改变
            }
        });
        animator.start();
    }


}
