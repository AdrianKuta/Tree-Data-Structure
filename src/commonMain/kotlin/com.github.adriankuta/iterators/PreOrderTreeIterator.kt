package com.github.adriankuta.iterators

import com.github.adriankuta.TreeNode

/**
 * Tree is iterated by using `Pre-order Traversal Algorithm"
 *  1. Check if the current node is empty or null.
 *  2. Display the data part of the root (or current node).
 *  3. Traverse the left subtree by recursively calling the pre-order function.
 *  4. Traverse the right subtree by recursively calling the pre-order function.
 * ```
 * E.g.
 *                    1
 *                  / | \
 *                 /  |   \
 *               2    3     4
 *              / \       / | \
 *             5    6    7  8  9
 *            /   / | \
 *           10  11 12 13
 *
 * Output: 1 2 5 10 6 11 12 13 3 4 7 8 9
 * ```
 */
class PreOrderTreeIterator<T>(root: TreeNode<T>) : Iterator<TreeNode<T>> {

    private val stack = ArrayDeque<TreeNode<T>>()

    init {
        stack.addLast(root)
    }

    override fun hasNext(): Boolean = stack.isNotEmpty()

    override fun next(): TreeNode<T> {
        println(stack)
        val node = stack.removeLast()
        node.children
            .asReversed()
            .forEach { stack.addLast(it) }
        println(stack)
        return node
    }
}