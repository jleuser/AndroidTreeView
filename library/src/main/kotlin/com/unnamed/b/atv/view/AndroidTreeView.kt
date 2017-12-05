package com.unnamed.b.atv.view

import android.content.Context
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.ScrollView
import com.unnamed.b.atv.R
import com.unnamed.b.atv.holder.SimpleViewHolder
import com.unnamed.b.atv.model.BaseNodeViewHolder
import com.unnamed.b.atv.model.TreeNode
import java.util.*

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 * Converted to Kotlin by jleuser
 */
class AndroidTreeView(context: Context, root: TreeNode? = null) {
    private var mContext: Context = context
    private var mRoot: TreeNode? = root
    private var applyForRoot: Boolean = false
    private var containerStyle = 0
    private var defaultViewHolderClass: Class<out BaseNodeViewHolder<*>> = SimpleViewHolder::class.java
    private var nodeClickListener: TreeNode.TreeNodeClickListener? = null
    private var nodeLongClickListener: TreeNode.TreeNodeLongClickListener? = null

    // TODO fix double iteration over tree
    var isSelectionModeEnabled: Boolean = false
        set(selectionModeEnabled) {
            if (!selectionModeEnabled) {
                deselectAll()
            }
            field = selectionModeEnabled

            val children = mRoot?.getChildren()
            if (children != null) {
                for (node in children) {
                    toggleSelectionMode(node, selectionModeEnabled)
                }
            }
        }
    private var mUseDefaultAnimation = false
    var is2dScrollEnabled = false
        private set
    var isExpansionAutoToggleEnabled = true
        private set
    private var enableSelectionsAutoToggle = true
    private var mRootView: ViewGroup? = null
    private var currentSelectedLeaf: TreeNode? = null
    private var autoScrollToExpandedNode = true
    private var autoScrollToSelectedLeafs = false

    val view: View
        get() = getView(-1)

    val saveState: String
        get() {
            val builder = StringBuilder()
            getSaveState(mRoot, builder)
            if (builder.isNotEmpty()) {
                builder.setLength(builder.length - 1)
            }
            return builder.toString()
        }

    val selected: List<TreeNode>
        get() = if (isSelectionModeEnabled) {
            getSelected(mRoot!!)
        } else {
            ArrayList()
        }

    fun setRoot(mRoot: TreeNode) {
        this.mRoot = mRoot
    }

    fun setAutoScrollToSelectedLeafs(autoScrollToSelectedLeafs: Boolean) {
        this.autoScrollToSelectedLeafs = autoScrollToSelectedLeafs
    }

    fun setAutoScrollToExpandedNode(autoScrollToExpandedNode: Boolean) {
        this.autoScrollToExpandedNode = autoScrollToExpandedNode
    }

    fun setDefaultAnimation(defaultAnimation: Boolean) {
        this.mUseDefaultAnimation = defaultAnimation
    }

    @JvmOverloads
    fun setDefaultContainerStyle(style: Int, applyForRoot: Boolean = false) {
        containerStyle = style
        this.applyForRoot = applyForRoot
    }

    fun setUse2dScroll(use2dScroll: Boolean) {
        this.is2dScrollEnabled = use2dScroll
    }

    fun setExpansionAutoToggle(enableAutoToggle: Boolean) {
        this.isExpansionAutoToggleEnabled = enableAutoToggle
    }

    fun setLeafSelectionAutoToggle(enableSelectionsAutoToggle: Boolean) {
        this.enableSelectionsAutoToggle = enableSelectionsAutoToggle
    }

    fun setDefaultViewHolder(viewHolder: Class<out BaseNodeViewHolder<*>>) {
        defaultViewHolderClass = viewHolder
    }

    fun setDefaultNodeClickListener(listener: TreeNode.TreeNodeClickListener) {
        nodeClickListener = listener
    }

    fun setDefaultNodeLongClickListener(listener: TreeNode.TreeNodeLongClickListener) {
        nodeLongClickListener = listener
    }

    fun expandAll() {
        expandNode(mRoot!!, true)
    }

    fun collapseAll() {
        for (n in mRoot!!.getChildren()) {
            collapseNode(n, true)
        }
    }


