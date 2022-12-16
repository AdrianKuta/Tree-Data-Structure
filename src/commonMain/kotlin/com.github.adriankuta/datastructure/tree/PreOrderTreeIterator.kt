package com.github.adriankuta.datastructure.tree

/**
 * Tree is iterated by using `Pre-order Traversal Algorithm"
 * The pre-order traversal is a topologically sorted one,
 * because a parent node is processed before any of its child nodes is done.
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
        val node = stack.removeLast()
        node.children
            .asReversed()
            .forEach { stack.addLast(it) }
        return node
    }
}