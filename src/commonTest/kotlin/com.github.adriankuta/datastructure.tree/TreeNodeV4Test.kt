package com.github.adriankuta.datastructure.tree

import com.github.adriankuta.datastructure.tree.exceptions.TreeNodeException
import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TreeNodeV4Test {

    @Test
    fun addChildRejectsNodeThatAlreadyHasAParent() {
        val a = TreeNode("a")
        val b = TreeNode("b")
        a.addChild(b)

        val other = TreeNode("other")
        assertFailsWith<TreeNodeException> { other.addChild(b) }
    }

    @Test
    fun addChildRejectsCycles() {
        val root = TreeNode("root")
        val child = TreeNode("child")
        root.addChild(child)

        // Attaching an ancestor under its own descendant would create a cycle.
        assertFailsWith<TreeNodeException> { child.addChild(root) }
        // Attaching a node under itself is also a cycle.
        assertFailsWith<TreeNodeException> { root.addChild(root) }
    }

    @Test
    fun detachRemovesFromParent() {
        val root = TreeNode("root")
        val child = TreeNode("child")
        root.addChild(child)

        assertTrue(child.detach())
        assertNull(child.parent)
        assertContentEquals(emptyList(), root.children)
        // Detached node can now be re-attached elsewhere.
        val newParent = TreeNode("newParent")
        newParent.addChild(child)
        assertSame(newParent, child.parent)
    }

    @Test
    fun detachOnRootReturnsFalse() {
        assertFalse(TreeNode("root").detach())
    }

    @Test
    fun removeChildOnlyRemovesDirectChildren() {
        val root = TreeNode("root")
        val parent = TreeNode("parent")
        val grandchild = TreeNode("grandchild")
        root.addChild(parent)
        parent.addChild(grandchild)

        // grandchild is not a direct child of root -> no-op, returns false.
        assertFalse(root.removeChild(grandchild))
        assertSame(parent, grandchild.parent)

        // direct child removal works.
        assertTrue(parent.removeChild(grandchild))
        assertNull(grandchild.parent)
    }

    @Test
    fun clearOnNonRootKeepsItAttachedToItsParent() {
        val root = TreeNode("root")
        val branch = TreeNode("branch")
        val leaf = TreeNode("leaf")
        root.addChild(branch)
        branch.addChild(leaf)

        branch.clear()

        assertContentEquals(emptyList(), branch.children)
        assertSame(root, branch.parent)          // branch stays attached to root
        assertContentEquals(listOf(branch), root.children)
        assertNull(leaf.parent)                  // former descendant is detached
    }

    @Test
    fun iteratorAcceptsExplicitOrderWithoutMutatingDefault() {
        val tree = tree(1) {
            child(2) { child(4) }
            child(3)
        }

        val postOrder = tree.iterator(TreeNodeIterators.PostOrder).asSequence().map { it.value }.toList()
        assertContentEquals(listOf(4, 2, 3, 1), postOrder)

        // Default order is unchanged (PreOrder).
        assertEquals(TreeNodeIterators.PreOrder, tree.treeIterator)
        assertContentEquals(listOf(1, 2, 4, 3), tree.map { it.value })
    }
}
