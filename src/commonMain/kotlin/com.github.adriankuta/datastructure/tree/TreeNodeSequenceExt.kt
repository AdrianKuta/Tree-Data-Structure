package com.github.adriankuta.datastructure.tree

import com.github.adriankuta.datastructure.tree.iterators.LevelOrderTreeIterator
import com.github.adriankuta.datastructure.tree.iterators.PostOrderTreeIterator
import com.github.adriankuta.datastructure.tree.iterators.PreOrderTreeIterator
import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators

/**
 * Lazily traverses this subtree in the given [order] as a [Sequence], without forcing the whole
 * traversal up front. Pairs with the Kotlin stdlib, e.g.
 * `root.asSequence().map { it.value }.firstOrNull { it == target }`.
 */
public fun <T> TreeNode<T>.asSequence(
    order: TreeNodeIterators = TreeNodeIterators.PreOrder,
): Sequence<TreeNode<T>> {
    val self = this
    return when (order) {
        TreeNodeIterators.PreOrder -> Sequence { PreOrderTreeIterator(self) }
        TreeNodeIterators.PostOrder -> Sequence { PostOrderTreeIterator(self) }
        TreeNodeIterators.LevelOrder -> Sequence { LevelOrderTreeIterator(self) }
    }
}

/** Lazy pre-order traversal as a [Sequence]. */
public fun <T> TreeNode<T>.preOrderSequence(): Sequence<TreeNode<T>> = asSequence(TreeNodeIterators.PreOrder)

/** Lazy post-order traversal as a [Sequence]. */
public fun <T> TreeNode<T>.postOrderSequence(): Sequence<TreeNode<T>> = asSequence(TreeNodeIterators.PostOrder)

/** Lazy level-order (breadth-first) traversal as a [Sequence]. */
public fun <T> TreeNode<T>.levelOrderSequence(): Sequence<TreeNode<T>> = asSequence(TreeNodeIterators.LevelOrder)
