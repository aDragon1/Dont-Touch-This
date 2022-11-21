package com.example.donttouchthis

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.TextView

private var collapsed: MutableList<Pair<Int, Int>> = mutableListOf()

class OuterLevelExpandableAdapter(
    private val myContext: Context,
    private val groups: List<Pair<String, List<Pair<String, List<String>>>>>,
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int = groups.size

    override fun getChildrenCount(groupPosition: Int): Int = groups[groupPosition].second.size

    override fun getGroup(groupPosition: Int): Any = groups[groupPosition].second

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        groups[groupPosition].second[childPosition].first

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    @SuppressLint("SetTextI18n")
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        val groupTitle = TextView(myContext)

        groupTitle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        groupTitle.setPadding(80, 20, 0, 0)
        groupTitle.text = "Номер вагона -  ${groups[groupPosition].first}"
        groupTitle.textSize = 20F
        groupTitle.typeface = Typeface.DEFAULT_BOLD



        return groupTitle
    }

    @SuppressLint("SetTextI18n")
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {

        if (collapsed.contains(groupPosition to childPosition) && convertView != null)
            return convertView

        val childExList = CustomExpListView(this.myContext)
        val lParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        childExList.layoutParams = lParams

        childExList.setOnGroupClickListener { _: ExpandableListView, _: View, _: Int, _: Long ->
            collapsed.add(groupPosition to childPosition)
            false
        }

        val childAdapter =
            InnerLevelExpandableAdapter(myContext, groups[groupPosition].second[childPosition])
        childExList.setAdapter(childAdapter)

        return childExList
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean = true
}