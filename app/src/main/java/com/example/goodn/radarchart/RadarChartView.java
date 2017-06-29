package com.example.goodn.radarchart;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static com.example.goodn.radarchart.CommonUtil.decToHex;
import static com.example.goodn.radarchart.CommonUtil.evaluate;

/**
 * Created by goodn on 2017-05-31.
 */

public class RadarChartView extends View {

    private Paint paint;

    private float startX;

    private float startY;

    private float endX;

    private float endY;

    private float x;

    private float y;
    private int widthMode;
    private int heightMode;
    private int widthSize;
    private int heightSize;
    private Paint circlePaint;
    private Paint[] linePaint;
    private String[] strValue;
    private float textEndX;
    private float textEndY;
    private Paint textPaint;
    private Path path;
    private Paint fifthPaint;
    private Paint firstPaint;
    private Paint secondPaint;
    private Paint thirdPaint;
    private Paint fourthPaint;

    private float preX;
    private float preY;
    private float nextX;
    private float nextY;

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private float colorRatio;
    private int preColorValue;
    private int nextColorValue;
    private int chartCount;

    public RadarChartView(Context context) {
        super(context);
        init();
    }

    public RadarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        strValue = new String[]{"소모량", "걸음수", "물섭취", "섭취량", "수면시간"};
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setColor(Color.parseColor("#f4f0ec"));

        paint = new Paint();
        paint.setStrokeWidth(3f);
        paint.setColor(Color.parseColor("#cdc8c4"));

        firstPaint = new Paint();
        secondPaint = new Paint();
        thirdPaint = new Paint();
        fourthPaint = new Paint();
        fifthPaint = new Paint();

        linePaint = new Paint[]{firstPaint, secondPaint, thirdPaint, fourthPaint, fifthPaint, firstPaint};

        firstPaint.setStyle(Paint.Style.STROKE);
        firstPaint.setStrokeCap(Paint.Cap.ROUND);

        secondPaint.setStyle(Paint.Style.STROKE);
        secondPaint.setStrokeCap(Paint.Cap.ROUND);

        thirdPaint.setStyle(Paint.Style.STROKE);
        thirdPaint.setStrokeCap(Paint.Cap.ROUND);

        fourthPaint.setStyle(Paint.Style.STROKE);
        fourthPaint.setStrokeCap(Paint.Cap.ROUND);

        fifthPaint.setStyle(Paint.Style.STROKE);
        fifthPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#70604e"));
        textPaint.setTextSize(30f);

        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMode = MeasureSpec.getMode(widthMeasureSpec);
        heightMode = MeasureSpec.getMode(heightMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 차트 갯수
        chartCount = 5;
        canvas.drawCircle(widthSize / 2, heightSize / 2, widthSize / 4, circlePaint);
        for (int i = 0; i < chartCount + 1; i++) {
            drawCircleLine(canvas, widthSize / 2, heightSize / 2, (360 / chartCount) * i, i);
        }
        circlePaint.setColor(Color.parseColor("#E7DFD8"));
        canvas.drawCircle(widthSize / 2, heightSize / 2, widthSize * 0.075f, circlePaint);

    }

    private void drawCircleLine(Canvas canvas, float x, float y, double angle, int position) {

        float ratio[] = new float[]{0.5f, 0.6f, 0.7f, 0.4f, 0.5f, 0.5f};
        float lineLength = widthSize * 0.33f;
        float textLength = widthSize * 0.4f;
        angle = Math.toRadians(angle);
        startX = x;
        startY = y;
        endX = (float) (x + lineLength * Math.sin(angle));
        endY = (float) (y - lineLength * Math.cos(angle));

        float endPointX;
        float endPointY;

        endPointX = (float) (x + lineLength * ratio[position] * Math.sin(angle));
        endPointY = (float) (y - lineLength * ratio[position] * Math.cos(angle));

        textEndX = (float) (x + textLength * Math.sin(angle));
        textEndY = (float) (y - textLength * Math.cos(angle));

        // 축선과 끝점
        canvas.drawLine(startX, startY, endX, endY, paint);
        canvas.drawCircle(endX, endY, 5f, paint);

        // Draw Value

        if (position == 0) {
            path.moveTo(endPointX, endPointY);
            preX = endPointX;
            preY = endPointY;
            preColorValue = (int) evaluate(ratio[position], ContextCompat.getColor(getContext(), R.color.startGradientColor), ContextCompat.getColor(getContext(), R.color.endGradientColor));
            Log.d("mspark", "preColorValue : " + preColorValue);
            decToHex(preColorValue);
        } else {

            final float x2 = (endPointX + startX) / 2;
            final float y2 = (endPointY + startY) / 2;
            path.quadTo(x2, y2, endPointX, endPointY);
//            path.lineTo(endPointX, endPointY);
            nextX = endPointX;
            nextY = endPointY;
            nextColorValue = (int) evaluate(ratio[position], ContextCompat.getColor(getContext(), R.color.startGradientColor), ContextCompat.getColor(getContext(), R.color.endGradientColor));
            Log.d("mspark", "nextColorValue : " + nextColorValue);

            linePaint[position].setStrokeJoin(Paint.Join.ROUND);
            linePaint[position].setStrokeWidth(15f);
            linePaint[position].setAntiAlias(true);
            linePaint[position].setDither(true);
            linePaint[position].setShader(new LinearGradient(preX, preY, nextX, nextY,
                    Color.parseColor(decToHex(preColorValue)),
                    Color.parseColor(decToHex(nextColorValue)),
                    Shader.TileMode.CLAMP));
//            linePaint[position].setColor(Color.parseColor(decToHex(nextColorValue)));
//            if (position % 2 == 0) {
//
//            } else {
//                linePaint[position].setShader(new LinearGradient(0, 0, nextX, 0,
//                        Color.parseColor(decToHex(nextColorValue)),
//                        Color.parseColor(decToHex(preColorValue)),
//                        Shader.TileMode.CLAMP));
//            }
            canvas.drawPath(path, linePaint[position]);
            path.reset();

            path.moveTo(endPointX, endPointY);
            preX = endPointX;
            preY = endPointY;
            preColorValue = nextColorValue;
        }

        if (position < chartCount) {

            canvas.drawText(strValue[position], textEndX - 50, textEndY, textPaint);
            canvas.drawText(strValue[position], textEndX - 50, textEndY + 50, textPaint);
        }

    }
}
