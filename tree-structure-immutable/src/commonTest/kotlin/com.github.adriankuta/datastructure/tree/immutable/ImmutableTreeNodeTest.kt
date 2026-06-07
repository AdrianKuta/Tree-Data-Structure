package com.github.adriankuta.datastructure.tree.immutable

import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ImmutableTreeNodeTest {

    // World
    // ├── North America
    // │   └── USA
    // └── Europe
    //     ├── Poland
    //     └── Germany
    private val usa = ImmutableTreeNode("USA")
    private val northAmerica = ImmutableTreeNode("North America", persistentListOf(usa))
    private val poland = ImmutableTreeNode("Poland")
    private val germany = ImmutableTreeNode("Germany")
    private val europe = ImmutableTreeNode("Europe", persistentListOf(poland, germany))
    private val world = ImmutableTreeNode("World", persistentListOf(northAmerica, europe))

    @Test
    fun addChildReturnsNewInstanceAndLeavesOriginalUnchanged() {
        val asia = ImmutableTreeNode("Asia")
        val updated = world.addChild(asia)

        assertEquals(3, updated.children.size)
        assertEquals("Asia", updated.children[2].value)

        // Original is untouched.
        assertEquals(2, world.children.size)
        assertFalse(updated === world)
    }

    @Test
    fun removeChildReturnsNewInstanceAndLeavesOriginalUnchanged() {
        val updated = world.removeChild(europe)

        assertEquals(1, updated.children.size)
        assertEquals("North America", updated.children[0].value)

        // Original is untouched.
        assertEquals(2, world.children.size)
        assertFalse(updated === world)
    }

    @Test
    fun addChildSharesUnmodifiedSiblingSubtrees() {
        val asia = ImmutableTreeNode("Asia")
        val updated = world.addChild(asia)

        // The siblings that are not on the modified path are the SAME instances.
        assertSame(northAmerica, updated.children[0])
        assertSame(europe, updated.children[1])
    }

    @Test
    fun rebuildingOnlyOnePathSharesTheOtherSubtree() {
        // Add a child under Europe; North America's subtree should be reused untouched.
        val spain = ImmutableTreeNode("Spain")
        val newEurope = europe.addChild(spain)
        val updated = world.copy(children = world.children.set(1, newEurope))

        assertSame(northAmerica, updated.children[0])
        assertFalse(updated.children[1] === europe)
        assertSame(usa, updated.children[0].children[0])
    }

    @Test
    fun mapValuesTransformsEveryValueAndKeepsShape() {
        val lengths = world.mapValues { it.length }

        assertEquals("World".length, lengths.value)
        assertEquals(2, lengths.children.size)
        assertEquals("North America".length, lengths.children[0].value)
        assertEquals("USA".length, lengths.children[0].children[0].value)
        assertEquals("Germany".length, lengths.children[1].children[1].value)
    }

    @Test
    fun preOrderVisitsParentBeforeChildren() {
        assertEquals(
            listOf("World", "North America", "USA", "Europe", "Poland", "Germany"),
            world.preOrder().map { it.value },
        )
    }

    @Test
    fun postOrderVisitsChildrenBeforeParent() {
        assertEquals(
            listOf("USA", "North America", "Poland", "Germany", "Europe", "World"),
            world.postOrder().map { it.value },
        )
    }

    @Test
    fun levelOrderVisitsBreadthFirst() {
        assertEquals(
            listOf("World", "North America", "Europe", "USA", "Poland", "Germany"),
            world.levelOrder().map { it.value },
        )
    }

    @Test
    fun nodeCountExcludesReceiver() {
        assertEquals(5, world.nodeCount())
        assertEquals(1, northAmerica.nodeCount())
        assertEquals(0, usa.nodeCount())
    }

    @Test
    fun heightCountsEdgesOnLongestPath() {
        assertEquals(2, world.height())
        assertEquals(1, europe.height())
        assertEquals(0, usa.height())
    }

    @Test
    fun equalityIsValueBased() {
        val sameWorld = ImmutableTreeNode(
            "World",
            persistentListOf(
                ImmutableTreeNode("North America", persistentListOf(ImmutableTreeNode("USA"))),
                ImmutableTreeNode("Europe", persistentListOf(ImmutableTreeNode("Poland"), ImmutableTreeNode("Germany"))),
            ),
        )

        assertEquals(world, sameWorld)
        assertTrue(world == sameWorld)
        assertFalse(world === sameWorld)
    }
}
