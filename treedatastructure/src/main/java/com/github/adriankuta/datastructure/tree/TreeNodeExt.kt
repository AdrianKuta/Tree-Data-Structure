package com.github.adriankuta.datastructure.tree

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
inline fun<reified T> tree(root: T, childDeclaration: ChildDeclaration<T>): TreeNode<T> {
    val treeNode = TreeNode(root)
    treeNode.childDeclaration()
    return treeNode
}