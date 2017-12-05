package com.unnamed.b.atv.sample.fragment

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.sample.R
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder
import com.unnamed.b.atv.sample.holder.SelectableHeaderHolder
import com.unnamed.b.atv.view.AndroidTreeView

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 * Converted to Kotlin by jleuser
 */
class TwoDScrollingFragment : Fragment() {
    private var tView: AndroidTreeView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_selectable_nodes, null, false)
        rootView.findViewById<View>(R.id.status).visibility = View.GONE
        val containerView = rootView.findViewById<View>(R.id.container) as ViewGroup

        val root = TreeNode.root()

        val s1 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Folder with very long name ")).setViewHolder(SelectableHeaderHolder(activity))
        val s2 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Another folder with very long name")).setViewHolder(SelectableHeaderHolder(activity))

        fillFolder(s1)
        fillFolder(s2)

        root.addChildren(s1, s2)

        tView = AndroidTreeView(activity, root)
        tView?.setDefaultAnimation(true)
        tView?.setUse2dScroll(true)
        tView?.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
        containerView.addView(tView?.view)

        tView?.expandAll()

        if (savedInstanceState != null) {
            val state = savedInstanceState.getString(STATE_SAVE_KEY)
            if (!TextUtils.isEmpty(state)) {
                tView!!.restoreState(state!!)
            }
        }
        return rootView
    }

    private fun fillFolder(folder: TreeNode) {
        var currentNode = folder
        for (i in 0..9) {
            val file = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, NAME)).setViewHolder(SelectableHeaderHolder(activity))
            currentNode.addChild(file)
            currentNode = file
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SAVE_KEY, tView!!.saveState)
    }

    companion object {
        private val NAME = "Very long name for folder"
        private val STATE_SAVE_KEY = "tState"
    }
}
