package hdychi.hencoderdemo.lrc;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import hdychi.hencoderdemo.CommonData;
import hdychi.hencoderdemo.R;
import hdychi.hencoderdemo.interfaces.OnLrcClickListener;
import hdychi.hencoderdemo.interfaces.OnMoveListner;
import hdychi.hencoderdemo.interfaces.OnSeekToListener;
import hdychi.hencoderdemo.support.MusicUtil;

public class LrcView extends View{
    /**
     * 所有的歌词
     ***/
    private List<LrcRow> mLrcRows;
    /**
     * 无歌词数据的时候 显示的默认文字
     **/
    private static final String DEFAULT_TEXT = "暂时没有歌词";
    /**
     * 默认文字的字体大小
     **/
    private static final float SIZE_FOR_DEFAULT_TEXT = LrcUtil.dip2px(CommonData.context,17);

    /**
     * 画高亮歌词的画笔
     ***/
    private Paint mPaintForHighLightLrc;
    /**
     * 高亮歌词的默认字体大小
     ***/
    private static final float DEFAULT_SIZE_FOR_HIGHT_LIGHT_LRC = LrcUtil.dip2px(CommonData.context,15);
    /**
     * 高亮歌词当前的字体大小
     ***/
    private float mCurSizeForHightLightLrc = DEFAULT_SIZE_FOR_HIGHT_LIGHT_LRC;
    /**
     * 高亮歌词的默认字体颜色
     **/
    private static final int DEFAULT_COLOR_FOR_HIGHT_LIGHT_LRC = 0xffffffff;

    /**
     * 高亮歌词当前的字体颜色
     **/
    private int mCurColorForHightLightLrc = DEFAULT_COLOR_FOR_HIGHT_LIGHT_LRC;

    /**
     * 画其他歌词的画笔
     ***/
    private Paint mPaintForOtherLrc;
    /**
     * 其他歌词的默认字体大小
     ***/
    private static final float DEFAULT_SIZE_FOR_OTHER_LRC = LrcUtil.dip2px(CommonData.context,15);
    /**
     * 其他歌词当前的字体大小
     ***/
    private float mCurSizeForOtherLrc = DEFAULT_SIZE_FOR_OTHER_LRC;
    /**
     * 其他歌词的默认字体颜色
     **/
    private static final int DEFAULT_COLOR_FOR_OTHER_LRC = 0x80ffffff;
    /**
     * 其他歌词当前的字体颜色
     **/
    private int mCurColorForOtherLrc = DEFAULT_COLOR_FOR_OTHER_LRC;


    /**
     * 画时间线的画笔
     ***/
    private Paint mPaintForTimeLine;
    /***
     * 时间线的颜色
     **/
    private static final int DEFAULT_COLOR_FOR_TIME_LINE = 0xff999999;
    /**
     * 时间文字大小
     **/
    private static final int DEFAULT_SIZE_FOR_TIME = LrcUtil.dip2px(CommonData.context,12);
    /**
     * 是否画时间线
     **/
    private boolean mIsDrawTimeLine = false;
    /**
     * 歌词间默认的行距
     **/
    private static final float DEFAULT_PADDING = LrcUtil.dip2px(CommonData.context,30);
    /**
     * 歌词当前的行距
     **/
    private float mCurPadding = DEFAULT_PADDING;

    /**
     * 实现歌词竖直方向平滑滚动的辅助对象
     **/
    private Scroller mScroller;
    /***
     * 移动一句歌词的持续时间
     **/
    private static final int DURATION_FOR_LRC_SCROLL = 2000;
    /***
     * 停止触摸时 如果View需要滚动 时的持续时间
     **/
    private static final int DURATION_FOR_ACTION_UP = 400;

    /**
     * 控制文字缩放的因子
     **/
    private float mCurFraction = 0;
    /**
     * 系统默认的滑动最小距离
     **/
    private int mTouchSlop;
    private Bitmap arrowBitmap;
    /**
     * 总共画多少行
     **/
    private int mTotalDrawRows;
    /**
     * 当前在哪行
     **/
    private int mCurRow;

    private OnLrcClickListener onLrcClickListener;
    private OnMoveListner onMoveListner;
    private OnSeekToListener onSeekToListener;

