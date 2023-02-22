package com.github.adriankuta.datastructure.tree.iterators

import com.github.adriankuta.datastructure.tree.TreeNode

/**
 * Tree is iterated by using `Level-order Traversal Algorithm"
 * In level-order traversal we iterating nodes level by level,
 * starting from root, and going deeper and deeper in tree.
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
 * Output: 1 2 3 4 5 6 7 8 9 10 11 12 13
 * ```
 */
class LevelOrderTreeIterator<T>(root: TreeNode<T>) : Iterator<TreeNode<T>> {

    private val stack = ArrayDeque<TreeNode<T>>()

    init {
        stack.addLast(root)
    }

    override fun hasNext(): Boolean = stack.isNotEmpty()

    override fun next(): TreeNode<T> {
        val node =  stack.removeFirst()
        node.children
            .forEach { stack.addLast(it) }
        return node
    }
}