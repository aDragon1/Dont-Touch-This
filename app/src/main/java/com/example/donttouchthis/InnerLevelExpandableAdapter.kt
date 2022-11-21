package com.example.donttouchthis

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.LinearLayout
import android.widget.TextView


class InnerLevelExpandableAdapter(
    private val context: Context,
    private val groups: Pair<String, List<String>>,
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int = 1

    override fun getChildrenCount(groupPosition: Int): Int = 1

    override fun getGroup(groupPosition: Int): Any = groups.first

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        groups.second[childPosition]

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

        val groupTitle = TextView(context)

        groupTitle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        groupTitle.setPadding(100, 30, 0, 0)
        groupTitle.text = "Номер колесной пары -  ${groups.first}"
        groupTitle.textSize = 15F
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
        val linearLayout = LinearLayout(context)

        val childText = TextView(context)
        childText.setPadding(110, 10, 0, 0)

        childText.text =
            " Позиция:  ${groups.second[0]} \n Пробег ПР: ${groups.second[1]} \n Общий пробег:  ${groups.second[2]}"
        childText.textSize = 18F
        linearLayout.addView(childText)
        linearLayout.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        return linearLayout
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean = true
}