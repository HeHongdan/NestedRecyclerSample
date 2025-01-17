package com.example.nestedrecyclersample.utils

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * 处理(横向)滑动冲突。
 *
 * https://bladecoder.medium.com/fixing-recyclerview-nested-scrolling-in-opposite-direction-f587be5c1a04
 */
fun RecyclerView.enforceSingleScrollDirection() {
    val enforcer = SingleScrollDirectionEnforcer()
    addOnItemTouchListener(enforcer)
    /**
     * 2/4RV 水平方向与垂直方向滑动的冲突解决
     */
    addOnScrollListener(enforcer)
}

public class SingleScrollDirectionEnforcer : RecyclerView.OnScrollListener(), RecyclerView.OnItemTouchListener {

    private var scrollState = RecyclerView.SCROLL_STATE_IDLE
    private var scrollPointerId = -1
    private var initialTouchX = 0
    private var initialTouchY = 0
    private var dx = 0
    private var dy = 0

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrollPointerId = e.getPointerId(0)
                initialTouchX = (e.x + 0.5f).toInt()
                initialTouchY = (e.y + 0.5f).toInt()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = e.actionIndex
                scrollPointerId = e.getPointerId(actionIndex)
                initialTouchX = (e.getX(actionIndex) + 0.5f).toInt()
                initialTouchY = (e.getY(actionIndex) + 0.5f).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val index = e.findPointerIndex(scrollPointerId)
                if (index >= 0 && scrollState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    val x = (e.getX(index) + 0.5f).toInt()
                    val y = (e.getY(index) + 0.5f).toInt()
                    dx = x - initialTouchX
                    dy = y - initialTouchY
                }
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val oldState = scrollState
        scrollState = newState
        if (oldState == RecyclerView.SCROLL_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            recyclerView.layoutManager?.let { layoutManager ->
                val canScrollHorizontally = layoutManager.canScrollHorizontally()
                val canScrollVertically = layoutManager.canScrollVertically()
                if (canScrollHorizontally != canScrollVertically) {
                    if ((canScrollHorizontally && Math.abs(dy) > Math.abs(dx))
                        || (canScrollVertically && Math.abs(dx) > Math.abs(dy))) {
                        recyclerView.stopScroll()
                    }
                }
            }
        }
    }
}