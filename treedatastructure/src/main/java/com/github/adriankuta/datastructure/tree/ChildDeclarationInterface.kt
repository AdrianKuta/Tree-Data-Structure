package com.github.adriankuta.datastructure.tree

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
     */
    @JvmSynthetic
    fun child(value: T, childDeclaration: ChildDeclaration<T>? = null)
}