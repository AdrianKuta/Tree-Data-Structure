package com.github.adriankuta.datastructure.tree.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.adriankuta.datastructure.tree.TreeNode

/**
 * A sensible default row for a single node in a [LazyTree]. The whole row is clickable to expand or
 * collapse, indentation reflects [depth], and a `▾`/`▸` marker precedes non-leaf nodes.
 *
 * It is intentionally foundation-only (no Material), so using it does not pull a theming dependency
 * into your app. For full control over a node's appearance, use the `LazyTree` overload that takes a
 * `nodeContent` lambda instead.
 *
 * ```
 * LazyTree(root) { node, depth, expanded, toggle ->
 *     TreeNodeRow(node, depth, expanded, toggle)
 * }
 * ```
 *
 * @param node the node to render.
 * @param depth the node's depth in the tree (root = 0), used for indentation.
 * @param expanded whether the node is currently expanded.
 * @param toggle flips this node's expansion state; invoked when the row is clicked.
 * @param modifier the [Modifier] applied to the row.
 * @param indent the horizontal indentation applied per depth level.
 * @param label maps the node's value to the text shown. Defaults to `toString()`.
 */
@Composable
public fun <T> TreeNodeRow(
    node: TreeNode<T>,
    depth: Int,
    expanded: Boolean,
    toggle: () -> Unit,
    modifier: Modifier = Modifier,
    indent: Dp = 16.dp,
    label: (T) -> String = { it.toString() },
) {
    val marker = when {
        node.children.isEmpty() -> ""
        expanded -> "▾ "
        else -> "▸ "
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = toggle)
            .padding(start = indent * depth, top = 8.dp, bottom = 8.dp, end = 8.dp),
    ) {
        BasicText(text = marker + label(node.value))
    }
}
