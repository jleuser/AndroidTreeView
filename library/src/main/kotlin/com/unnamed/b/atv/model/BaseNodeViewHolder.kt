package com.unnamed.b.atv.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unnamed.b.atv.R
import com.unnamed.b.atv.view.AndroidTreeView
import com.unnamed.b.atv.view.TreeNodeWrapperView

/**
 * Converted to Kotlin by jleuser
 */
abstract class BaseNodeViewHolder<in E>(protected var context: Context) {
    var treeView: AndroidTreeView? = null
    var mNode: TreeNode? = null
    private var mView: View? = null
    open var containerStyle: Int = 0
    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    val view: View
        get() {
            if (mView != null) {
                return mView!!
            }
            val nodeView = nodeView
            if(nodeView != null) {
                val nodeWrapperView = TreeNodeWrapperView(nodeView.context, containerStyle)
                nodeWrapperView.insertNodeView(nodeView)
                mView = nodeWrapperView
            }

            return mView!!
        }

    val nodeView: View?
        get() = createNodeView(mNode!!, mNode!!.value as E)

    open val nodeItemsView: ViewGroup
        get() = view.findViewById<View>(R.id.node_items) as ViewGroup

    val isInitialized: Boolean
        get() = mView != null

    abstract fun createNodeView(node: TreeNode, value: E): View?

    open fun toggle(active: Boolean) {
        // empty
    }

    open fun toggleSelectionMode(editModeEnabled: Boolean) {
        // empty
    }

    fun toggleSelection(isSelected: Boolean) {
        //empty
    }
}