package com.github.adriankuta.datastructure.tree

import com.github.adriankuta.datastructure.tree.exceptions.TreeNodeException
import com.github.adriankuta.datastructure.tree.iterators.LevelOrderTreeIterator
import com.github.adriankuta.datastructure.tree.iterators.PostOrderTreeIterator
import com.github.adriankuta.datastructure.tree.iterators.PreOrderTreeIterator
import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators
import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators.*
import kotlin.jvm.JvmSynthetic

/**
 * A node in a generic, mutable n-ary tree. Each node holds a [value], a reference to its [parent]
 * and an ordered list of [children].
 *
 * Iterating a node (via [iterator], or the lazy [asSequence]/[preOrderSequence] extensions) visits
 * the node and all of its descendants. Traversal and the [height]/[nodeCount]/[clear] helpers are
 * implemented iteratively, so they are safe on arbitrarily deep trees.
 *
 * **Not thread-safe.** Nodes are mutable ([addChild]/[removeChild]/[clear] mutate the structure and
 * parent pointers). Sharing a tree across threads requires external synchronization, and the tree
 * must not be modified while it is being iterated.
 *
 * Equality is by reference (identity); use the `structurallyEquals` extension to compare two trees
 * by value and shape.
 *
 * @param value the value stored in this node.
 * @param treeIterator the default traversal order used by [iterator]. Prefer the
 *   `asSequence(order)` / `preOrderSequence()` extensions to choose an order without mutating state.
 */
open class TreeNode<T>(val value: T, var treeIterator: TreeNodeIterators = PreOrder) : Iterable<TreeNode<T>>, ChildDeclarationInterface<T> {

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
     * Checks whether the current tree node is the root of the tree
     * @return `true` if the current tree node is root of the tree, `false` otherwise.
     */
    val isRoot: Boolean
        get() = _parent == null

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
        newChild._parent = this
        if (childDeclaration != null)
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
        val removed = child._parent?._children?.remove(child)
        child._parent = null
        return removed ?: false
    }

    /**
     * This function go through tree and counts children. Root element is not counted.
     * @return All child and nested child count.
     */
    fun nodeCount(): Int {
        var count = 0
        val stack = ArrayDeque<TreeNode<T>>()
        stack.addAll(_children)
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            count++
            stack.addAll(node._children)
        }
        return count
    }

    /**
     * @return The number of edges on the longest path between current node and a descendant leaf.
     */
    fun height(): Int {
        var maxDepth = 0
        val stack = ArrayDeque<Pair<TreeNode<T>, Int>>()
        stack.addLast(this to 0)
        while (stack.isNotEmpty()) {
            val (node, depthSoFar) = stack.removeLast()
            if (depthSoFar > maxDepth) maxDepth = depthSoFar
            node._children.forEach { stack.addLast(it to depthSoFar + 1) }
        }
        return maxDepth
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
     * Returns the collection of nodes, which connect the current node
     * with its descendants
     *
     * @param descendant the bottom child node for which the path is calculated
     * @return collection of nodes, which connect the current node with its descendants
     * @throws TreeNodeException exception that may be thrown in case if the
     *                           current node does not have such descendant or if the
     *                           specified tree node is root
     */
    @Throws(TreeNodeException::class)
    fun path(descendant: TreeNode<T>): List<TreeNode<T>> {

        val path = mutableListOf<TreeNode<T>>()
        var node = descendant
        path.add(node)
        while (!node.isRoot) {
            node = node.parent!!
            path.add(node)
            if (node == this)
                return path
        }
        throw TreeNodeException("The specified tree node $descendant is not the descendant of tree node $this")
    }

    /**
     * Remove all children from root and every node in tree.
     */
    fun clear() {
        val all = ArrayDeque<TreeNode<T>>()
        val stack = ArrayDeque<TreeNode<T>>()
        stack.addLast(this)
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            all.addLast(node)
            stack.addAll(node._children)
        }
        all.forEach { node ->
            node._parent = null
            node._children.clear()
        }
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
     * You can change default iterator by changing [treeIterator] property.
     */
    override fun iterator(): Iterator<TreeNode<T>> = when (treeIterator) {
        PreOrder -> PreOrderTreeIterator(this)
        PostOrder -> PostOrderTreeIterator(this)
        LevelOrder -> LevelOrderTreeIterator(this)
    }
}