package com.unnamed.b.atv.model

import android.view.View
import java.util.*

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 * Converted to Kotlin by jleuser
 */
class TreeNode(val value: Any) {

    var id: Int = 0
        private set
    private var mLastId: Int = 0
    var parent: TreeNode? = null
        private set
    private var mSelected: Boolean = false
    var isSelectable = true
    private val children: MutableList<TreeNode> = ArrayList()
    private var mViewHolder: BaseNodeViewHolder<*>? = null
    private var mClickListener: TreeNodeClickListener? = null
    private var mLongClickListener: TreeNodeLongClickListener? = null
    private var mExpanded: Boolean = false

    val isLeaf: Boolean
        get() = size() == 0

    val isBranch: Boolean
        get() = size() > 0

    var isSelected: Boolean
        get() = isSelectable && mSelected
        set(selected) {
            mSelected = selected
            if (mViewHolder != null) {
                mViewHolder!!.toggleSelection(mSelected)
            }
        }

    val path: String
        get() {
            val path = StringBuilder()
            var node: TreeNode? = this
            while (node!!.parent != null) {
                path.append(node.id)
                node = node.parent
                if (node!!.parent != null) {
                    path.append(NODES_ID_SEPARATOR)
                }
            }
            return path.toString()
        }


    val level: Int
        get() {
            var level = 0
            var root: TreeNode? = this
            while (root!!.parent != null) {
                root = root.parent
                level++
            }
            return level
        }

    val isLastChild: Boolean
        get() {
            if (!isRoot) {
                val parentSize = parent!!.children.size
                if (parentSize > 0) {
                    val parentChildren = parent!!.children
                    return parentChildren[parentSize - 1].id == id
                }
            }
            return false
        }

    val isInitialized: Boolean
        get() = if (mViewHolder != null) mViewHolder!!.isInitialized else false

    val view: View?
        get() = if (mViewHolder != null) mViewHolder!!.view else null

    val isFirstChild: Boolean
        get() {
            if (!isRoot) {
                val parentChildren = parent!!.children
                return parentChildren[0].id == id
            }
            return false
        }

    val isRoot: Boolean
        get() = parent == null

    val root: TreeNode
        get() {
            var root: TreeNode? = this
            while (root!!.parent != null) {
                root = root.parent
            }
            return root
        }

    private fun generateId(): Int {
        return ++mLastId
    }

    fun getChildren(): List<TreeNode> {
        return Collections.unmodifiableList(children)
    }

    /**
     * Adds a child node at give position
     * @param childNode
     * @param position First position is 0. If -1, child is inserted at the end
     * @return The current [TreeNode] (*not* the child node!)
     */
    fun addChild(childNode: TreeNode, position: Int = -1): TreeNode {
        var pos = position
        childNode.parent = this
        childNode.id = generateId()

        if (pos == -1 || pos > children.size)
            pos = children.size

        children.add(pos, childNode)
        return this
    }

    /**
     * Adds the child nodes
     * @return The current [TreeNode] (*not* any of the child nodes!)
     */
    fun addChildren(vararg nodes: TreeNode): TreeNode {
        for (n in nodes) {
            addChild(n)
        }
        return this
    }

    /**
     * Adds the child nodes
     * @return The current [TreeNode] (*not* any of the child nodes!)
     */
    fun addChildren(nodes: Collection<TreeNode>): TreeNode {
        for (n in nodes) {
            addChild(n)
        }
        return this
    }

    fun deleteChild(child: TreeNode): Int {
        for ((index, tmpChild) in children.withIndex()) {
            if (tmpChild.id == child.id) {
                children.removeAt(index)
                return index
            }
        }
        return -1
    }

    fun size(): Int {
        return children.size
    }

    fun isExpanded(): Boolean {
        return mExpanded
    }

    fun setExpanded(expanded: Boolean): TreeNode {
        mExpanded = expanded
        return this
    }

    fun setViewHolder(viewHolder: BaseNodeViewHolder<*>?): TreeNode {
        mViewHolder = viewHolder
        if (viewHolder != null) {
            viewHolder.mNode = this
        }
        return this
    }

    fun setClickListener(listener: TreeNodeClickListener): TreeNode {
        mClickListener = listener
        return this
    }

    fun getClickListener(): TreeNodeClickListener? {
        return this.mClickListener
    }

    fun setLongClickListener(listener: TreeNodeLongClickListener): TreeNode {
        mLongClickListener = listener
        return this
    }

    fun getLongClickListener(): TreeNodeLongClickListener? {
        return mLongClickListener
    }

    fun getViewHolder(): BaseNodeViewHolder<*>? {
        return mViewHolder
    }

    fun hasView(): Boolean? {
        return view != null
    }

    interface TreeNodeClickListener {
        fun onClick(node: TreeNode, value: Any)
    }

    interface TreeNodeLongClickListener {
        fun onLongClick(node: TreeNode, value: Any): Boolean
    }

    companion object {
        val NODES_ID_SEPARATOR = ":"

        fun root(): TreeNode {
            val root = TreeNode(Any())
            root.isSelectable = false
            return root
        }
    }
}
