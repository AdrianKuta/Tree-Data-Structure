package com.github.adriankuta.datastructure.tree

import java.util.*

class TreeNodeIterator<T>(root: TreeNode<T>) : Iterator<TreeNode<T>> {

    private val stack = Stack<TreeNode<T>>()

    init {
        stack.push(root)
    }

    override fun hasNext(): Boolean = !stack.empty()

    override fun next(): TreeNode<T> {
        val node = stack.pop()
        node.children
            .asReversed()
            .forEach { stack.push(it) }
        return node
    }
}