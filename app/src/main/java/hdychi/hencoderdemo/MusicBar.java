package hdychi.hencoderdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import hdychi.hencoderdemo.interfaces.OnMoveListner;

public class MusicBar extends View {
    private Context mContext;
    private float progress = 0.0f;
    private final int RADIUS = 25;
    private final int startY = 28;
    private final int startX = 30;
    private OnMoveListner onMoveListner;
    public MusicBar(Context context) {
        super(context);
        this.mContext = context;

    }

    public MusicBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float maxW = this.getWidth() - startX;
        float endX = startX + maxW * progress;
        int percent = (int)Math.ceil(progress * 100);
        for(int i = 0;i < percent;i++){
            canvas.drawRect(startX + i * maxW / 100,(float) startY,startX + i * maxW / 100 + maxW / 200,startY + RADIUS / 2.0f,paint);
        }
        paint.setColor(Color.parseColor("#ADADAD"));
        for(int i = percent;i < 100;i++){
            canvas.drawRect(startX + i * maxW / 100,(float) startY,startX + i * maxW / 100 + maxW / 200,startY + RADIUS / 2.0f,paint);
        }
        paint.setColor(Color.RED);
        canvas.drawRect(endX,startY - RADIUS / 2,endX + maxW / 100,startY + RADIUS,paint);
        Log.i("onDr","调用");

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = this.getWidth();
        int measureHeight = 60;
        measureWidth = resolveSize(measureWidth,widthMeasureSpec);
        measureHeight = resolveSize(measureHeight,heightMeasureSpec);
        setMeasuredDimension(measureWidth,measureHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        float maxW = this.getWidth() - 30;
        progress = (x - startX) / maxW ;
        invalidate();
        if(onMoveListner != null){
            onMoveListner.onMove();
        }
        return true;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public OnMoveListner getOnMoveListner() {
        return onMoveListner;
    }

    public void setOnMoveListner(OnMoveListner onMoveListner) {
        this.onMoveListner = onMoveListner;
    }

}
