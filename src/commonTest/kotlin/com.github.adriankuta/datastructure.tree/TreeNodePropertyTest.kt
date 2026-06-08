package com.github.adriankuta.datastructure.tree

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Property-based tests for traversal and structural invariants (issue #38).
 *
 * Instead of a handful of hand-written example trees, each property is checked against many
 * randomly generated trees. Generation is seeded ([BASE_SEED] + iteration index), so a failing case
 * is fully reproducible: rerun [randomTree] with the seed printed in the failure message. No
 * external dependency is used, so these run on every Kotlin target (JVM/JS/Wasm/Native).
 */
class TreeNodePropertyTest {

    // -----------------------------------------------------------------------------------------
    // Traversal node-set invariants
    // -----------------------------------------------------------------------------------------

    @Test
    fun allThreeOrdersVisitTheSameSetOfNodes() = forEachRandomTree { tree, seed ->
        val pre = tree.preOrderSequence().toList()
        val post = tree.postOrderSequence().toList()
        val level = tree.levelOrderSequence().toList()

        assertEquals(pre.toSet(), post.toSet(), "pre vs post node set (seed=$seed)")
        assertEquals(pre.toSet(), level.toSet(), "pre vs level node set (seed=$seed)")
    }

    @Test
    fun allThreeOrdersHaveTheSameCardinalityAndNoDuplicates() = forEachRandomTree { tree, seed ->
        val pre = tree.preOrderSequence().toList()
        val post = tree.postOrderSequence().toList()
        val level = tree.levelOrderSequence().toList()
        val expectedSize = tree.nodeCount() + 1 // traversal includes the root; nodeCount excludes it

        assertEquals(expectedSize, pre.size, "pre-order size (seed=$seed)")
        assertEquals(expectedSize, post.size, "post-order size (seed=$seed)")
        assertEquals(expectedSize, level.size, "level-order size (seed=$seed)")
        assertEquals(pre.size, pre.toSet().size, "pre-order visits no node twice (seed=$seed)")
        assertEquals(post.size, post.toSet().size, "post-order visits no node twice (seed=$seed)")
        assertEquals(level.size, level.toSet().size, "level-order visits no node twice (seed=$seed)")
    }

    // -----------------------------------------------------------------------------------------
    // Per-order ordering invariants
    // -----------------------------------------------------------------------------------------

    @Test
    fun preOrderEmitsEverySubtreeAsAContiguousBlockAfterItsRoot() = forEachRandomTree { tree, seed ->
        val pre = tree.preOrderSequence().toList()
        val index = pre.indexMap()
        for (node in pre) {
            val start = index.getValue(node) + 1
            val block = pre.subList(start, start + node.nodeCount())
            assertEquals(node.descendants().toSet(), block.toSet(), "pre-order subtree of $node (seed=$seed)")
        }
    }

    @Test
    fun postOrderEmitsEverySubtreeAsAContiguousBlockBeforeItsRoot() = forEachRandomTree { tree, seed ->
        val post = tree.postOrderSequence().toList()
        val index = post.indexMap()
        for (node in post) {
            val end = index.getValue(node)
            val block = post.subList(end - node.nodeCount(), end)
            assertEquals(node.descendants().toSet(), block.toSet(), "post-order subtree of $node (seed=$seed)")
        }
    }

    @Test
    fun levelOrderVisitsNodesInNonDecreasingDepth() = forEachRandomTree { tree, seed ->
        val depths = tree.levelOrderSequence().map { it.depth() }.toList()
        for (i in 1 until depths.size) {
            assertTrue(depths[i - 1] <= depths[i], "level-order depth not monotonic at $i (seed=$seed)")
        }
    }

    @Test
    fun preAndLevelOrderVisitEveryParentBeforeItsChildren() = forEachRandomTree { tree, seed ->
        for (order in listOf(tree.preOrderSequence(), tree.levelOrderSequence())) {
            val index = order.toList().indexMap()
            for (node in index.keys) {
                for (child in node.children) {
                    assertTrue(index.getValue(node) < index.getValue(child), "parent before child (seed=$seed)")
                }
            }
        }
    }

    @Test
    fun postOrderVisitsEveryChildBeforeItsParent() = forEachRandomTree { tree, seed ->
        val index = tree.postOrderSequence().toList().indexMap()
        for (node in index.keys) {
            for (child in node.children) {
                assertTrue(index.getValue(child) < index.getValue(node), "child before parent (seed=$seed)")
            }
        }
    }

    // -----------------------------------------------------------------------------------------
    // depth / height invariants
    // -----------------------------------------------------------------------------------------

    @Test
    fun depthMatchesAnIndependentBfsLevelAndEveryChildIsOneDeeper() = forEachRandomTree { tree, seed ->
        assertEquals(0, tree.depth(), "root depth (seed=$seed)")
        // Independent oracle: derive each node's level by walking DOWN through children (BFS), then
        // cross-check against depth(), which walks UP through parent pointers. A bug in the parent
        // walk cannot corrupt both derivations identically.
        val levelByNode = HashMap<TreeNode<Int>, Int>()
        levelByNode[tree] = 0
        val queue = ArrayDeque<TreeNode<Int>>()
        queue.add(tree)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            for (child in node.children) {
                levelByNode[child] = levelByNode.getValue(node) + 1
                queue.add(child)
            }
        }
        for (node in tree) {
            assertEquals(levelByNode.getValue(node), node.depth(), "depth matches BFS level (seed=$seed)")
            for (child in node.children) {
                assertEquals(node.depth() + 1, child.depth(), "child depth (seed=$seed)")
            }
        }
    }

    @Test
    fun heightEqualsDeepestDescendantDistanceAndLeavesHaveHeightZero() = forEachRandomTree { tree, seed ->
        for (node in tree) {
            val expected = node.asSequence().maxOf { it.depth() } - node.depth()
            assertEquals(expected, node.height(), "height of $node (seed=$seed)")
            if (node.isLeaf) assertEquals(0, node.height(), "leaf height (seed=$seed)")
        }
    }

    // -----------------------------------------------------------------------------------------
    // nodeCount invariants
    // -----------------------------------------------------------------------------------------

    @Test
    fun nodeCountEqualsDescendantCountForEveryNode() = forEachRandomTree { tree, seed ->
        for (node in tree) {
            // Independent oracle: nodeCount() walks an explicit stack; the pre-order sequence is a
            // separate traversal, so agreeing pins the count without circularity.
            assertEquals(node.asSequence().count() - 1, node.nodeCount(), "nodeCount vs traversal (seed=$seed)")
            // Recursive self-consistency: a node's count is the sum of (each child + its subtree).
            assertEquals(node.children.sumOf { it.nodeCount() + 1 }, node.nodeCount(), "nodeCount recursive sum (seed=$seed)")
        }
    }

    @Test
    fun nodeCountAndTraversalStayConsistentAcrossAttachAndDetach() = forEachRandomTree { tree, seed ->
        // A second, independent tree we graft onto a random node of `tree`, then remove again.
        val grafted = randomTree(Random(seed * 31 + 7), maxNodes = 20)
        val host = tree.toList().random(Random(seed xor SELECT_SALT))

        val countBefore = tree.nodeCount()
        val graftSize = grafted.nodeCount() + 1 // the grafted root plus its descendants

        host.addChild(grafted)
        assertEquals(countBefore + graftSize, tree.nodeCount(), "nodeCount after addChild (seed=$seed)")
        assertTrue(tree.toSet().containsAll(grafted.toList()), "grafted nodes now reachable (seed=$seed)")
        assertSameNode(host, grafted.parent, "graft re-parented (seed=$seed)")

        assertTrue(grafted.detach(), "detach returns true (seed=$seed)")
        assertEquals(countBefore, tree.nodeCount(), "nodeCount restored after detach (seed=$seed)")
        assertTrue(grafted.isRoot, "grafted is a root again (seed=$seed)")
        assertFalse(tree.toSet().contains(grafted), "grafted no longer reachable (seed=$seed)")
    }

    @Test
    fun removeAndReinsertKeepNodeCountAndPointersConsistent() = forEachRandomTree { tree, seed ->
        val rnd = Random(seed xor 0x55AA_55AAL)
        val parents = tree.toList().filter { it.children.isNotEmpty() }
        if (parents.isEmpty()) return@forEachRandomTree // single-node tree: nothing to remove

        val parent = parents.random(rnd)
        val countBefore = tree.nodeCount()
        val index = rnd.nextInt(parent.children.size)
        val subtreeSize = parent.children[index].nodeCount() + 1

        val removed = parent.removeChildAt(index)
        assertTrue(removed.isRoot, "removeChildAt detaches the child (seed=$seed)")
        assertEquals(countBefore - subtreeSize, tree.nodeCount(), "nodeCount drops by the subtree (seed=$seed)")
        assertFalse(tree.toSet().contains(removed), "removed subtree no longer reachable (seed=$seed)")

        val insertAt = rnd.nextInt(parent.children.size + 1)
        parent.insertChild(insertAt, removed)
        assertEquals(countBefore, tree.nodeCount(), "nodeCount restored after insertChild (seed=$seed)")
        assertSameNode(parent, removed.parent, "re-inserted subtree is re-parented (seed=$seed)")
        assertSameNode(removed, parent.children[insertAt], "re-inserted at the requested index (seed=$seed)")
        for (node in tree) {
            for (child in node.children) {
                assertSameNode(node, child.parent, "parent pointers stay consistent (seed=$seed)")
            }
        }
    }

    @Test
    fun clearRemovesEveryDescendantAndKeepsTheNodeAttached() = forEachRandomTree { tree, seed ->
        val node = tree.toList().random(Random(seed xor SELECT_SALT))
        val parentBefore = node.parent
        node.clear()
        assertEquals(0, node.nodeCount(), "nodeCount after clear (seed=$seed)")
        assertTrue(node.children.isEmpty(), "children empty after clear (seed=$seed)")
        assertSameNode(parentBefore, node.parent, "node keeps its own parent after clear (seed=$seed)")
    }

    // -----------------------------------------------------------------------------------------
    // Structural / parent-pointer invariants
    // -----------------------------------------------------------------------------------------

    @Test
    fun parentAndChildPointersAreConsistentForEveryNode() = forEachRandomTree { tree, seed ->
        assertTrue(tree.isRoot, "generated root is a root (seed=$seed)")
        for (node in tree) {
            assertEquals(node.parent == null, node.isRoot, "isRoot matches null parent (seed=$seed)")
            assertSameNode(tree, node.root(), "root() returns the tree root (seed=$seed)")
            for (child in node.children) {
                assertSameNode(node, child.parent, "child points back to parent (seed=$seed)")
            }
            val parent = node.parent
            if (parent != null) {
                assertTrue(parent.children.any { it === node }, "node listed in its parent (seed=$seed)")
                // Note: TreeNode is Iterable, so `list + node` would flatten the node's subtree —
                // compare siblings against the parent's other children directly instead.
                assertEquals(
                    parent.children.filter { it !== node }.toSet(),
                    node.siblings().toSet(),
                    "siblings are exactly the parent's other children (seed=$seed)",
                )
                assertFalse(node.siblings().any { it === node }, "siblings exclude self (seed=$seed)")
            }
        }
    }

    @Test
    fun ancestorChainOfEveryNodeTerminatesAtTheRoot() = forEachRandomTree { tree, seed ->
        for (node in tree) {
            val ancestors = node.ancestors()
            assertEquals(node.depth(), ancestors.size, "ancestor count equals depth (seed=$seed)")
            if (ancestors.isNotEmpty()) {
                assertSameNode(tree, ancestors.last(), "topmost ancestor is the root (seed=$seed)")
            }
            ancestors.forEach { assertTrue(it.depth() < node.depth(), "ancestors are shallower (seed=$seed)") }
        }
    }

    @Test
    fun leavesAreExactlyTheChildlessNodes() = forEachRandomTree { tree, seed ->
        assertEquals(tree.asSequence().filter { it.isLeaf }.toSet(), tree.leaves().toSet(), "leaves (seed=$seed)")
        assertTrue(tree.leaves().all { it.isLeaf }, "every leaf is childless (seed=$seed)")
        assertEquals(tree.toList().size, tree.descendants().size + 1, "descendants + self (seed=$seed)")
    }

    // -----------------------------------------------------------------------------------------
    // Transform / structural-equality invariants
    // -----------------------------------------------------------------------------------------

    @Test
    fun deepCopyAndIdentityMapPreserveShapeWithFreshNodes() = forEachRandomTree { tree, seed ->
        assertTrue(tree.structurallyEquals(tree), "structurallyEquals is reflexive (seed=$seed)")

        val copy = tree.deepCopy()
        assertTrue(copy.structurallyEquals(tree), "deepCopy is structurally equal (seed=$seed)")
        assertEquals(tree.nodeCount(), copy.nodeCount(), "deepCopy node count (seed=$seed)")
        assertEquals(tree.height(), copy.height(), "deepCopy height (seed=$seed)")
        assertTrue(copy.toSet().intersect(tree.toSet()).isEmpty(), "deepCopy shares no node object (seed=$seed)")

        val mapped = tree.mapValues { it }
        assertTrue(mapped.structurallyEquals(tree), "identity mapValues preserves structure (seed=$seed)")
        assertTrue(mapped.toSet().intersect(tree.toSet()).isEmpty(), "mapValues yields fresh nodes (seed=$seed)")
    }

    // -----------------------------------------------------------------------------------------
    // Functional / value-query invariants
    // -----------------------------------------------------------------------------------------

    @Test
    fun valueQueriesAgreeWithTraversalOverUniqueValues() = forEachRandomTree { tree, seed ->
        val values = tree.asSequence().map { it.value }.toList()
        assertEquals(values.size, values.toSet().size, "generated values are unique (seed=$seed)")
        assertEquals(values.size, tree.countNodes { true }, "countNodes(true) == size (seed=$seed)")
        for (value in values) {
            assertTrue(tree.contains(value), "contains present value $value (seed=$seed)")
            assertNotNull(tree.findNode { it == value }, "findNode present value $value (seed=$seed)")
        }
        val absent = values.max() + 1 // values are unique and dense from 0, so this one is absent
        assertFalse(tree.contains(absent), "absent value not contained (seed=$seed)")
    }

    // -----------------------------------------------------------------------------------------
    // Query algorithms (lca / distance / pathBetween)
    // -----------------------------------------------------------------------------------------

    @Test
    fun lowestCommonAncestorIsTheDeepestSharedAncestor() = forEachRandomTree { tree, seed ->
        val nodes = tree.toList()
        val rnd = Random(seed xor 0x1234_5678L)
        repeat(PAIRS_PER_TREE) {
            val a = nodes.random(rnd)
            val b = nodes.random(rnd)

            val lca = a.lowestCommonAncestor(b)
            assertNotNull(lca, "lca within one tree is non-null (seed=$seed)")
            assertSameNode(lca, b.lowestCommonAncestor(a), "lca is symmetric (seed=$seed)")

            val ancestorsAndSelfA = (listOf(a) + a.ancestors()).toSet()
            val ancestorsAndSelfB = (listOf(b) + b.ancestors()).toSet()
            assertTrue(lca in ancestorsAndSelfA, "lca is an ancestor-or-self of a (seed=$seed)")
            assertTrue(lca in ancestorsAndSelfB, "lca is an ancestor-or-self of b (seed=$seed)")
            // Common ancestors form a chain, so the deepest one is unique; it must be the lca itself.
            val deepestShared = ancestorsAndSelfA.intersect(ancestorsAndSelfB).maxByOrNull { it.depth() }
            assertSameNode(deepestShared, lca, "lca is the deepest shared ancestor (seed=$seed)")
        }
    }

    @Test
    fun distanceAndPathBetweenAreConsistent() = forEachRandomTree { tree, seed ->
        val nodes = tree.toList()
        val rnd = Random(seed xor 0x0F0F_0F0FL)
        repeat(PAIRS_PER_TREE) {
            val a = nodes.random(rnd)
            val b = nodes.random(rnd)

            val distance = a.distance(b)
            assertNotNull(distance, "distance within one tree is non-null (seed=$seed)")
            assertTrue(distance >= 0, "distance is non-negative (seed=$seed)")
            assertEquals(distance, b.distance(a), "distance is symmetric (seed=$seed)")

            val path = a.pathBetween(b)
            assertNotNull(path, "path within one tree is non-null (seed=$seed)")
            assertSameNode(a, path.first(), "path starts at a (seed=$seed)")
            assertSameNode(b, path.last(), "path ends at b (seed=$seed)")
            assertEquals(distance, path.size - 1, "distance == path edges (seed=$seed)")
            assertEquals(path.size, path.toSet().size, "path has no repeated node (seed=$seed)")
            for (i in 1 until path.size) {
                val (p, q) = path[i - 1] to path[i]
                assertTrue(p.parent === q || q.parent === p, "consecutive path nodes are an edge (seed=$seed)")
            }
        }
    }

    @Test
    fun distanceAndPathToSelfAreTrivial() = forEachRandomTree { tree, seed ->
        val node = tree.toList().random(Random(seed xor SELECT_SALT))
        assertEquals(0, node.distance(node), "distance to self is 0 (seed=$seed)")
        assertSameNode(node, node.lowestCommonAncestor(node), "lca with self is self (seed=$seed)")
        assertEquals(listOf(node), node.pathBetween(node), "path to self is the singleton (seed=$seed)")
    }

    // -----------------------------------------------------------------------------------------
    // Termination and ordering on degenerate (deep / wide) trees
    // -----------------------------------------------------------------------------------------

    @Test
    fun everyTraversalTerminatesAndIsCorrectlyOrderedOnADeepChain() {
        val depth = 5_000
        val root = TreeNode(0)
        var current = root
        for (i in 1..depth) {
            val child = TreeNode(i)
            current.addChild(child)
            current = child
        }

        // On a chain pre- and level-order descend the chain; post-order returns it leaf-first. These
        // pin the actual ordering, not merely that every order visits the same number of nodes.
        assertContentEquals((0..depth).toList(), root.preOrderSequence().map { it.value }.toList(), "pre-order of a chain")
        assertContentEquals((0..depth).toList(), root.levelOrderSequence().map { it.value }.toList(), "level-order of a chain")
        assertContentEquals((depth downTo 0).toList(), root.postOrderSequence().map { it.value }.toList(), "post-order of a chain")
        assertEquals(depth, root.height(), "height on deep chain")
        assertEquals(depth, root.nodeCount(), "nodeCount on deep chain")
        assertEquals(depth, current.depth(), "depth of the deepest node")
    }

    @Test
    fun everyTraversalTerminatesAndIsCorrectlyOrderedOnAWideTree() {
        val width = 5_000
        val root = TreeNode(0)
        for (i in 1..width) root.addChild(TreeNode(i))

        // pre- and level-order list the root then its children in order; post-order lists the
        // children in order then the root.
        assertContentEquals(listOf(0) + (1..width), root.preOrderSequence().map { it.value }.toList(), "pre-order of a star")
        assertContentEquals(listOf(0) + (1..width), root.levelOrderSequence().map { it.value }.toList(), "level-order of a star")
        assertContentEquals((1..width).toList() + 0, root.postOrderSequence().map { it.value }.toList(), "post-order of a star")
        assertEquals(1, root.height(), "height on wide tree")
        assertEquals(width, root.nodeCount(), "nodeCount on wide tree")
        assertTrue(root.children.all { it.depth() == 1 }, "every child of a wide root is at depth 1")
    }
}

