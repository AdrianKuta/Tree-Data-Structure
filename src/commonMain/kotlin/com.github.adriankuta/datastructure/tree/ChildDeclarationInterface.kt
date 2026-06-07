package com.github.adriankuta.datastructure.tree

import kotlin.jvm.JvmSynthetic

public interface ChildDeclarationInterface<T> {

    /**
     * This method is used to easily create child in node.
     * ```
     * val root = tree("World") {
     *     child("North America") {
     *         child("USA")
     *     }
     *     child("Europe") {
     *         child("Poland")
     *         child("Germany")
     *     }
     * }
     * ```
     * @return New created TreeNode.
     */
    @JvmSynthetic
    public fun child(value: T, childDeclaration: ChildDeclaration<T>? = null): TreeNode<T>
}