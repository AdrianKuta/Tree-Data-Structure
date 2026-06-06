package com.github.adriankuta.datastructure.tree.coroutines

import com.github.adriankuta.datastructure.tree.TreeNode
import com.github.adriankuta.datastructure.tree.asSequence
import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

/**
 * Emits this node and all of its descendants as a cold [Flow], traversed in the given [order].
 * Useful for plugging tree traversal into coroutine/Flow pipelines (e.g. in a ViewModel).
 */
public fun <T> TreeNode<T>.asFlow(
    order: TreeNodeIterators = TreeNodeIterators.PreOrder,
): Flow<TreeNode<T>> = asSequence(order).asFlow()

/** Pre-order traversal as a cold [Flow]. */
public fun <T> TreeNode<T>.preOrderFlow(): Flow<TreeNode<T>> = asFlow(TreeNodeIterators.PreOrder)

/** Post-order traversal as a cold [Flow]. */
public fun <T> TreeNode<T>.postOrderFlow(): Flow<TreeNode<T>> = asFlow(TreeNodeIterators.PostOrder)

/** Level-order (breadth-first) traversal as a cold [Flow]. */
public fun <T> TreeNode<T>.levelOrderFlow(): Flow<TreeNode<T>> = asFlow(TreeNodeIterators.LevelOrder)