// ---------------------------------------------------------------------------------------------
// Random tree generation + property harness
// ---------------------------------------------------------------------------------------------

private const val ITERATIONS = 200
private const val BASE_SEED = 0x5EEDL

/** How many random node pairs each query property samples per generated tree. */
private const val PAIRS_PER_TREE = 8

/** Decorrelates node-selection RNGs from the tree-construction RNG that shares the same seed. */
private const val SELECT_SALT = 0x2545_F491_4F6C_DD1DL

/** Runs [property] against [iterations] freshly generated random trees, one per derived seed. */
private fun forEachRandomTree(
    iterations: Int = ITERATIONS,
    maxNodes: Int = 80,
    property: (tree: TreeNode<Int>, seed: Long) -> Unit,
) {
    for (i in 0 until iterations) {
        val seed = BASE_SEED + i
        property(randomTree(Random(seed), maxNodes), seed)
    }
}

/**
 * Builds a random tree of `1..[maxNodes]` nodes by uniform random attachment: each new node (value
 * `1, 2, …`) is attached under a uniformly chosen existing node. This samples a broad spread of
 * shapes — chains, bushy, and lopsided trees, plus single-node trees — without the left-heavy skew
 * of a depth-first node budget, and is iterative so it never risks the call stack. Values are unique
 * and dense from `0` (the root). Deterministic for a given [random], so the seed reproduces it.
 */
private fun randomTree(random: Random, maxNodes: Int): TreeNode<Int> {
    val size = random.nextInt(1, maxNodes + 1)
    val root = TreeNode(0)
    val nodes = ArrayList<TreeNode<Int>>(size)
    nodes.add(root)
    for (value in 1 until size) {
        val parent = nodes[random.nextInt(nodes.size)]
        val child = TreeNode(value)
        parent.addChild(child)
        nodes.add(child)
    }
    return root
}

/** Maps each node to its position in this traversal. Keys compare by identity (TreeNode equality). */
private fun List<TreeNode<Int>>.indexMap(): Map<TreeNode<Int>, Int> =
    withIndex().associate { (i, node) -> node to i }

/** Asserts two references point at the same node object (TreeNode uses identity equality). */
private fun assertSameNode(expected: TreeNode<*>?, actual: TreeNode<*>?, message: String) {
    assertTrue(expected === actual, "$message — expected same node as $expected but was $actual")
}
