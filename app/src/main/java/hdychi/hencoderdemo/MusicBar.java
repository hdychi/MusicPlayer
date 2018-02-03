package hdychi.hencoderdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MusicBar extends View {
    private Context mContext;
    private int progress = 0;
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
        float endX = startX + maxW * progress / 100.0f;
        for(int i = 0;i < progress;i++){
            canvas.drawRect(startX + i * maxW / 100,(float) startY,startX + i * maxW / 100 + maxW / 200,startY + RADIUS / 2.0f,paint);
        }
        paint.setColor(Color.parseColor("#ADADAD"));
        for(int i = progress;i < 100;i++){
            canvas.drawRect(startX + i * maxW / 100,(float) startY,startX + i * maxW / 100 + maxW / 200,startY + RADIUS / 2.0f,paint);
        }
        paint.setColor(Color.WHITE);
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
        final float y = event.getY();
        final int action = event.getAction();
        float maxW = this.getWidth() - 30;
        float circleX= maxW * progress / 100.0f;
        progress = Math.max(0,(int)Math.floor((x - startX) / maxW * 100));
        invalidate();
        if(onMoveListner != null){
            onMoveListner.onMove();
        }
        return true;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
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