    fun getView(style: Int): View {
        mRootView = when {
            style > 0 -> {
                val newContext = ContextThemeWrapper(mContext, style)
                if (is2dScrollEnabled) TwoDScrollView(newContext) else ScrollView(newContext)
            }
            else -> if (is2dScrollEnabled) TwoDScrollView(mContext) else ScrollView(mContext)
        }

        var containerContext = mContext
        if (containerStyle != 0 && applyForRoot) {
            containerContext = ContextThemeWrapper(mContext, containerStyle)
        }
        val viewTreeItems = LinearLayout(containerContext, null, containerStyle)

        viewTreeItems.id = R.id.tree_items
        viewTreeItems.orientation = LinearLayout.VERTICAL
        mRootView!!.addView(viewTreeItems)

        val viewHolder: BaseNodeViewHolder<*> = object : BaseNodeViewHolder<Any>(mContext) {
            override fun createNodeView(node: TreeNode, value: Any): View? {
                return null
            }

            override val nodeItemsView = viewTreeItems
        }

        mRoot!!.setViewHolder(viewHolder)

        expandNode(mRoot!!, false)
        return mRootView!!
    }


    fun expandLevel(level: Int) {
        for (n in mRoot!!.getChildren()) {
            expandLevel(n, level)
        }
    }

    private fun expandLevel(node: TreeNode, level: Int) {
        val lastAutoScrollEnabled = autoScrollToExpandedNode
        autoScrollToExpandedNode = false
        if (node.level <= level) {
            expandNode(node, false)
        }
        for (n in node.getChildren()) {
            expandLevel(n, level)
        }
        autoScrollToExpandedNode = lastAutoScrollEnabled
    }

    fun expandNode(node: TreeNode) {
        expandNode(node, false)
    }

    fun collapseNodeWithSubnodes(node: TreeNode) {
        collapseNode(node, true)
    }

    fun collapseNode(node: TreeNode) {
        collapseNode(node, false)
    }

