package com.github.adriankuta.datastructure.tree

/** Returns the first node (pre-order) whose value matches [predicate], or `null`. Short-circuits. */
fun <T> TreeNode<T>.findNode(predicate: (T) -> Boolean): TreeNode<T>? =
    preOrderSequence().firstOrNull { predicate(it.value) }

/** All nodes (pre-order) whose value matches [predicate]. */
fun <T> TreeNode<T>.filterNodes(predicate: (T) -> Boolean): List<TreeNode<T>> =
    preOrderSequence().filter { predicate(it.value) }.toList()

/** `true` if any node's value matches [predicate]. Short-circuits. */
fun <T> TreeNode<T>.anyNode(predicate: (T) -> Boolean): Boolean =
    preOrderSequence().any { predicate(it.value) }

/** `true` if every node's value matches [predicate]. Short-circuits. */
fun <T> TreeNode<T>.allNodes(predicate: (T) -> Boolean): Boolean =
    preOrderSequence().all { predicate(it.value) }

/** Counts nodes whose value matches [predicate]. */
fun <T> TreeNode<T>.countNodes(predicate: (T) -> Boolean): Int =
    preOrderSequence().count { predicate(it.value) }

/** Folds [operation] over all nodes in pre-order, starting from [initial]. */
fun <T, R> TreeNode<T>.foldNodes(initial: R, operation: (acc: R, node: TreeNode<T>) -> R): R =
    preOrderSequence().fold(initial) { acc, node -> operation(acc, node) }

/**
 * Returns a new tree with the same shape whose values are produced by [transform]. The original is
 * left untouched. Stack-safe (iterative), so it handles arbitrarily deep trees.
 */
fun <T, R> TreeNode<T>.mapValues(transform: (T) -> R): TreeNode<R> {
    val newRoot = TreeNode(transform(value), treeIterator)
    val stack = ArrayDeque<Pair<TreeNode<T>, TreeNode<R>>>()
    stack.addLast(this to newRoot)
    while (stack.isNotEmpty()) {
        val (source, target) = stack.removeLast()
        source.children.forEach { child ->
            val mappedChild = TreeNode(transform(child.value), child.treeIterator)
            target.addChild(mappedChild)
            stack.addLast(child to mappedChild)
        }
    }
    return newRoot
}

/** Returns an independent deep copy of this subtree (same values, same shape, new nodes). */
fun <T> TreeNode<T>.deepCopy(): TreeNode<T> = mapValues { it }

/**
 * Structural equality: `true` when [other] holds the same values in the same shape. Unlike
 * [TreeNode]'s reference equality, this compares the entire subtree. Stack-safe.
 */
fun <T> TreeNode<T>.structurallyEquals(other: TreeNode<T>): Boolean {
    val stack = ArrayDeque<Pair<TreeNode<T>, TreeNode<T>>>()
    stack.addLast(this to other)
    while (stack.isNotEmpty()) {
        val (a, b) = stack.removeLast()
        if (a.value != b.value) return false
        if (a.children.size != b.children.size) return false
        for (i in a.children.indices) {
            stack.addLast(a.children[i] to b.children[i])
        }
    }
    return true
}
