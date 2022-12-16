package com.github.adriankuta.datastructure.tree

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

    private val stack = ArrayDeque<TreeNode<T>>()

    init {
        stack.addAll(getChildrenStack(root))
    }

    override fun hasNext(): Boolean = stack.isNotEmpty()

    override fun next(): TreeNode<T> {
        return stack.removeFirst()
    }

    private fun getChildrenStack(node: TreeNode<T>): ArrayDeque<TreeNode<T>> {
        val stack = ArrayDeque<TreeNode<T>>()
        if(node.children.isEmpty()) {
            return ArrayDeque(listOf(node))
        }
        node.children.forEach {
            stack.addAll(getChildrenStack(it))
        }
        stack.addLast(node)
        return stack
    }
}