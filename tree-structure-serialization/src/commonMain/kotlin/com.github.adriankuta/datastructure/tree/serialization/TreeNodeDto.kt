package com.github.adriankuta.datastructure.tree.serialization

import com.github.adriankuta.datastructure.tree.TreeNode
import kotlinx.serialization.Serializable

/**
 * A serializable, acyclic view of a [TreeNode] subtree. [TreeNode] itself holds a back-reference to
 * its parent (a cycle), so it cannot be `@Serializable` directly — convert to/from this DTO instead.
 *
 * ```
 * val json = Json.encodeToString(tree.toDto())
 * val restored = Json.decodeFromString<TreeNodeDto<String>>(json).toTreeNode()
 * ```
 */
@Serializable
public data class TreeNodeDto<T>(
    public val value: T,
    public val children: List<TreeNodeDto<T>> = emptyList(),
)

/** Converts this subtree into a serializable [TreeNodeDto], preserving values and shape. */
public fun <T> TreeNode<T>.toDto(): TreeNodeDto<T> =
    TreeNodeDto(value, children.map { it.toDto() })

/** Rebuilds a mutable [TreeNode] tree from this DTO. */
public fun <T> TreeNodeDto<T>.toTreeNode(): TreeNode<T> {
    val node = TreeNode(value)
    children.forEach { node.addChild(it.toTreeNode()) }
    return node
}
