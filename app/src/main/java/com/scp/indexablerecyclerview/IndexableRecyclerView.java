package com.scp.indexablerecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

//字母导航控件
public class IndexableRecyclerView extends RecyclerView {

    //索引条
    private IndexScroller mScroller = null;
    //手势
    private GestureDetector mGestureDetector = null;

    public IndexableRecyclerView(Context context) {
        this(context, null);
    }

    public IndexableRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new IndexScroller(getContext(),this);
        mGestureDetector = new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener());
    }


    //用于绘制右侧的索引条
    @Override
    public void draw(Canvas c) {
        super.draw(c);
        if (mScroller != null) {
            //绘制右侧的索引条
            mScroller.draw(c);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //如果mScroller自己来处理触摸事件，该方法返回true
        if (mScroller != null && mScroller.onTouchEvent(ev)) {
            return true;
        }
        mGestureDetector.onTouchEvent(ev);

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScroller.contains(ev.getX(), ev.getY()))
            return true;

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (mScroller != null)
            mScroller.setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mScroller != null)
            mScroller.onSizeChanged(w, h, oldw, oldh);
    }

}