package com.github.adriankuta.datastructure.tree

/**
 * The lowest (deepest) node that is an ancestor of both this node and [other], where every node is
 * considered an ancestor of itself.
 *
 * Nodes are compared by identity (`===`), so this only returns a node when both arguments live in
 * the same tree.
 *
 * @param other the other node to find the common ancestor with.
 * @return the lowest common ancestor, or `null` when the two nodes belong to different trees and
 *   therefore share no common ancestor.
 *
 * Runs in `O(da + db)` time and `O(da + db)` space, where `da`/`db` are the depths of the two nodes.
 */
public fun <T> TreeNode<T>.lowestCommonAncestor(other: TreeNode<T>): TreeNode<T>? {
    // TreeNode has identity equality, so a HashSet gives O(1) identity membership and keeps the
    // overall walk at O(da + db). Collect [other] and its ancestors, then climb from this node
    // upward; the first node already on [other]'s chain is the deepest common ancestor.
    val ancestorsOfOther = HashSet<TreeNode<T>>(other.ancestors())
    ancestorsOfOther.add(other)
    var node: TreeNode<T>? = this
    while (node != null) {
        if (node in ancestorsOfOther) return node
        node = node.parent
    }
    return null
}

/**
 * The number of edges on the shortest path between this node and [other].
 *
 * Computed as `depth() + other.depth() - 2 * lca.depth()`, where `lca` is their
 * [lowestCommonAncestor]. The distance from a node to itself is `0`.
 *
 * @param other the other node to measure the distance to.
 * @return the edge count, or `null` when the two nodes belong to different trees.
 *
 * Runs in `O(da + db)` time, where `da`/`db` are the depths of the two nodes.
 */
public fun <T> TreeNode<T>.distance(other: TreeNode<T>): Int? {
    val lca = lowestCommonAncestor(other) ?: return null
    return depth() + other.depth() - 2 * lca.depth()
}

/**
 * The shortest path of nodes from this node to [other], inclusive of both endpoints.
 *
 * The path ascends from this node up to their [lowestCommonAncestor] and then descends to [other];
 * the common ancestor appears exactly once. When `this === other` the result is `listOf(this)`. When
 * one node is an ancestor of the other the path is simply the chain between them.
 *
 * @param other the node the path ends at.
 * @return the path `[this, …, lca, …, other]`, or `null` when the two nodes belong to different
 *   trees.
 *
 * Runs in `O(da + db)` time and space, where `da`/`db` are the depths of the two nodes.
 */
public fun <T> TreeNode<T>.pathBetween(other: TreeNode<T>): List<TreeNode<T>>? {
    val lca = lowestCommonAncestor(other) ?: return null
    val up = mutableListOf<TreeNode<T>>()
    var node: TreeNode<T> = this
    up.add(node)
    while (node !== lca) {
        node = node.parent!!
        up.add(node)
    }
    val down = mutableListOf<TreeNode<T>>()
    node = other
    down.add(node)
    while (node !== lca) {
        node = node.parent!!
        down.add(node)
    }
    return up + down.dropLast(1).reversed()
}

/**
 * Returns `true` when this subtree contains a node whose value equals [value], including the
 * receiver itself. Values are compared with `==` ([equals]).
 *
 * @param value the value to search for.
 * @return `true` if any node in the pre-order traversal of this subtree holds [value].
 *
 * Runs in `O(n)` time over the `n` nodes of this subtree and stops at the first match.
 */
public fun <T> TreeNode<T>.contains(value: T): Boolean =
    preOrderSequence().any { it.value == value }
