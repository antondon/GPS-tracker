package com.antondon.gpstracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
    private int width;
    private int height;

    private int xCenterSOA, yCenterSOA;
    private int padding;
    private Paint axisSystemPaint;
    private Paint pointPaint;
    private int textSize, textShiftX, textShiftY, arrowShift, divisionShift, divisionPadding;
    //1 meter int pixels
    private float trackscale;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVariables();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        xCenterSOA = width / 2;
        yCenterSOA = height / 2;

        /*
        xStartSOA = padding;
        xEndSOA = width - padding;
        yStartSOA = padding;
        yEndSOA = height - padding;
        divisionStep = (width - padding * 2 - arrowShift * 2) / DIVISION_COUNT;
        */

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawAxisSystem(canvas);
    }


    private void initVariables(){
        padding = getResources().getDimensionPixelSize(R.dimen.axis_padding);
        arrowShift = getResources().getDimensionPixelSize(R.dimen.arrow_shift);
        divisionShift = getResources().getDimensionPixelSize(R.dimen.division_shift);
        divisionPadding = getResources().getDimensionPixelOffset(R.dimen.division_padding);
        textShiftX = getResources().getDimensionPixelSize(R.dimen.text_shift_x);
        textShiftY = getResources().getDimensionPixelSize(R.dimen.text_shift_y);
        textSize = getResources().getDimensionPixelSize(R.dimen.text_size);
        int lineSize = getResources().getDimensionPixelSize(R.dimen.line_size);
        trackscale = (width / 2f - arrowShift - divisionShift ) / 100f;

        axisSystemPaint = new Paint();
        axisSystemPaint.setColor(Color.BLACK);
        axisSystemPaint.setStrokeWidth(lineSize);
        axisSystemPaint.setTextSize(textSize);
        axisSystemPaint.setTextAlign(Paint.Align.CENTER);

        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.point_size));

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
        startX = xStopAxisX - arrowShift - divisionPadding;
        startY = yCenterSOA - divisionShift;
        stopX = xStopAxisX - arrowShift - divisionPadding;
        stopY = yCenterSOA + divisionShift;

        canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
        canvas.drawText("+100m", stopX, stopY + textShiftY + textSize, axisSystemPaint);
        canvas.drawText("East", stopX, stopY - textShiftY - textSize, axisSystemPaint);

        //West
        startX = xStartAxisX + arrowShift + divisionPadding;
        startY = yCenterSOA - divisionShift;
        stopX = xStartAxisX + arrowShift + divisionPadding;
        stopY = yCenterSOA + divisionShift;

        canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
        canvas.drawText("-100m", stopX, stopY + textShiftY + textSize, axisSystemPaint);
        canvas.drawText("West", stopX, stopY - textShiftY - textSize, axisSystemPaint);

        //North
        startX = xCenterSOA - divisionShift;
        startY = yStopAxisY + arrowShift + divisionPadding;
        stopX = xCenterSOA + divisionShift;
        stopY = yStopAxisY + arrowShift + divisionPadding;

        canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
        canvas.drawText("+100m", stopX + textShiftX + textSize, stopY + textSize / 2, axisSystemPaint);
        canvas.drawText("North", stopX + textShiftX + textSize, stopY - textSize, axisSystemPaint);

        //South
        startX = xCenterSOA - divisionShift;
        startY = yStartAxisY - arrowShift - divisionPadding;
        stopX = xCenterSOA + divisionShift;
        stopY = yStartAxisY - arrowShift - divisionPadding;

        canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
        canvas.drawText("-100m", stopX + textShiftX + textSize, stopY + textSize / 2, axisSystemPaint);
        canvas.drawText("South", stopX + textShiftX + textSize, stopY + textSize * 2, axisSystemPaint);


    }
}

