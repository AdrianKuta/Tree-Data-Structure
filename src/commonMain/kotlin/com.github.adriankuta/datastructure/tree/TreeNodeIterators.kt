package com.github.adriankuta.datastructure.tree

/**
 * @see PreOrder
 * @see PostOrder
 * @see LevelOrder
 */
enum class TreeNodeIterators {
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
    PreOrder,

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
    PostOrder,

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
    LevelOrder
}