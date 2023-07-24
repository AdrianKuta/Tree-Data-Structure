package com.github.adriankuta.datastructure.tree

import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators
import kotlin.jvm.JvmSynthetic

typealias ChildDeclaration<T> = ChildDeclarationInterface<T>.() -> Unit

/**
 * This method can be used to initialize new tree.
 * ```
 * val root = tree("World") { ... }
 * ```
 * @param root Root element of new tree.
 * @see [ChildDeclarationInterface.child]
 */
@JvmSynthetic
inline fun <reified T> tree(
    root: T,
    defaultIterator: TreeNodeIterators = TreeNodeIterators.PreOrder,
    childDeclaration: ChildDeclaration<T>
): TreeNode<T> {
    val treeNode = TreeNode(root, defaultIterator)
    treeNode.childDeclaration()
    return treeNode
}