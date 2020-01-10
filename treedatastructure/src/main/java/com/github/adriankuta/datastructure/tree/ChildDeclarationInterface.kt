package com.github.adriankuta.datastructure.tree

interface ChildDeclarationInterface<T> {

    /**
     * This method is used to easily create child in node.
     * ```
     * val root = treeNode("World") {
     *     treeNode("North America") {
     *         treeNode("USA")
     *     }
     *     treeNode("Europe") {
     *         treeNode("Poland")
     *         treeNode("Germany")
     *     }
     * }
     * ```
     */
    @JvmSynthetic
    fun treeNode(value: T, childDeclaration: ChildDeclaration<T>? = null)
}