package com.github.adriankuta.datastructure.tree

typealias ChildDeclaration<T> = ChildDeclarationInterface<T>.() -> Unit

@JvmSynthetic
inline fun<reified T> treeNode(value: T, childDeclaration: ChildDeclaration<T>): TreeNode<T> {
    val treeNode = TreeNode(value)
    treeNode.childDeclaration()
    return treeNode
}

interface ChildDeclarationInterface<T> {

    fun child(value: T, childDeclaration: ChildDeclaration<T>? = null)
}