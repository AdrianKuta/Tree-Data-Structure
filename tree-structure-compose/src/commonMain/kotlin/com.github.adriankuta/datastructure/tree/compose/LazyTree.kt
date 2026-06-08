package com.github.adriankuta.datastructure.tree.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.adriankuta.datastructure.tree.TreeNode

/**
 * A lazily-rendered, expand/collapse tree for Compose Multiplatform. Only the currently-visible
 * nodes are composed, so it scales to large trees. Expansion state is remembered internally, keyed
 * by node identity.
 *
 * ```
 * LazyTree(root) { node, depth, expanded, toggle ->
 *     Row(Modifier.padding(start = (depth * 16).dp).clickable(onClick = toggle)) {
 *         if (!node.isLeaf) Text(if (expanded) "▾" else "▸")
 *         Text(node.value.toString())
 *     }
 * }
 * ```
 *
 * @param root the root of the tree to display.
 * @param modifier the [Modifier] applied to the underlying [LazyColumn].
 * @param initiallyExpanded whether nodes start expanded.
 * @param nodeContent renders a single node. Receives the node, its depth (root = 0), whether it is
 *   expanded, and a `toggle` callback that flips this node's expansion state.
 */
@Composable
public fun <T> LazyTree(
    root: TreeNode<T>,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true,
    nodeContent: @Composable (node: TreeNode<T>, depth: Int, expanded: Boolean, toggle: () -> Unit) -> Unit,
) {
    val expansion = remember(root) { mutableStateMapOf<TreeNode<T>, Boolean>() }
    val isExpanded: (TreeNode<T>) -> Boolean = { node -> expansion[node] ?: initiallyExpanded }

    val visible = flattenVisible(root, isExpanded)

    LazyColumn(modifier = modifier) {
        items(visible.size) { index ->
            val (node, depth) = visible[index]
            nodeContent(node, depth, isExpanded(node)) {
                expansion[node] = !isExpanded(node)
            }
        }
    }
}

/**
 * Convenience overload of [LazyTree] that renders each node with the built-in [TreeNodeRow], so the
 * common case is a single call:
 *
 * ```
 * LazyTree(root)
 * ```
 *
 * Use the overload that takes a `nodeContent` lambda when you need full control over a node's look.
 *
 * @param root the root of the tree to display.
 * @param modifier the [Modifier] applied to the underlying [LazyColumn].
 * @param initiallyExpanded whether nodes start expanded.
 * @param indent the horizontal indentation applied per depth level.
 * @param label maps a node's value to the text shown. Defaults to `toString()`.
 */
@Composable
public fun <T> LazyTree(
    root: TreeNode<T>,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true,
    indent: Dp = 16.dp,
    label: (T) -> String = { it.toString() },
) {
    LazyTree(root, modifier, initiallyExpanded) { node, depth, expanded, toggle ->
        TreeNodeRow(node, depth, expanded, toggle, indent = indent, label = label)
    }
}

/**
 * Flattens the tree into the list of currently-visible `(node, depth)` pairs in pre-order, skipping
 * the subtrees of collapsed nodes. Iterative, so it is safe on deep trees.
 */
private fun <T> flattenVisible(
    root: TreeNode<T>,
    isExpanded: (TreeNode<T>) -> Boolean,
): List<Pair<TreeNode<T>, Int>> {
    val result = mutableListOf<Pair<TreeNode<T>, Int>>()
    val stack = ArrayDeque<Pair<TreeNode<T>, Int>>()
    stack.addLast(root to 0)
    while (stack.isNotEmpty()) {
        val (node, depth) = stack.removeLast()
        result.add(node to depth)
        if (isExpanded(node)) {
            node.children.asReversed().forEach { child -> stack.addLast(child to depth + 1) }
        }
    }
    return result
}
