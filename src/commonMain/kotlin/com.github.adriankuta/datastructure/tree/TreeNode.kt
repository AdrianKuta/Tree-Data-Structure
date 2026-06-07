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
public open class TreeNode<T>(public val value: T, public val treeIterator: TreeNodeIterators = PreOrder) : Iterable<TreeNode<T>>, ChildDeclarationInterface<T> {

    private var _parent: TreeNode<T>? = null

    /**
     * The converse notion of a child, an immediate ancestor.
     */
    public val parent: TreeNode<T>?
        get() = _parent

    private val _children = mutableListOf<TreeNode<T>>()

    /**
     * A group of nodes with the same parent.
     */
    public val children: List<TreeNode<T>>
        get() = _children

    /**
     * Checks whether the current tree node is the root of the tree
     * @return `true` if the current tree node is root of the tree, `false` otherwise.
     */
    public val isRoot: Boolean
        get() = _parent == null

    /**
     * Adds [child] as a direct child of this node.
     *
     * @param child a node that is not already attached to a tree. To move a node that already has a
     *   parent, call [detach] on it first.
     * @throws TreeNodeException if [child] already has a parent, or if attaching it here would create
     *   a cycle (i.e. [child] is this node or one of its ancestors).
     */
    public fun addChild(child: TreeNode<T>) {
        validateAttachable(child)
        child._parent = this
        _children.add(child)
    }

    /**
     * Validates that [child] can be attached as a direct child of this node, throwing if it cannot.
     *
     * @param child the node about to be attached.
     * @throws TreeNodeException if [child] already has a parent, or if attaching it here would create
     *   a cycle (i.e. [child] is this node or one of its ancestors).
     */
    private fun validateAttachable(child: TreeNode<T>) {
        if (child._parent != null) {
            throw TreeNodeException("$child already has a parent; call detach() before re-attaching it.")
        }
        if (child === this) {
            throw TreeNodeException("Adding $child here would create a cycle.")
        }
        // Only a node that already has its own subtree can contain `this` and thus form a cycle.
        // Skipping this walk for leaves keeps building deep trees O(n) instead of O(n²).
        if (child._children.isNotEmpty()) {
            var ancestor: TreeNode<T>? = _parent
            while (ancestor != null) {
                if (ancestor === child) {
                    throw TreeNodeException("Adding $child here would create a cycle.")
                }
                ancestor = ancestor._parent
            }
        }
    }

    /**
     * Detaches this node from its parent, removing it from the parent's [children].
     *
     * @return `true` if this node was attached and is now detached; `false` if it was already a root.
     */
    public fun detach(): Boolean {
        val currentParent = _parent ?: return false
        currentParent._children.remove(this)
        _parent = null
        return true
    }

    @JvmSynthetic
    public override fun child(value: T, childDeclaration: ChildDeclaration<T>?): TreeNode<T> {
        val newChild = TreeNode(value)
        newChild._parent = this
        if (childDeclaration != null)
            newChild.childDeclaration()
        _children.add(newChild)
        return newChild
    }

    /**
     * Removes [child] from this node's direct [children], if present.
     *
     * This only removes a *direct* child of the receiver; it does not reach into other nodes. To
     * remove a node from wherever it currently lives, call [detach] on it instead.
     *
     * @return `true` if [child] was a direct child and has been removed; `false` otherwise.
     */
    public fun removeChild(child: TreeNode<T>): Boolean {
        val removed = _children.remove(child)
        if (removed) child._parent = null
        return removed
    }

    /**
     * Inserts [child] as a direct child of this node at the given [index], shifting any existing
     * children at and after [index] one position to the right.
     *
     * @param index the position at which to insert [child]; must be in `0..children.size`.
     * @param child a node that is not already attached to a tree. To move a node that already has a
     *   parent, call [detach] on it first.
     * @throws IndexOutOfBoundsException if [index] is out of range.
     * @throws TreeNodeException if [child] already has a parent, or if attaching it here would create
     *   a cycle (i.e. [child] is this node or one of its ancestors).
     */
    public fun insertChild(index: Int, child: TreeNode<T>) {
        validateAttachable(child)
        child._parent = this
        _children.add(index, child)
    }

    /**
     * Removes the direct child at the given [index], detaching it (its parent becomes `null`).
     *
     * @param index the position of the child to remove; must be in `0 until children.size`.
     * @return the detached child that was at [index].
     * @throws IndexOutOfBoundsException if [index] is out of range.
     */
    public fun removeChildAt(index: Int): TreeNode<T> {
        val removed = _children.removeAt(index)
        removed._parent = null
        return removed
    }

