package com.unnamed.b.atv.holder

import android.content.Context
import android.view.View
import android.widget.TextView
import com.unnamed.b.atv.model.BaseNodeViewHolder

import com.unnamed.b.atv.model.TreeNode

/**
 * Created by Bogdan Melnychuk on 2/11/15.
 * Converted to Kotlin by jleuser
 */
class SimpleViewHolder(context: Context) : BaseNodeViewHolder<Any>(context) {

    override fun createNodeView(node: TreeNode, value: Any): View {
        val tv = TextView(context)
        tv.text = value.toString()
        return tv
    }

    override fun toggle(active: Boolean) {}
}
