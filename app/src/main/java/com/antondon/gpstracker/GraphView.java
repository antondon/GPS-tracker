package com.antondon.gpstracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GraphView extends View {
    private int width;
    private int height;

    private int xCenterSOA, yCenterSOA;
    private int textShiftX, textShiftY, arrowShift, divisionShift, textSize, padding;

    private Paint axisSystemPaint;
    private Paint wayPaint;
    private int axisSize;
    private float trackScale;
    private Pair<Float, Float> previousCoordinate, nextCoordinate;

    private Path way = new Path();

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVariables();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        xCenterSOA = width / 2;
        yCenterSOA = height / 2;
        axisSize = width / 2 - arrowShift - divisionShift - padding;
        trackScale = axisSize / 100f;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawAxisSystem(canvas);
        if (!way.isEmpty())
            canvas.drawPath(way, wayPaint);
    }


    private void initVariables(){
        padding = getResources().getDimensionPixelSize(R.dimen.axis_padding);
        arrowShift = getResources().getDimensionPixelSize(R.dimen.arrow_shift);
        divisionShift = getResources().getDimensionPixelSize(R.dimen.division_shift);
        textShiftX = getResources().getDimensionPixelSize(R.dimen.text_shift_x);
        textShiftY = getResources().getDimensionPixelSize(R.dimen.text_shift_y);
        textSize = getResources().getDimensionPixelSize(R.dimen.text_size);
        int lineSize = getResources().getDimensionPixelSize(R.dimen.line_size);

        axisSystemPaint = new Paint();
        axisSystemPaint.setColor(Color.BLACK);
        axisSystemPaint.setStrokeWidth(lineSize);
        axisSystemPaint.setTextSize(textSize);
        axisSystemPaint.setTextAlign(Paint.Align.CENTER);

        wayPaint = new Paint();
        wayPaint.setColor(Color.BLUE);
        wayPaint.setStyle(Paint.Style.STROKE);
        wayPaint.setStrokeWidth(lineSize);
    }

    private void drawAxisSystem(Canvas canvas){
        //Draw coordinate axis
        int xStartAxisX = padding;
        int yStartAxisX = height / 2;
        int xStopAxisX = width - padding;
        int yStopAxisX = height / 2;

        int xStartAxisY = width / 2;
        int yStartAxisY = height - padding;
        int xStopAxisY = width / 2;
        int yStopAxisY = padding;

        canvas.drawLine(xStartAxisX, yStartAxisX, xStopAxisX, yStopAxisX, axisSystemPaint);
        canvas.drawLine(xStartAxisY, yStartAxisY, xStopAxisY, yStopAxisY, axisSystemPaint);

        //Draw arrows
        canvas.drawLine(xStopAxisX, yStopAxisX, xStopAxisX - arrowShift, yStopAxisX - arrowShift, axisSystemPaint);
        canvas.drawLine(xStopAxisX, yStopAxisX, xStopAxisX - arrowShift, yStopAxisX + arrowShift, axisSystemPaint);
        canvas.drawLine(xStartAxisY, yStopAxisY, xStopAxisY - arrowShift, yStopAxisY + arrowShift, axisSystemPaint);
        canvas.drawLine(xStartAxisY, yStopAxisY, xStopAxisY + arrowShift, yStopAxisY + arrowShift, axisSystemPaint);

        //Draw divisions
        //East
        int startX, startY, stopX, stopY;
        startX = xCenterSOA + axisSize;
        startY = yCenterSOA - divisionShift;
        stopX = xCenterSOA + axisSize;
        stopY = yCenterSOA + divisionShift;

        canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
        canvas.drawText("+100m", stopX, stopY + textShiftY + textSize, axisSystemPaint);
        canvas.drawText("East", stopX, stopY - textShiftY - textSize, axisSystemPaint);

        //West
        startX = xCenterSOA - axisSize;
        startY = yCenterSOA - divisionShift;
        stopX = xCenterSOA - axisSize;
        stopY = yCenterSOA + divisionShift;

        canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
        canvas.drawText("-100m", stopX, stopY + textShiftY + textSize, axisSystemPaint);
        canvas.drawText("West", stopX, stopY - textShiftY - textSize, axisSystemPaint);

        //North
        startX = xCenterSOA - divisionShift;
        startY = yCenterSOA - axisSize;
        stopX = xCenterSOA + divisionShift;
        stopY = yCenterSOA - axisSize;

        canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
        canvas.drawText("+100m", stopX + textShiftX + textSize, stopY + textSize / 2, axisSystemPaint);
        canvas.drawText("North", stopX + textShiftX + textSize, stopY - textSize, axisSystemPaint);

        //South
        startX = xCenterSOA - divisionShift;
        startY = yCenterSOA + axisSize;
        stopX = xCenterSOA + divisionShift;
        stopY = yCenterSOA + axisSize;

        canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
        canvas.drawText("-100m", stopX + textShiftX + textSize, stopY + textSize / 2, axisSystemPaint);
        canvas.drawText("South", stopX + textShiftX + textSize, stopY + textSize * 2, axisSystemPaint);


    }

    public void setCoordinate(double latitude, double longitude) {
        if (previousCoordinate == null){
            previousCoordinate = Pair.create((float)latitude, (float)longitude);
            way.moveTo(xCenterSOA, yCenterSOA);
            return;
        }
        this.nextCoordinate = Pair.create((float)latitude, (float)longitude);
        drawWay();
    }

    private void drawWay(){

        if (previousCoordinate == null || nextCoordinate == null){
            return;
        }
        float offsetX = (nextCoordinate.second - previousCoordinate.second) * trackScale;
        float offsetY = -1f * (nextCoordinate.first - previousCoordinate.first) * trackScale;
        nextCoordinate = null;

        way.lineTo(xCenterSOA + offsetX, yCenterSOA + offsetY);
        invalidate();
    }

    public void clear(){
        previousCoordinate = null;
        nextCoordinate = null;
        way = new Path();
        invalidate();
    }
}