    fun restoreState(saveState: String) {
        if (!TextUtils.isEmpty(saveState)) {
            collapseAll()
            val openNodesArray = saveState.split(NODES_PATH_SEPARATOR.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val openNodes = HashSet(Arrays.asList<String>(*openNodesArray))
            restoreNodeState(mRoot, openNodes)
        }
    }

    private fun restoreNodeState(node: TreeNode?, openNodes: Set<String>) {
        for (n in node!!.getChildren()) {
            if (openNodes.contains(n.path)) {
                expandNode(n)
                restoreNodeState(n, openNodes)
            }
        }
    }

    private fun getSaveState(root: TreeNode?, sBuilder: StringBuilder) {
        for (node in root!!.getChildren()) {
            if (node.isExpanded()) {
                sBuilder.append(node.path)
                sBuilder.append(NODES_PATH_SEPARATOR)
                getSaveState(node, sBuilder)
            }
        }
    }

    fun toggleNodeExpansion(node: TreeNode) {
        if (node.isExpanded()) {
            collapseNode(node, false)
        } else {
            expandNode(node, false)
        }
    }

    private fun toggleLeafSelection(node: TreeNode) {
        if (node.isLeaf) {
            val leaf = currentSelectedLeaf
            if (leaf != null) {
                selectNode(leaf, false)
            }
            selectNode(node, !node.isSelected)
        }
    }

    private fun collapseNode(node: TreeNode, includeSubnodes: Boolean) {
        node.setExpanded(false)
        val nodeViewHolder = getViewHolderForNode(node)

        if (mUseDefaultAnimation) {
            collapse(nodeViewHolder.nodeItemsView)
        } else {
            nodeViewHolder.nodeItemsView.visibility = View.GONE
        }
        nodeViewHolder.toggle(false)
        if (includeSubnodes) {
            for (n in node.getChildren()) {
                collapseNode(n, includeSubnodes)
            }
        }
    }

    private fun expandNode(node: TreeNode, includeSubnodes: Boolean) {
        node.setExpanded(true)
        val parentViewHolder = getViewHolderForNode(node)
        parentViewHolder.nodeItemsView.removeAllViews()


        parentViewHolder.toggle(true)

        for (n in node.getChildren()) {
            addNode(parentViewHolder.nodeItemsView, n, -1)

            if (n.isExpanded() || includeSubnodes) {
                expandNode(n, includeSubnodes)
            }

        }
        if (mUseDefaultAnimation) {
            expand(parentViewHolder.nodeItemsView)
        } else {
            parentViewHolder.nodeItemsView.visibility = View.VISIBLE
            parentViewHolder.nodeItemsView.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            parentViewHolder.nodeItemsView.requestLayout()
        }

        if (node !== mRoot) {
            if (node.isLeaf && autoScrollToSelectedLeafs || node.isBranch && autoScrollToExpandedNode) {
                scrollToNode(node)
            }
        }

    }

    fun expandNodeIncludingParents(node: TreeNode, autoScroll: Boolean) {
        val parents = getParents(node)
        for (parentNode in parents) {
            val lastAutoScrollEnabled = autoScrollToExpandedNode
            autoScrollToExpandedNode = false
            expandNode(parentNode)
            autoScrollToExpandedNode = lastAutoScrollEnabled
        }
        if (autoScroll) {
            scrollToNode(node)
        }
    }

    private fun getParents(node: TreeNode): List<TreeNode> {
        val parents = ArrayList<TreeNode>()
        var parent: TreeNode? = node
        while (parent !== mRoot && parent != null) {
            parents.add(0, parent)
            parent = parent.parent
        }
        return parents
    }

    fun scrollToNode(node: TreeNode) {
        if (node.isInitialized) {
            if (node.view!!.height == 0) {
                node.view!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        node.view!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                        scrollToNode(node)
                    }
                })
                return
            }
            var yToScroll = node.view!!.y.toInt()
            var parent = node.view!!.parent as ViewGroup
            while (parent !== mRootView) {
                yToScroll += parent.y.toInt()
                parent = parent.parent as ViewGroup
            }
            if (mRootView is TwoDScrollView) {
                (mRootView as TwoDScrollView).smoothScrollTo(0, yToScroll)
            } else if (mRootView is ScrollView) {
                (mRootView as ScrollView).smoothScrollTo(0, yToScroll)
            }
        }
    }

    /**
     * Add the view corresponding to TreeNode to the View Group
     * @param container
     * @param n
     */
    private fun addNode(container: ViewGroup, n: TreeNode, index: Int) {
        val viewHolder = getViewHolderForNode(n)
        val nodeView = viewHolder.view

        if (index == -1)
            container.addView(nodeView)
        else
            container.addView(nodeView, index)

        if (isSelectionModeEnabled) {
            viewHolder.toggleSelectionMode(isSelectionModeEnabled)
        }

        nodeView.setOnClickListener {
            toggleAll(n)
            if (n.getClickListener() != null) {
                val cl = n.getClickListener()
                cl?.onClick(n, n.value)
            } else if (nodeClickListener != null) {
                nodeClickListener!!.onClick(n, n.value)
            }
        }

        nodeView.setOnLongClickListener(View.OnLongClickListener {
            toggleAll(n)
            if (n.getLongClickListener() != null) {
                val lcl = n.getLongClickListener()
                if (lcl != null)
                    return@OnLongClickListener lcl.onLongClick(n, n.value)
            } else if (nodeLongClickListener != null) {
                return@OnLongClickListener nodeLongClickListener!!.onLongClick(n, n.value)
            }
            false
        })
    }

    //------------------------------------------------------------
    //  Selection methods

    fun toggleAll(node: TreeNode) {
        if (isExpansionAutoToggleEnabled) {
            toggleNodeExpansion(node)
        }
        if (enableSelectionsAutoToggle) {
            toggleLeafSelection(node)
        }
    }

    fun <E> getSelectedValues(clazz: Class<E>): List<E> {
        val selected = selected
        val result = selected
                .map { it.value }
                .filter { it.javaClass == clazz }
                .map { it as E }
        return result
    }

    private fun toggleSelectionMode(parent: TreeNode, mSelectionModeEnabled: Boolean) {
        toggleSelectionForNode(parent, mSelectionModeEnabled)
        if (parent.isExpanded()) {
            for (node in parent.getChildren()) {
                toggleSelectionMode(node, mSelectionModeEnabled)
            }
        }
    }

    // TODO Do we need to go through whole tree? Save references or consider collapsed nodes as not selected
    private fun getSelected(parent: TreeNode): List<TreeNode> {
        val result = ArrayList<TreeNode>()
        for (n in parent.getChildren()) {
            if (n.isSelected) {
                result.add(n)
            }
            result.addAll(getSelected(n))
        }
        return result
    }

    fun selectAll(skipCollapsed: Boolean) {
        makeAllSelection(true, skipCollapsed)
    }

    fun deselectAll() {
        makeAllSelection(false, false)
    }

    private fun makeAllSelection(selected: Boolean, skipCollapsed: Boolean) {
        if (isSelectionModeEnabled) {
            for (node in mRoot!!.getChildren()) {
                selectNode(node, selected, skipCollapsed)
            }
        }
    }

    fun selectNode(node: TreeNode, selected: Boolean) {
        if (isSelectionModeEnabled) {
            if (node.isLeaf) {
                if (selected) {
                    currentSelectedLeaf = node
                } else if (node === currentSelectedLeaf) {
                    currentSelectedLeaf = null
                }
            }
            node.isSelected = selected
            toggleSelectionForNode(node, true)
        }
    }

    private fun selectNode(parent: TreeNode, selected: Boolean, skipCollapsed: Boolean) {
        parent.isSelected = selected
        toggleSelectionForNode(parent, true)
        val toContinue = if (skipCollapsed) parent.isExpanded() else true
        if (toContinue) {
            for (node in parent.getChildren()) {
                selectNode(node, selected, skipCollapsed)
            }
        }
    }

    private fun toggleSelectionForNode(node: TreeNode, makeSelectable: Boolean) {
        val holder = getViewHolderForNode(node)
        if (holder.isInitialized) {
            getViewHolderForNode(node).toggleSelectionMode(makeSelectable)
        }
    }

    private fun getViewHolderForNode(node: TreeNode): BaseNodeViewHolder<*> {
        var viewHolder: BaseNodeViewHolder<*>? = node.getViewHolder()
        if (viewHolder == null) {
            try {
                val `object` = defaultViewHolderClass.getConstructor(Context::class.java).newInstance(mContext)
                viewHolder = `object` as BaseNodeViewHolder<*>
                node.setViewHolder(viewHolder)
            } catch (e: Exception) {
                throw RuntimeException("Could not instantiate class " + defaultViewHolderClass)
            }

        }
        if (viewHolder.containerStyle <= 0) {
            viewHolder.containerStyle = containerStyle
        }
        if (viewHolder.treeView == null) {
            viewHolder.treeView = this
        }
        return viewHolder
    }

    /**
     * Adds the node at given position
     * @param parent
     * @param nodeToAdd
     * @param position if -1, node is add in the last
     */
    @JvmOverloads
    fun addNode(parent: TreeNode, nodeToAdd: TreeNode, position: Int = -1) {
        parent.addChild(nodeToAdd, position)
        if (parent.isExpanded()) {
            val parentViewHolder = getViewHolderForNode(parent)
            addNode(parentViewHolder.nodeItemsView, nodeToAdd, position)
        }
    }

    fun removeNode(node: TreeNode) {
        if (node.parent != null) {
            val parent = node.parent
            val index = parent?.deleteChild(node)
            if (index != null) {
                if (index >= 0 && parent.isExpanded()) {
                    val parentViewHolder = getViewHolderForNode(parent)
                    parentViewHolder.nodeItemsView.removeViewAt(index)
                }
            }
        }
    }

    companion object {
        private val NODES_PATH_SEPARATOR = ";"

        private fun expand(v: View) {
            v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight

            v.layoutParams.height = 0
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height = if (interpolatedTime == 1f)
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    else
                        (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        }

        private fun collapse(v: View) {
            val initialHeight = v.measuredHeight

            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        }
    }
}
