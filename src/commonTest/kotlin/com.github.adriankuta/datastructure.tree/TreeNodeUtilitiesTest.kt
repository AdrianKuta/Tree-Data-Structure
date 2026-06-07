package com.github.adriankuta.datastructure.tree

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TreeNodeUtilitiesTest {

    private val root = TreeNode(1)
    private val a = TreeNode(2)
    private val b = TreeNode(3)

    init {
        root.addChild(a)
        a.addChild(b)
    }

    @Test
    fun nodeCountCountsDescendantsExcludingRoot() {
        assertEquals(0, TreeNode("solo").nodeCount())
        assertEquals(2, root.nodeCount())
    }

    @Test
    fun heightIsLongestEdgePathToLeaf() {
        assertEquals(0, TreeNode("solo").height())
        assertEquals(2, root.height())
        assertEquals(1, a.height())
    }

    @Test
    fun depthIsDistanceToRoot() {
        assertEquals(0, root.depth())
        assertEquals(1, a.depth())
        assertEquals(2, b.depth())
    }

    @Test
    fun pathReturnsDescendantToReceiverChain() {
        assertContentEquals(listOf(b, a, root), root.path(b))
    }

    @Test
    fun pathReturnsNullWhenNotADescendant() {
        assertNull(root.path(TreeNode(99)))
    }

    @Test
    fun pathReturnsNullWhenDescendantIsRootItself() {
        assertNull(root.path(root))
    }
}
