package com.scp.indexablerecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.SectionIndexer;

/**
 * 绘制索引条
 */
public class IndexScroller {

    private float mIndexbarWidth;//索引条的宽度
    private float mIndexbarMargin;//索引条距离右侧的外边距
    private float mPreviewPadding;//预览文本的内边距
    private float mDensity;//当前屏幕密度/160
    private float mScaledDensity;//当前屏幕密度/160(当前字体的尺寸)
    private int mRecyclerViewWidth;//RecyclerView的宽度
    private int mRecyclerViewHeight;//RecyclerView的高度
    private int mCurrentSection = -1;//当前section的索引
    private boolean mIsIndexing = false;//是否在索引条上
    private RecyclerView mRecyclerView = null;//当前封装的RecyclerView
    private SectionIndexer mIndexer = null;//当前封装的SectionIndexer
    private String[] mSections = null;//右侧索引列表文本
    private RectF mIndexbarRect;//索引条的区域

    /**
     * 构造方法
     *
     * @param context      上下文
     * @param recyclerView RecyclerView
     */
    public IndexScroller(Context context, RecyclerView recyclerView) {
        //当前屏幕密度/160
        mDensity = context.getResources().getDisplayMetrics().density;
        //当前屏幕密度/160(当前字体的尺寸)
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        //初始化recyclerView
        mRecyclerView = recyclerView;
        //设置适配器
        setAdapter(mRecyclerView.getAdapter());
        //设置索引条的宽度
        mIndexbarWidth = 20 * mDensity;
        //设置索引条距离屏幕右侧的外边距
        mIndexbarMargin = 10 * mDensity;
        //设置预览文本的内边距
        mPreviewPadding = 5 * mDensity;
    }

    /**
     * 绘制索引条以及预览文本
     *
     * @param canvas Canvas
     */
    public void draw(Canvas canvas) {
        //绘制索引条背景
        Paint indexbarPaint = new Paint();//绘制索引条背景的画笔
        indexbarPaint.setColor(Color.BLACK);//设置颜色
        indexbarPaint.setAlpha(0);//索引条背景的透明度
        indexbarPaint.setAntiAlias(true);//抗锯齿
        //绘制索引条背景
        canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity, indexbarPaint);

        //右侧字母集合不能为空且集合个数必须大于0才绘制
        if (mSections != null && mSections.length > 0) {
            //只有在当前选中字母和手指按下未抬起的情况下才绘制预览背景以及文本
            if (mCurrentSection >= 0 && mIsIndexing) {
                Paint previewPaint = new Paint();//绘制预览背景的画笔
                previewPaint.setColor(Color.BLACK);//设置颜色
                previewPaint.setAlpha(96);//设置透明度
                previewPaint.setAntiAlias(true);//抗锯齿
                previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));//设置阴影

                Paint previewTextPaint = new Paint();//绘制预览文本的画笔
                previewTextPaint.setColor(Color.WHITE);//设置颜色
                previewTextPaint.setAntiAlias(true);//抗锯齿
                previewTextPaint.setTextSize(50 * mScaledDensity);//设置文本大小

                //测量要预览的字符的宽度
                float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
                //预览的大小
                float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
                //预览文本的背景区域
                RectF previewRect = new RectF((mRecyclerViewWidth - previewSize) / 2
                        , (mRecyclerViewHeight - previewSize) / 2
                        , (mRecyclerViewWidth - previewSize) / 2 + previewSize
                        , (mRecyclerViewHeight - previewSize) / 2 + previewSize);
                //绘制预览文本的背景
                canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
                //绘制预览文本
                canvas.drawText(mSections[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1
                        , previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1, previewTextPaint);
            }

