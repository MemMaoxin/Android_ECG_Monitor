package org.maoxin.zkapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class Electrocardiogram extends View {
    private static final String TAG = Electrocardiogram.class.getSimpleName();
    private Handler mHandler;
    private boolean mIsDrawGird;
    private Paint paint;
    private Paint electrocarPaint;
    public Path electrocarPath;
    private int width;
    private int height;
    private int baseLine;
    int bigGirdNum;
    int verticalBigGirdNum;
    private int widthOfSmallGird;
    private List<Float> datas;
    public List<Float> electrocardDatas;
    public int show_index;
    private int maxLevel;
    private int index;
    private int lastSize = 0;
    Runnable runnable;
    final int[] colors = new int[]{Color.argb(0, 0, 0, 0), Color.argb(0, 0, 0, 0), Color.RED};


    public Electrocardiogram(Context context, AttributeSet attrs) {
        super(context, attrs);
        class NamelessClass_1 extends Handler {
            NamelessClass_1() {
            }
        }
        this.mHandler = new NamelessClass_1();
        this.mIsDrawGird = true;
        this.bigGirdNum = 12;
        this.verticalBigGirdNum = 16;
        this.datas = new ArrayList();
        this.electrocardDatas = new ArrayList(816);
        this.index = 0;
        this.init();
    }

    public Electrocardiogram(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        class NamelessClass_1 extends Handler {
            NamelessClass_1() {
            }
        }

        this.mHandler = new NamelessClass_1();
        this.mIsDrawGird = true;
        this.bigGirdNum = 12;
        this.verticalBigGirdNum = 16;
        this.datas = new ArrayList();
        this.electrocardDatas = new ArrayList(816);
        this.index = 0;
        this.show_index=0;
        this.init();
    }

    private void init() {
        this.paint = new Paint();
        this.paint.setStyle(Style.STROKE);
        this.electrocarPaint = new Paint();
        this.electrocarPaint.setColor(-16777216);
        this.electrocarPaint.setStyle(Style.STROKE);
        this.electrocarPaint.setStrokeWidth(3.0F);
        this.electrocarPath = new Path();
        this.setData();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure");
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged");
        this.width = w;
        this.height = h;
        this.widthOfSmallGird = this.width / (this.verticalBigGirdNum * 5);
        this.baseLine = this.height / 2;
        this.maxLevel = this.height / 3;
        this.setData();
    }

    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
//        Log.e(TAG, "onDraw");
        if (this.mIsDrawGird) {
            this.drawGird(canvas);
        }

        this.drawElectrocardiogram(canvas);
    }

    private void drawElectrocardiogram(Canvas canvas) {
        electrocarPath.reset();
        //canvas.drawPath(this.electrocarPath, this.electrocarPaint);
        this.electrocarPath.moveTo(0.0F, (float)this.baseLine - (Float)this.datas.get(0));

        for(int i = 0; i < 816; ++i) {
            float y = (float)this.baseLine - (Float)this.electrocardDatas.get(i);
            if(i>=this.show_index && i<=this.show_index+10)
            {
                continue;
            }
            else if(i==this.show_index+11)
            {
                this.electrocarPath.moveTo((float)((i * this.widthOfSmallGird)/10.2), y);
            }
            else
                this.electrocarPath.lineTo((float)((i * this.widthOfSmallGird)/10.2), y);
        }
        float linearGrad_start=(this.show_index * this.widthOfSmallGird)/20;
        //this.electrocarPaint.setShader(new LinearGradient((linearGrad_start+200)%400,0,linearGrad_start,200,colors, null,Shader.TileMode.MIRROR));
        canvas.drawPath(this.electrocarPath, this.electrocarPaint);
    }

    public void setData() {
        this.generateElectrocar();
        Log.e(TAG, "" + this.datas);
    }

    public void generateElectrocar() {
        int i;
        for(i = 0; i < 816; ++i) {
            this.datas.add(0.0F);
            this.electrocardDatas.add(0.0F);
        }

    }

    private void drawGird(Canvas canvas) {
        //this.paint.setColor(-16711936);
        this.paint.setColor(-65536);
        canvas.drawColor(-1);
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);


        int i;
        for(i = 0; i <= this.verticalBigGirdNum * 5; ++i) {
            if (i % 5 == 0) {
                this.paint.setStrokeWidth(2.0F);
            } else {
                this.paint.setStrokeWidth(1.0F);
            }

            canvas.drawLine((float)((i+0.3) * this.widthOfSmallGird), 0.0F, (float)((i+0.3) * this.widthOfSmallGird), (float)this.height, this.paint);
        }

        for(i = 0; i <= this.bigGirdNum * 5; ++i) {
            if (i % 5 == 0) {
                this.paint.setStrokeWidth(2.0F);
            } else {
                this.paint.setStrokeWidth(1.0F);
            }

            canvas.drawLine(0.0F, (float)(i * this.widthOfSmallGird), (float)this.width, (float)(i * this.widthOfSmallGird), this.paint);
        }

    }
}
