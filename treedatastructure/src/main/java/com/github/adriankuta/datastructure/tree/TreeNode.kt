package com.github.adriankuta.datastructure.tree

open class TreeNode<T>(val value: T) {

    private var parent: TreeNode<T>? = null
    private val children = mutableListOf<TreeNode<T>>()

    fun addChild(child: TreeNode<T>) {
        child.parent = this
        children += child
    }

    fun removeChild(child: TreeNode<T>): Boolean {
        if (children.isEmpty()) {
            return false
        }

        val nestedChildRemoved = children.map {
            it.removeChild(child)
        }.reduce { acc, b -> acc or b }

        return children.remove(child) or nestedChildRemoved
    }

    fun getParent(): TreeNode<T>? = parent

    fun getChildren(): List<TreeNode<T>> = children

    fun size(): Int {
        if (children.isEmpty())
            return 0
        return children.size +
                children.sumBy { it.size() }

    }

    fun depth(): Int {

        val childrenMaxDepth = children.map { it.depth() }.max() ?: 0
        return childrenMaxDepth + 1
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        print(stringBuilder, "", "")
        return stringBuilder.toString()
    }

    private fun print(stringBuilder: StringBuilder, prefix: String, childrenPrefix: String) {
        stringBuilder.append(prefix)
        stringBuilder.append(value)
        stringBuilder.append('\n')
        val childIterator = children.iterator()
        while (childIterator.hasNext()) {
            val node = childIterator.next()
            if(childIterator.hasNext()) {
                node.print(stringBuilder, "$childrenPrefix├── ", "$childrenPrefix│   ")
            } else {
                node.print(stringBuilder, "$childrenPrefix└── ", "$childrenPrefix    ")
            }
        }
    }
}