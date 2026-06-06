package com.github.adriankuta.datastructure.tree.iterators

import com.github.adriankuta.datastructure.tree.TreeNode

/**
 * Tree is iterated by using `Post-order Traversal Algorithm"
 * In post-order traversal, we starting from most left child.
 * First visit all children of parent, then parent.
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
 * Output: 10 5 11 12 13 6 2 3 7 8 9 4 1
 * ```
 */
class PostOrderTreeIterator<T>(root: TreeNode<T>) : Iterator<TreeNode<T>> {

    private val result = ArrayDeque<TreeNode<T>>()

    init {
        // Iterative post-order: pop a node, prepend it to `result`, then push its children
        // left-to-right. Reading `result` front-to-back yields post-order — without the deep
        // recursion that overflowed the stack on degenerate (linear) trees.
        val stack = ArrayDeque<TreeNode<T>>()
        stack.addLast(root)
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            result.addFirst(node)
            node.children.forEach { stack.addLast(it) }
        }
    }

    override fun hasNext(): Boolean = result.isNotEmpty()

    override fun next(): TreeNode<T> = result.removeFirst()
}