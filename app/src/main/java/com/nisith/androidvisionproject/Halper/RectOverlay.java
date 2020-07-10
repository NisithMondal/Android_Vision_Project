package com.nisith.androidvisionproject.Halper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectOverlay extends GraphicOverlay.Graphic {
    private Paint paint;
    private Rect rect;
    private int rectColor = Color.GREEN;
    private float strokeWidth = 4.0f;

    public RectOverlay(GraphicOverlay graphicOverlay, Rect rect) {
        super(graphicOverlay);
        this.rect = rect;
        paint = new Paint();
        paint.setColor(rectColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(strokeWidth);

    }

    @Override
    public void draw(Canvas canvas) {
        RectF rectF = new RectF(rect);
        rectF.left = translateX(rectF.left);
        rectF.right = translateX(rectF.right);
        rectF.top = translateY(rectF.top);
        rectF.bottom = translateY(rectF.bottom);
        canvas.drawRect(rectF, paint);


    }
}
