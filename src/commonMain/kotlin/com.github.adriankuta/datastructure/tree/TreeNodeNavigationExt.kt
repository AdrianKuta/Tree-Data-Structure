package com.github.adriankuta.datastructure.tree

/** `true` when this node has no children. */
val <T> TreeNode<T>.isLeaf: Boolean get() = children.isEmpty()

/** The number of direct children of this node. */
val <T> TreeNode<T>.degree: Int get() = children.size

/** Walks up the parent chain and returns the topmost ancestor (the tree root). */
fun <T> TreeNode<T>.root(): TreeNode<T> {
    var node = this
    var parent = node.parent
    while (parent != null) {
        node = parent
        parent = node.parent
    }
    return node
}

/** The chain of ancestors from the immediate [parent] up to (and including) the root. */
fun <T> TreeNode<T>.ancestors(): List<TreeNode<T>> {
    val result = mutableListOf<TreeNode<T>>()
    var parent = this.parent
    while (parent != null) {
        result.add(parent)
        parent = parent.parent
    }
    return result
}

/** The other children of this node's parent (excludes this node). Empty for the root. */
fun <T> TreeNode<T>.siblings(): List<TreeNode<T>> =
    parent?.children?.filter { it !== this } ?: emptyList()

/** All leaf nodes in this subtree, in pre-order. */
fun <T> TreeNode<T>.leaves(): List<TreeNode<T>> =
    preOrderSequence().filter { it.isLeaf }.toList()

/** All nodes in this subtree except this node, in pre-order. */
fun <T> TreeNode<T>.descendants(): List<TreeNode<T>> =
    preOrderSequence().filter { it !== this }.toList()
