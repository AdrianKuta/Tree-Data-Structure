package com.github.adriankuta.datastructure.tree

typealias ChildDeclaration<T> = ChildDeclarationInterface<T>.() -> Unit

/**
 * This method can be used to initialize new tree.
 * ```
 * val root = treeNode("World") { ... }
 * ```
 * @see [ChildDeclarationInterface.treeNode]
 */
@JvmSynthetic
inline fun<reified T> treeNode(value: T, childDeclaration: ChildDeclaration<T>): TreeNode<T> {
    val treeNode = TreeNode(value)
    treeNode.childDeclaration()
    return treeNode
}