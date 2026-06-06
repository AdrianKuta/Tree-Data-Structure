package com.github.adriankuta.datastructure.tree

import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A deep, degenerate (linear) tree must not overflow the call stack. These tests build a chain
 * thousands of nodes deep — recursive implementations of [TreeNode.height], [TreeNode.nodeCount]
 * and the post-order iterator blow the stack here, so they pin the iterative rewrites.
 */
class TreeNodeStackSafetyTest {

    private val depth = 50_000

    private fun deepChain(): TreeNode<Int> {
        val root = TreeNode(0)
        var current = root
        for (i in 1..depth) {
            val child = TreeNode(i)
            current.addChild(child)
            current = child
        }
        return root
    }

    @Test
    fun heightDoesNotOverflowOnDeepTree() {
        assertEquals(depth, deepChain().height())
    }

    @Test
    fun nodeCountDoesNotOverflowOnDeepTree() {
        // nodeCount() excludes the root, so a chain of `depth` extra nodes counts as `depth`.
        assertEquals(depth, deepChain().nodeCount())
    }

    @Test
    fun postOrderIterationDoesNotOverflowOnDeepTree() {
        val tree = deepChain().apply { treeIterator = TreeNodeIterators.PostOrder }
        assertEquals(depth + 1, tree.toList().size)
    }

    @Test
    fun preOrderIterationDoesNotOverflowOnDeepTree() {
        val tree = deepChain().apply { treeIterator = TreeNodeIterators.PreOrder }
        assertEquals(depth + 1, tree.toList().size)
    }
}
