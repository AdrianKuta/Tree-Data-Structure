package com.github.adriankuta.datastructure.tree

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TreeNodeNavigationTest {

    private val root = TreeNode(1)
    private val n2 = TreeNode(2)
    private val n3 = TreeNode(3)
    private val n4 = TreeNode(4)
    private val n5 = TreeNode(5)
    private val n6 = TreeNode(6)

    init {
        root.addChild(n2)
        root.addChild(n3)
        n2.addChild(n4)
        n2.addChild(n5)
        n3.addChild(n6)
    }

    @Test
    fun isLeaf() {
        assertTrue(n4.isLeaf)
        assertFalse(root.isLeaf)
    }

    @Test
    fun degree() {
        assertEquals(2, root.degree)
        assertEquals(0, n4.degree)
    }

    @Test
    fun root() {
        assertSame(root, n6.root())
        assertSame(root, root.root())
    }

    @Test
    fun ancestors() {
        assertContentEquals(listOf(n2, root), n4.ancestors())
        assertContentEquals(emptyList(), root.ancestors())
    }

    @Test
    fun siblings() {
        assertContentEquals(listOf(n5), n4.siblings())
        assertContentEquals(emptyList(), root.siblings())
    }

    @Test
    fun leaves() {
        assertContentEquals(listOf(n4, n5, n6), root.leaves())
    }

    @Test
    fun descendants() {
        assertContentEquals(listOf(n2, n4, n5, n3, n6), root.descendants())
    }
}