    public LrcView(Context context) {
        super(context);
        init(context);
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mScroller = new Scroller(getContext());
        mPaintForHighLightLrc = new Paint();
        mPaintForHighLightLrc.setColor(DEFAULT_COLOR_FOR_HIGHT_LIGHT_LRC);
        mPaintForHighLightLrc.setTextSize(DEFAULT_SIZE_FOR_HIGHT_LIGHT_LRC);
        mPaintForHighLightLrc.setAntiAlias(true);

        mPaintForOtherLrc = new Paint();
        mPaintForOtherLrc.setColor(DEFAULT_COLOR_FOR_OTHER_LRC);
        mPaintForOtherLrc.setTextSize(DEFAULT_SIZE_FOR_OTHER_LRC);
        mPaintForOtherLrc.setAntiAlias(true);

        mPaintForTimeLine = new Paint();
        mPaintForTimeLine.setColor(DEFAULT_COLOR_FOR_TIME_LINE);
        mPaintForTimeLine.setTextSize(DEFAULT_SIZE_FOR_TIME);
        mPaintForTimeLine.setAntiAlias(true);

        mTouchSlop = ViewConfiguration.get(getContext())
                .getScaledTouchSlop();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = 30;
        options.inTargetDensity = 30;
        arrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.raw.lrc_arrow,options);
        mLrcRows = new ArrayList<>();
    }


    public void setLrcRows(List<LrcRow> lrcRows) {
        reset();
        mLrcRows = lrcRows;
        invalidate();
    }

    int lastRow = -1;
    public void seekTo(int timeSecs,boolean isFromSelf) {
        if(isFromSelf){
            if(onSeekToListener != null){
                onSeekToListener.onSeekTo(timeSecs);
            }
        }

        for(int i = mLrcRows.size() - 1;i >= 0;i--){
            if(mLrcRows.get(i).getTime() < timeSecs || i == 0){
                mCurRow = i;
                if(!isFromSelf){
                    if(!mScroller.isFinished()){
                        mScroller.forceFinished(true);
                    }
                    scrollTo(getScrollX(),(int)((mCurRow * (mCurSizeForOtherLrc + mCurPadding)) - getHeight() / 2.0f));
                }
                else{
                    smoothScrollTo((int)((mCurRow * (mCurSizeForOtherLrc + mCurPadding)) - getHeight() / 2.0f),DURATION_FOR_LRC_SCROLL);
                }
                float textWidth = mPaintForHighLightLrc.measureText(mLrcRows.get(mCurRow).getContent());
                if(textWidth > getWidth()){
                    if(!isFromSelf){
                        mScroller.forceFinished(true);
                    }
                    if(mCurRow != lastRow){
                        startScrollLrc(getWidth() - textWidth,(long) (mLrcRows.get(mCurRow).getTotalTime() * 0.9));
                    }
                }
                break;
            }
        }
        lastRow = mCurRow;
        invalidate();
    }


    public void reset() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mLrcRows = null;
        scrollTo(getScrollX(), - getHeight() / 2);
        invalidate();
    }
    @Override
    public void onDraw(Canvas canvas){
        if(mLrcRows == null || mLrcRows.size() == 0){
            mPaintForOtherLrc.setTextSize(SIZE_FOR_DEFAULT_TEXT);
            canvas.drawText(DEFAULT_TEXT,(getWidth() - mPaintForOtherLrc.measureText(DEFAULT_TEXT)) / 2,
                    getHeight()/2,mPaintForOtherLrc);
            return;
        }
        mPaintForOtherLrc.setTextSize(DEFAULT_SIZE_FOR_OTHER_LRC);

        for(int i = 0;i < mLrcRows.size();i++){
            String content = mLrcRows.get(i).getContent();
            if(i == mCurRow){
                mPaintForHighLightLrc.setTextSize(mCurSizeForHightLightLrc +
                        (mCurColorForHightLightLrc - mCurSizeForOtherLrc) * mCurFraction);
                float textWidth = mPaintForHighLightLrc.measureText(content);
                if(textWidth <= getWidth()){
                    canvas.drawText(content, (getWidth() - textWidth) / 2,
                            mCurPadding * (i + 1) + mCurSizeForHightLightLrc * i,mPaintForHighLightLrc);
                }
                else{
                    canvas.drawText(content, mCurTextXForHighLightLrc,
                            mCurPadding * (i + 1) + mCurSizeForHightLightLrc * i, mPaintForHighLightLrc);
                }

            }
            else{
                mPaintForOtherLrc.setTextSize(mCurSizeForOtherLrc);
                canvas.drawText(content,(getWidth() - mPaintForOtherLrc.measureText(content)) / 2,
                        mCurPadding * (i + 1) + mCurSizeForOtherLrc * i,mPaintForOtherLrc);
            }
        }
        if(mIsDrawTimeLine){
            float y = getHeight() / 2 + getScrollY();
            canvas.drawBitmap(arrowBitmap,0,y - arrowBitmap.getHeight(),null);
            canvas.drawLine(60,y,getWidth() - 110,y,mPaintForTimeLine);
            canvas.drawText(mLrcRows.get(mCurRow).getTimeStr(),getWidth() - 100,y,mPaintForTimeLine);
        }
    }
    /**
     * 事件的第一次的y坐标
     **/
    private float firstY;
    /**
     * 事件的上一次的y坐标
     **/
    private float lastY;
    private float lastX;
    private boolean canDrag = false;
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(mLrcRows == null || mLrcRows.size() == 0){
            return  false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstY = event.getRawY();
                lastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canDrag) {
                    if (Math.abs(event.getRawY() - firstY) > mTouchSlop && Math.abs(event.getRawY() - firstY) > Math.abs(event.getRawX() - lastX)) {
                        canDrag = true;
                        mIsDrawTimeLine = true;
                        mScroller.forceFinished(true);
                        stopScrollLrc();
                        mCurFraction = 1;
                    }
                    lastY = event.getRawY();
                }

                if (canDrag) {
                    float offset = event.getRawY() - lastY;//偏移量
                    if (getScrollY() - offset < 0) {
                        if (offset > 0) {
                            offset = offset / 3;
                        }
                    }
                    else if (getScrollY() - offset > mLrcRows.size() * (mCurSizeForOtherLrc + mCurPadding) - mCurPadding) {
                        if (offset < 0) {
                            offset = offset / 3;
                        }
                    }
                    scrollBy(getScrollX(), -(int) offset);
                    lastY = event.getRawY();
                    int currentRow = (int) ((getScrollY() + getHeight() / 2.0f)/ (mCurSizeForOtherLrc + mCurPadding));
                    currentRow = Math.min(currentRow, mLrcRows.size() - 1);
                    currentRow = Math.max(currentRow, 0);
                    mCurRow = currentRow;
                    seekTo(mLrcRows.get(currentRow).getTime(), true);
                    return true;
                }
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!canDrag) {
                    if (onLrcClickListener != null) {
                        onLrcClickListener.onClick();
                    }
                }
                else {
                    /*if (onSeekToListener != null && mCurRow != -1) {
                        onSeekToListener.onSeekTo(mLrcRows.get(mCurRow).getTime());
                    }*/
                    canDrag = false;
                    mIsDrawTimeLine = false;
                    invalidate();
                }
                break;
        }
        return true;
    }
    private void smoothScrollTo(int dsty,int duraton){
        int offset = dsty - getScrollY();
        mScroller.startScroll(getScrollX(),getScrollY(),0,offset);
        invalidate();
    }
    @Override
    public void computeScroll(){
        if(!mScroller.isFinished()){
            if(mScroller.computeScrollOffset()){
                int oldY = getScrollY();
                int y = mScroller.getCurrY();
                if(oldY != y && !canDrag){
                    scrollTo(getScrollX(),y);
                }
                invalidate();
            }
        }
    }
    /**
     * 控制歌词水平滚动的属性动画
     ***/
    private ValueAnimator mAnimator;


    /**
     * 开始水平滚动歌词
     *
     * @param endX     歌词第一个字的最终的x坐标
     * @param duration 滚动的持续时间
     */
    private void startScrollLrc(float endX, long duration) {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofFloat(0, endX);
            mAnimator.addUpdateListener(updateListener);
        } else {
            mCurTextXForHighLightLrc = 0;
            mAnimator.cancel();
            mAnimator.setFloatValues(0, endX);
        }
        mAnimator.setDuration(duration);
        mAnimator.setStartDelay(30); //延迟执行属性动画
        mAnimator.start();
    }

    /**
     * 停止歌词的滚动
     */
    private void stopScrollLrc() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }
    /**
     * 高亮歌词当前的其实x轴绘制坐标
     **/
    private float mCurTextXForHighLightLrc;
    /***
     * 监听属性动画的数值值的改变
     */
    ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            mCurTextXForHighLightLrc = (Float) animation.getAnimatedValue();
            invalidate();
        }
    };
    public void setOnMoveListner(OnMoveListner onMoveListner) {
        this.onMoveListner = onMoveListner;
    }

    public void setOnLrcClickListener(OnLrcClickListener onLrcClickListener) {
        this.onLrcClickListener = onLrcClickListener;
    }

    public void setOnSeekToListener(OnSeekToListener onSeekToListener) {
        this.onSeekToListener = onSeekToListener;
    }
    public boolean isDraging(){
        return canDrag;
    }
}
