package com.github.adriankuta.datastructure.tree

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TreeNodeQueryTest {

    // root(1)
    // ├── n2(2)
    // │   ├── n4(4)
    // │   └── n5(5)
    // └── n3(3)
    //     └── n6(6)
    private val root = TreeNode(1)
    private val n2 = TreeNode(2)
    private val n3 = TreeNode(3)
    private val n4 = TreeNode(4)
    private val n5 = TreeNode(5)
    private val n6 = TreeNode(6)

    // A completely separate tree.
    private val otherRoot = TreeNode(10)
    private val o11 = TreeNode(11)

    init {
        root.addChild(n2)
        root.addChild(n3)
        n2.addChild(n4)
        n2.addChild(n5)
        n3.addChild(n6)

        otherRoot.addChild(o11)
    }

    @Test
    fun lowestCommonAncestorOfTwoLeaves() {
        assertSame(n2, n4.lowestCommonAncestor(n5))
        assertSame(root, n4.lowestCommonAncestor(n6))
    }

    @Test
    fun lowestCommonAncestorOfSameNode() {
        assertSame(n4, n4.lowestCommonAncestor(n4))
    }

    @Test
    fun lowestCommonAncestorOfAncestorAndDescendant() {
        assertSame(n2, n2.lowestCommonAncestor(n4))
        assertSame(n2, n4.lowestCommonAncestor(n2))
        assertSame(root, root.lowestCommonAncestor(n6))
    }

    @Test
    fun lowestCommonAncestorOfNodesInDifferentTreesIsNull() {
        assertNull(n4.lowestCommonAncestor(o11))
        assertNull(o11.lowestCommonAncestor(n4))
    }

    @Test
    fun distanceValues() {
        assertEquals(0, n4.distance(n4))
        assertEquals(2, n4.distance(n5))
        assertEquals(1, n2.distance(n4))
        assertEquals(4, n4.distance(n6))
        assertEquals(2, root.distance(n4))
    }

    @Test
    fun distanceOfNodesInDifferentTreesIsNull() {
        assertNull(n4.distance(o11))
    }

    @Test
    fun pathBetweenSameNode() {
        assertContentEquals(listOf(n4), n4.pathBetween(n4))
    }

    @Test
    fun pathBetweenTwoLeaves() {
        // n4 -> n2 -> n5 (lca = n2 appears once, endpoints are n4 and n5)
        assertContentEquals(listOf(n4, n2, n5), n4.pathBetween(n5))
        // n4 -> n2 -> root -> n3 -> n6 (lca = root appears once)
        assertContentEquals(listOf(n4, n2, root, n3, n6), n4.pathBetween(n6))
    }

    @Test
    fun pathBetweenWithUnequalDepthLegs() {
        // Neither is an ancestor of the other and the legs differ in length: n4 is at depth 2, n3 at
        // depth 1, lca = root. Exercises the asymmetric up/down assembly.
        assertContentEquals(listOf(n4, n2, root, n3), n4.pathBetween(n3))
        assertContentEquals(listOf(n3, root, n2, n4), n3.pathBetween(n4))
    }

    @Test
    fun pathBetweenAncestorAndDescendant() {
        assertContentEquals(listOf(n2, n4), n2.pathBetween(n4))
        assertContentEquals(listOf(n4, n2), n4.pathBetween(n2))
        assertContentEquals(listOf(root, n3, n6), root.pathBetween(n6))
    }

    @Test
    fun pathBetweenOfNodesInDifferentTreesIsNull() {
        assertNull(n4.pathBetween(o11))
    }

    @Test
    fun containsTrueForValuesInSubtree() {
        assertTrue(root.contains(1)) // the receiver itself
        assertTrue(root.contains(6))
        assertTrue(n2.contains(5))
    }

    @Test
    fun containsFalseForValuesNotInSubtree() {
        assertFalse(n2.contains(6)) // n6 lives under n3, not n2
        assertFalse(root.contains(99))
    }
}