            //绘制索引条上的每个字符
            Paint indexPaint = new Paint();//绘制索引条上的每个字符的画笔
            indexPaint.setColor(Color.BLACK);//设置颜色
            indexPaint.setAntiAlias(true);//抗锯齿
            indexPaint.setTextSize(12 * mScaledDensity);//设置文本大小
            //计算每一个Section的高度
            float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length;
            //计算字符上方的内边距
            float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
            //循环绘制所有的Section
            for (int i = 0; i < mSections.length; i++) {
                //设置颜色
                indexPaint.setColor(Color.BLACK);
                //如果为当前选中字符那么将该字符高亮
                if (i == mCurrentSection) {
                    indexPaint.setColor(Color.RED);
                }
                //计算字符的左侧内边距
                float paddingLeft = (mIndexbarWidth - indexPaint.measureText(mSections[i])) / 2;
                //绘制索引条上的字
                canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft
                        , mIndexbarRect.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
            }
        }
    }

    /**
     * 触摸事件
     *
     * @param ev
     * @return
     */
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //如果触摸发生在索引条区域那么将执行绘制以及RecyclerView滚动到相应的item
                if (contains(ev.getX(), ev.getY())) {
                    //表示触摸事件发生在索引条上
                    mIsIndexing = true;
                    //根据当前的Y的值获取当前索引条上对应的索引
                    mCurrentSection = getSectionByPoint(ev.getY());
                    //重绘RecyclerView(必须要有这一步！！！)
                    mRecyclerView.invalidate();
                    //RecyclerView滚动到相对应的索引位置
                    mRecyclerView.getLayoutManager().scrollToPosition(mIndexer.getPositionForSection(mCurrentSection));
                    //表示消费此次触摸事件
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //如果触摸事件发生在索引条上
                if (mIsIndexing) {
                    //判断触摸点是否在索引条上(可以查看contains方法的关于MOVE情况的处理，这里返回true)
                    if (contains(ev.getX(), ev.getY())) {
                        //根据当前的Y的值获取当前索引条上对应的索引
                        mCurrentSection = getSectionByPoint(ev.getY());
                        //重绘RecyclerView(必须要有这一步！！！)
                        mRecyclerView.invalidate();
                        //RecyclerView滚动到相对应的索引位置
                        mRecyclerView.getLayoutManager().scrollToPosition(mIndexer.getPositionForSection(mCurrentSection));
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //如果触摸事件发生在索引条上
                if (mIsIndexing) {
                    //当手指离开屏幕的时候将此记录置为false
                    mIsIndexing = false;
                    //原本是将mCurrentSection置为-1且RecyclerView不重绘
                    //mCurrentSection = -1;

                    //可选操作：以下这两步是为了当用户手指离开屏幕依旧绘制索引条上选中的字符
                    //如果不想这样做可以注释掉下面两行代码并将mCurrentSection = -1
                    //根据当前的Y的值获取当前索引条上对应的索引
                    mCurrentSection = getSectionByPoint(ev.getY());
                    //重绘RecyclerView(必须要有这一步！！！)
                    mRecyclerView.invalidate();
                }
                break;
        }
        return false;
    }

    /**
     * 当RecyclerView大小改变时，此方法是为了应对RecyclerView横竖屏切换
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRecyclerViewWidth = w;//RecyclerView的宽度
        mRecyclerViewHeight = h;//RecyclerView的高度
        //索引条区域
        mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth
                , mIndexbarMargin
                , w - mIndexbarMargin
                , h - mIndexbarMargin);
    }

    /**
     * 设置适配器
     *
     * @param adapter RecyclerView适配器
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        //只有实现了SectionIndexer接口的RecyclerView适配器才可以
        if (adapter instanceof SectionIndexer) {
            mIndexer = (SectionIndexer) adapter;//强转为SectionIndexer
            mSections = (String[]) mIndexer.getSections();//获取索引条上的字符集合
        }
    }


    /**
     * 是否触摸在索引条上
     *
     * @param x
     * @param y
     * @return
     */
    public boolean contains(float x, float y) {
        //仿微信：不在索引条上滑动也可以预览文本
        if (mIsIndexing) {
            return true;
        }
        //是否触摸在索引条上
        return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top + mIndexbarRect.height());
    }

    /**
     * 获取当前触摸的是索引条上的哪一个字符
     *
     * @param y
     * @return
     */
    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.length == 0)
            return 0;
        if (y < mIndexbarRect.top + mIndexbarMargin)
            return 0;
        if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
            return mSections.length - 1;
        return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length));
    }

}