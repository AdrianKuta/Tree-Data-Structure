package com.github.adriankuta

import kotlin.jvm.JvmSynthetic

interface ChildDeclarationInterface<T> {

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
    fun child(value: T, childDeclaration: ChildDeclaration<T>? = null): TreeNode<T>
}