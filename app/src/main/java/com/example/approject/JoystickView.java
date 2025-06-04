package com.example.approject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    private Paint basePaint, knobPaint;
    private float baseRadius, knobRadius;
    private PointF center, touch;

    private JoystickListener joystickListener;

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystick();
    }

    private void initJoystick() {
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(Color.parseColor("#10000000"));
        basePaint.setStyle(Paint.Style.FILL);

        knobPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        knobPaint.setColor(Color.parseColor("#ddD9D9D9"));
        knobPaint.setStyle(Paint.Style.FILL);

        center = new PointF();
        touch = new PointF();
    }

    public void setJoystickListener(JoystickListener listener) {
        this.joystickListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        center.set(w / 2f, h / 2f);
        touch.set(center.x, center.y);
        baseRadius = Math.min(w, h) / 2.5f;
        knobRadius = Math.min(w, h) / 5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, baseRadius, basePaint);
        canvas.drawCircle(touch.x, touch.y, knobRadius, knobPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float dx = event.getX() - center.x;
        float dy = event.getY() - center.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < baseRadius) {
            touch.set(event.getX(), event.getY());
        } else {
            float ratio = baseRadius / (float) distance;
            touch.set(center.x + dx * ratio, center.y + dy * ratio);
        }

        invalidate();

        if (joystickListener != null) {
            float xPercent = (touch.x - center.x) / baseRadius;
            float yPercent = (touch.y - center.y) / baseRadius;
            joystickListener.onMove(xPercent, yPercent);
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            touch.set(center.x, center.y);
            invalidate();
            if (joystickListener != null) {
                joystickListener.onMove(0, 0);
            }
        }

        return true;
    }

    public interface JoystickListener {
        void onMove(float xPercent, float yPercent); // -1.0 ~ 1.0 사이 값
    }
}
