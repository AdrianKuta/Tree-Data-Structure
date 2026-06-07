package com.github.adriankuta.datastructure.tree.immutable

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/**
 * A node in an immutable, persistent n-ary tree. Each node holds a [value] and an ordered
 * [PersistentList] of [children]; nodes never carry a parent back-reference, so a subtree is a
 * self-contained, acyclic value.
 *
 * Every mutating operation ([addChild], [removeChild], [mapValues]) returns a **new** root and
 * leaves the receiver untouched. Subtrees that are not on the path of the change are reused as the
 * same instances (structural sharing), so updates are cheap and old roots stay valid.
 *
 * Equality is value-based: two nodes are equal when their [value]s and [children] are equal
 * (a `data class`), independent of identity.
 *
 * @param value the value stored in this node.
 * @param children the ordered, persistent list of child subtrees.
 */
public data class ImmutableTreeNode<T>(
    public val value: T,
    public val children: PersistentList<ImmutableTreeNode<T>> = persistentListOf(),
) {

    /**
     * Returns a new node with [child] appended to this node's [children]. The receiver and every
     * existing child subtree are reused unchanged (structural sharing).
     *
     * @param child the subtree to append.
     * @return a new [ImmutableTreeNode] with [child] added; the receiver is not modified.
     */
    public fun addChild(child: ImmutableTreeNode<T>): ImmutableTreeNode<T> =
        copy(children = children.add(child))

    /**
     * Returns a new node with the first occurrence of [child] removed from this node's direct
     * [children], compared by value-based equality. If [child] is not a direct child, a structurally
     * equal new node is returned. The receiver is never modified.
     *
     * @param child the direct child subtree to remove.
     * @return a new [ImmutableTreeNode] without [child]; the receiver is not modified.
     */
    public fun removeChild(child: ImmutableTreeNode<T>): ImmutableTreeNode<T> =
        copy(children = children.remove(child))

    /**
     * Returns a new tree of the same shape with every node's value transformed by [transform].
     * The receiver is not modified.
     *
     * @param transform maps each node's value of type [T] to a value of type [R].
     * @return a new [ImmutableTreeNode] of type [R] mirroring this tree's structure.
     */
    public fun <R> mapValues(transform: (T) -> R): ImmutableTreeNode<R> =
        ImmutableTreeNode(transform(value), children.map { it.mapValues(transform) }.toPersistentList())
}

/**
 * Returns this subtree's nodes in pre-order (the receiver first, then each child subtree in order).
 * Implemented iteratively, so it is safe on arbitrarily deep trees.
 *
 * @return the nodes of this subtree in pre-order, starting with the receiver.
 */
public fun <T> ImmutableTreeNode<T>.preOrder(): List<ImmutableTreeNode<T>> {
    val result = mutableListOf<ImmutableTreeNode<T>>()
    val stack = ArrayDeque<ImmutableTreeNode<T>>()
    stack.addLast(this)
    while (stack.isNotEmpty()) {
        val node = stack.removeLast()
        result.add(node)
        node.children.asReversed().forEach { stack.addLast(it) }
    }
    return result
}

/**
 * Returns this subtree's nodes in post-order (each child subtree in order, then the receiver last).
 * Implemented iteratively, so it is safe on arbitrarily deep trees.
 *
 * @return the nodes of this subtree in post-order, ending with the receiver.
 */
public fun <T> ImmutableTreeNode<T>.postOrder(): List<ImmutableTreeNode<T>> {
    val result = ArrayDeque<ImmutableTreeNode<T>>()
    val stack = ArrayDeque<ImmutableTreeNode<T>>()
    stack.addLast(this)
    while (stack.isNotEmpty()) {
        val node = stack.removeLast()
        result.addFirst(node)
        node.children.forEach { stack.addLast(it) }
    }
    return result.toList()
}

/**
 * Returns this subtree's nodes in level-order (breadth-first: the receiver, then its children, then
 * their children, and so on). Implemented iteratively, so it is safe on arbitrarily deep trees.
 *
 * @return the nodes of this subtree in breadth-first order, starting with the receiver.
 */
public fun <T> ImmutableTreeNode<T>.levelOrder(): List<ImmutableTreeNode<T>> {
    val result = mutableListOf<ImmutableTreeNode<T>>()
    val queue = ArrayDeque<ImmutableTreeNode<T>>()
    queue.addLast(this)
    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        result.add(node)
        node.children.forEach { queue.addLast(it) }
    }
    return result
}

/**
 * Counts all descendants of this node; the receiver itself is not counted (matching the core
 * `TreeNode.nodeCount`). Implemented iteratively, so it is safe on arbitrarily deep trees.
 *
 * @return the number of descendant nodes (children and nested children) of this node.
 */
public fun <T> ImmutableTreeNode<T>.nodeCount(): Int {
    var count = 0
    val stack = ArrayDeque<ImmutableTreeNode<T>>()
    stack.addAll(children)
    while (stack.isNotEmpty()) {
        val node = stack.removeLast()
        count++
        stack.addAll(node.children)
    }
    return count
}

/**
 * Returns the number of edges on the longest path between this node and a descendant leaf (0 for a
 * leaf). Implemented iteratively, so it is safe on arbitrarily deep trees.
 *
 * @return the height of this subtree, measured in edges.
 */
public fun <T> ImmutableTreeNode<T>.height(): Int {
    var maxDepth = 0
    val stack = ArrayDeque<Pair<ImmutableTreeNode<T>, Int>>()
    stack.addLast(this to 0)
    while (stack.isNotEmpty()) {
        val (node, depthSoFar) = stack.removeLast()
        if (depthSoFar > maxDepth) maxDepth = depthSoFar
        node.children.forEach { stack.addLast(it to depthSoFar + 1) }
    }
    return maxDepth
}
