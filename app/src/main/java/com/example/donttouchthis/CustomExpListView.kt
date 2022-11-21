package com.example.donttouchthis

import android.content.Context
import android.widget.ExpandableListView

class CustomExpListView(context: Context?) : ExpandableListView(context) {

    override fun onMeasure(width: Int, height: Int) {
        val widthMeasureSpec: Int = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST)
        val heightMeasureSpec: Int = MeasureSpec.makeMeasureSpec(20000, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}