    /**
     * Replaces the direct child at the given [index] with [child], detaching the previous child
     * (its parent becomes `null`).
     *
     * @param index the position of the child to replace; must be in `0 until children.size`.
     * @param child a node that is not already attached to a tree. To move a node that already has a
     *   parent, call [detach] on it first.
     * @return the previous child that was at [index], now detached.
     * @throws IndexOutOfBoundsException if [index] is out of range.
     * @throws TreeNodeException if [child] already has a parent, or if attaching it here would create
     *   a cycle (i.e. [child] is this node or one of its ancestors).
     */
    public fun replaceChild(index: Int, child: TreeNode<T>): TreeNode<T> {
        validateAttachable(child)
        val old = _children[index]
        old._parent = null
        child._parent = this
        _children[index] = child
        return old
    }

    /**
     * Moves an existing direct [child] to a new position within this node's [children].
     *
     * [toIndex] is coerced into the valid range, so out-of-range targets clamp to the first or last
     * position. Because [child] is already a direct child, no re-parenting or cycle check is needed.
     *
     * @param child the node to reorder; must already be a direct child of this node.
     * @param toIndex the target position for [child] after removal, coerced into `0..children.size`.
     * @return `true` if [child] was a direct child and has been moved; `false` otherwise.
     */
    public fun moveChild(child: TreeNode<T>, toIndex: Int): Boolean {
        val from = _children.indexOf(child)
        if (from < 0) return false
        _children.removeAt(from)
        _children.add(toIndex.coerceIn(0, _children.size), child)
        return true
    }

    /**
     * Adds each of [children] as a direct child of this node, in order, validating each one the same
     * way as [addChild].
     *
     * Validation is performed per node as it is added, so if one node fails the children added before
     * it remain attached (the same partial-application behaviour as calling [addChild] in a loop).
     *
     * @param children nodes that are not already attached to a tree.
     * @throws TreeNodeException if any node already has a parent, or if attaching it here would create
     *   a cycle (i.e. it is this node or one of its ancestors).
     */
    public fun addChildren(vararg children: TreeNode<T>) {
        for (child in children) {
            addChild(child)
        }
    }

    /**
     * Sorts this node's direct [children] in place according to the given [comparator]. Only the
     * immediate children are reordered; their subtrees are left untouched.
     *
     * @param comparator the comparator used to order the children.
     */
    public fun sortChildren(comparator: Comparator<TreeNode<T>>) {
        _children.sortWith(comparator)
    }

    /**
     * This function go through tree and counts children. Root element is not counted.
     * @return All child and nested child count.
     */
    public fun nodeCount(): Int {
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
    public fun height(): Int {
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
    public fun depth(): Int {
        var depth = 0
        var tempParent = parent

        while (tempParent != null) {
            depth++
            tempParent = tempParent.parent
        }
        return depth
    }

    /**
     * Returns the chain of nodes from [descendant] up to and including this node, or `null` if
     * [descendant] is not a strict descendant of this node.
     *
     * @param descendant the node to walk up from.
     * @return the path `[descendant, …, this]`, or `null` if [descendant] is the root or is not
     *   located in this node's subtree.
     */
    public fun path(descendant: TreeNode<T>): List<TreeNode<T>>? {
        if (descendant.isRoot) return null
        val path = mutableListOf<TreeNode<T>>()
        var node = descendant
        path.add(node)
        while (!node.isRoot) {
            node = node.parent!!
            path.add(node)
            if (node == this) return path
        }
        return null
    }

    /**
     * Removes every descendant of this node. Afterwards [children] is empty and all former
     * descendants are detached (their parent is `null`). This node itself stays attached to its own
     * parent.
     */
    public fun clear() {
        val descendants = ArrayDeque<TreeNode<T>>()
        val stack = ArrayDeque<TreeNode<T>>()
        stack.addAll(_children)
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            descendants.addLast(node)
            stack.addAll(node._children)
        }
        descendants.forEach { node ->
            node._parent = null
            node._children.clear()
        }
        _children.clear()
    }

    public override fun toString(): String {
        return value.toString()
    }

    public fun prettyString(): String {
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
     * Returns an iterator over this node and its descendants using the default [treeIterator] order.
     * Use [iterator] with an explicit order, or the `asSequence(order)` extension, to traverse in a
     * different order without changing this node's default.
     */
    public override fun iterator(): Iterator<TreeNode<T>> = iterator(treeIterator)

    /** Returns an iterator over this node and its descendants in the given [order]. */
    public fun iterator(order: TreeNodeIterators): Iterator<TreeNode<T>> = when (order) {
        PreOrder -> PreOrderTreeIterator(this)
        PostOrder -> PostOrderTreeIterator(this)
        LevelOrder -> LevelOrderTreeIterator(this)
    }
}