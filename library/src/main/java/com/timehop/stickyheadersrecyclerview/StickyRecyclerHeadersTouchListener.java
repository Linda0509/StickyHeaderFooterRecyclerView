package com.timehop.stickyheadersrecyclerview;

import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class StickyRecyclerHeadersTouchListener implements RecyclerView.OnItemTouchListener {
    private final GestureDetector mTapDetector;
    private final RecyclerView mRecyclerView;
    private final StickyRecyclerHeadersDecoration mDecor;

    public StickyRecyclerHeadersTouchListener(final RecyclerView recyclerView,
                                              final StickyRecyclerHeadersDecoration decor) {
        mTapDetector = new GestureDetector(recyclerView.getContext(), new SingleTapDetector());
        mRecyclerView = recyclerView;
        mDecor = decor;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        boolean tapDetectorResponse = this.mTapDetector.onTouchEvent(e);
        if (tapDetectorResponse) {
            // Don't return false if a single tap is detected
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            int position = mDecor.findHeaderPositionUnder((int) e.getX(), (int) e.getY());
            return false;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent e) { /* do nothing? */ }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
    }

    private class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int position = mDecor.findHeaderPositionUnder((int) e.getX(), (int) e.getY());
            if (position != -1) {
                View headerView = mDecor.getHeaderView(mRecyclerView, position);
                performClick(headerView, e, position);
                return true;
            }
            return false;
        }

        private void performClick(View view, MotionEvent e, int position) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View child = viewGroup.getChildAt(i);
                    performClick(child, e, position);
                }
            }

            containsBounds(view, e, position);
        }

        private View containsBounds(View view, MotionEvent e, int position) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            Rect rect = new Rect();
            view.getHitRect(rect);
            if (view.getVisibility() == View.VISIBLE
                    && view.dispatchTouchEvent(e)
                    && rect.left < rect.right && rect.top < rect.bottom && x >= rect.left && x < rect.right && y >= rect.top) {
                view.setTag(position);
                view.performClick();
                return view;
            }
            return null;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }
}
