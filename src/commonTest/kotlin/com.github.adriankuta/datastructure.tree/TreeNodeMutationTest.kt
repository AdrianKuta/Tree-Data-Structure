package com.github.adriankuta.datastructure.tree

import com.github.adriankuta.datastructure.tree.exceptions.TreeNodeException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TreeNodeMutationTest {

    @Test
    fun insertChildAtStartMiddleAndEnd() {
        val root = TreeNode("root")
        val a = TreeNode("a")
        val b = TreeNode("b")
        val c = TreeNode("c")
        val d = TreeNode("d")

        root.insertChild(0, b)          // [b]
        root.insertChild(0, a)          // [a, b]
        root.insertChild(2, d)          // [a, b, d] (end)
        root.insertChild(2, c)          // [a, b, c, d] (middle)

        assertContentEquals(listOf(a, b, c, d), root.children)
        // Each inserted node is re-parented to root.
        assertSame(root, a.parent)
        assertSame(root, b.parent)
        assertSame(root, c.parent)
        assertSame(root, d.parent)
    }

    @Test
    fun removeChildAtReturnsDetachedNodeAndClearsParent() {
        val root = TreeNode("root")
        val a = TreeNode("a")
        val b = TreeNode("b")
        val c = TreeNode("c")
        root.addChildren(a, b, c)

        val removed = root.removeChildAt(1)

        assertSame(b, removed)
        assertNull(removed.parent)
        assertContentEquals(listOf(a, c), root.children)
    }

    @Test
    fun replaceChildSwapsAndDetachesTheOld() {
        val root = TreeNode("root")
        val a = TreeNode("a")
        val b = TreeNode("b")
        val replacement = TreeNode("replacement")
        root.addChildren(a, b)

        val old = root.replaceChild(0, replacement)

        assertSame(a, old)
        assertNull(old.parent)
        assertSame(root, replacement.parent)
        assertContentEquals(listOf(replacement, b), root.children)
    }

    @Test
    fun moveChildReordersChildren() {
        val root = TreeNode("root")
        val a = TreeNode("a")
        val b = TreeNode("b")
        val c = TreeNode("c")
        root.addChildren(a, b, c)

        assertTrue(root.moveChild(a, 2))
        assertContentEquals(listOf(b, c, a), root.children)
        // Parent pointer is unchanged after a move.
        assertSame(root, a.parent)
    }

    @Test
    fun moveChildReturnsFalseForNonChild() {
        val root = TreeNode("root")
        val a = TreeNode("a")
        root.addChild(a)

        val stranger = TreeNode("stranger")
        assertFalse(root.moveChild(stranger, 0))
        assertContentEquals(listOf(a), root.children)
    }

    @Test
    fun addChildrenAppendsAllInOrder() {
        val root = TreeNode("root")
        val a = TreeNode("a")
        val b = TreeNode("b")
        val c = TreeNode("c")

        root.addChildren(a, b, c)

        assertContentEquals(listOf(a, b, c), root.children)
        assertSame(root, a.parent)
        assertSame(root, b.parent)
        assertSame(root, c.parent)
    }

    @Test
    fun addChildrenRejectsNodeThatAlreadyHasAParent() {
        val root = TreeNode("root")
        val attached = TreeNode("attached")
        TreeNode("other").addChild(attached)

        assertFailsWith<TreeNodeException> { root.addChildren(attached) }
    }

    @Test
    fun insertChildRejectsNodeThatAlreadyHasAParent() {
        val root = TreeNode("root")
        val attached = TreeNode("attached")
        TreeNode("other").addChild(attached)

        assertFailsWith<TreeNodeException> { root.insertChild(0, attached) }
    }

    @Test
    fun replaceChildRejectsNodeThatAlreadyHasAParent() {
        val root = TreeNode("root")
        val existing = TreeNode("existing")
        root.addChild(existing)

        val attached = TreeNode("attached")
        TreeNode("other").addChild(attached)

        assertFailsWith<TreeNodeException> { root.replaceChild(0, attached) }
        // The original child is untouched after a failed replace.
        assertContentEquals(listOf(existing), root.children)
        assertSame(root, existing.parent)
    }

    @Test
    fun sortChildrenReordersByComparator() {
        val root = TreeNode("root")
        val c = TreeNode("c")
        val a = TreeNode("a")
        val b = TreeNode("b")
        root.addChildren(c, a, b)

        root.sortChildren(compareBy { it.value })

        assertContentEquals(listOf(a, b, c), root.children)
    }
}
