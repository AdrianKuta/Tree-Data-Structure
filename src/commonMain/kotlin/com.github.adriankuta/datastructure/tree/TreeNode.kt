package com.github.adriankuta.datastructure.tree

import com.github.adriankuta.datastructure.tree.TreeNodeIterators.*
import kotlin.jvm.JvmSynthetic

open class TreeNode<T>(val value: T) : Iterable<TreeNode<T>>, ChildDeclarationInterface<T> {

    private var _parent: TreeNode<T>? = null
    /**
     * The converse notion of a child, an immediate ancestor.
     */
    val parent: TreeNode<T>?
        get() = _parent

    private val _children = mutableListOf<TreeNode<T>>()
    /**
     * A group of nodes with the same parent.
     */
    val children: List<TreeNode<T>>
        get() = _children

    /**
     * Choose one of available iterators from [TreeNodeIterators]
     */
    var defaultIterator: TreeNodeIterators = PreOrder

    /**
     * Add new child to current node or root.
     *
     * @param child A node which will be directly connected to current node.
     */
    fun addChild(child: TreeNode<T>) {
        child._parent = this
        _children.add(child)
    }

    @JvmSynthetic
    override fun child(value: T, childDeclaration: ChildDeclaration<T>?): TreeNode<T> {
        val newChild = TreeNode(value)
        if(childDeclaration != null)
            newChild.childDeclaration()
        _children.add(newChild)
        return newChild
    }

    /**
     * Removes a single instance of the specified node from this tree, if it is present.
     *
     * @return `true` if the node has been successfully removed; `false` if it was not present in the tree.
     */
    fun removeChild(child: TreeNode<T>): Boolean {
        println(child.value)
        val removed = child._parent?._children?.remove(child)
        child._parent = null
        return removed ?: false
    }

    /**
     * This function go through tree and counts children. Root element is not counted.
     * @return All child and nested child count.
     */
    fun nodeCount(): Int {
        if (_children.isEmpty())
            return 0
        return _children.size +
                _children.sumOf { it.nodeCount() }
    }

    /**
     * @return The number of edges on the longest path between current node and a descendant leaf.
     */
    fun height(): Int {
        val childrenMaxDepth = _children.map { it.height() }
            .maxOrNull()
            ?: -1 // -1 because this method counts nodes, and edges are always one less then nodes.
        return childrenMaxDepth + 1
    }

    /**
     * Distance is the number of edges along the shortest path between two nodes.
     * @return The distance between current node and the root.
     */
    fun depth(): Int {
        var depth = 0
        var tempParent = parent

        while (tempParent != null) {
            depth++
            tempParent = tempParent.parent
        }
        return depth
    }

    /**
     * Remove all children from root and every node in tree.
     */
    fun clear() {
        _parent = null
        _children.forEach { it.clear() }
        _children.clear()
    }

    override fun toString(): String {
        return value.toString()
    }

    fun prettyString(): String {
        val stringBuilder = StringBuilder()
        print(stringBuilder, "", "")
        return stringBuilder.toString()
    }

    private fun print(stringBuilder: StringBuilder, prefix: String, childrenPrefix: String) {
        stringBuilder.append(prefix)
        stringBuilder.append(value)
        stringBuilder.append('\n')
        val childIterator = _children.iterator()
        while (childIterator.hasNext()) {
            val node = childIterator.next()
            if (childIterator.hasNext()) {
                node.print(stringBuilder, "$childrenPrefix├── ", "$childrenPrefix│   ")
            } else {
                node.print(stringBuilder, "$childrenPrefix└── ", "$childrenPrefix    ")
            }
        }
    }

    /**
     * You can change default iterator by changing [defaultIterator] property.
     */
    override fun iterator(): Iterator<TreeNode<T>> = when (defaultIterator) {
        PreOrder -> PreOrderTreeIterator(this)
        PostOrder -> PostOrderTreeIterator(this)
        LevelOrder -> LevelOrderTreeIterator(this)
    }
}