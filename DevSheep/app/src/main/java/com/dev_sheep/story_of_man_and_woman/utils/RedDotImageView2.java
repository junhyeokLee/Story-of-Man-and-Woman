package com.dev_sheep.story_of_man_and_woman.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dev_sheep.story_of_man_and_woman.R;


/**
 * Created by Administrator on 2017/5/6 0006.
 */

public class RedDotImageView2 extends androidx.appcompat.widget.AppCompatImageView {

    public Boolean ISBOOLEAN = false;

    private Paint paint1;//绘制红点
    private int msgNum;//数字值
    private Rect textBounds;//文字的bound
    private int redDotSize = 9;//红点大小


    public RedDotImageView2(Context context, boolean isboolean){
        super(context);

        ISBOOLEAN = isboolean;

        paint1 = new Paint();

//        paint1.setColor(getResources().getColor(android.R.color.transparent));

        Log.e("BOOLEAN VALUE  = ",""+ISBOOLEAN);
        if(ISBOOLEAN == true) {
            paint1 = new Paint();
            paint1.setStyle(Paint.Style.FILL);
            paint1.setColor(getResources().getColor(android.R.color.transparent));
        }else {
            paint1 = new Paint();
            paint1.setStyle(Paint.Style.FILL);
            paint1.setColor(getResources().getColor(R.color.main_Accent));
        }
    }

    public RedDotImageView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        Log.e("BOOLEAN VALUE  = ",""+isboolean);

        if(ISBOOLEAN == true) {
        paint1 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(getResources().getColor(android.R.color.transparent));
        }else {
            paint1 = new Paint();
            paint1.setStyle(Paint.Style.FILL);
            paint1.setColor(getResources().getColor(R.color.main_Accent));
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int radius;
        if (measuredHeight > measuredWidth) {
            radius = measuredWidth / redDotSize;

        } else {
            radius = measuredHeight / redDotSize;
        }
        textBounds = new Rect();
            if(ISBOOLEAN == false) {
                canvas.drawCircle(measuredWidth - radius, radius, radius, paint1);
            }else{
                setClear(canvas);
            }


    }


    public void setMsgNum(int msgNum) {
        if(msgNum > 99)
        {
            this.msgNum = 99;
        }
        else if(msgNum <0)
        {
            this.msgNum = 0;
        }else
        {
            this.msgNum = msgNum;
        }
        postInvalidate();
    }

    public void setRedDotSize(int redDotSize) {
        if(redDotSize >10 || redDotSize <=1)
            return;
        this.redDotSize = redDotSize;
        postInvalidate();
    }

    public void setMessageDot(boolean isBoolean){
        if(isBoolean == false) {
            paint1 = new Paint();
            paint1.setStyle(Paint.Style.FILL);
            paint1.setColor(getResources().getColor(R.color.main_Accent));
        }else{
            paint1 = new Paint();
            paint1.setStyle(Paint.Style.FILL);
            paint1.setColor(getResources().getColor(android.R.color.transparent));

        }

    }
    public void setClear(Canvas canvas){
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }


}
