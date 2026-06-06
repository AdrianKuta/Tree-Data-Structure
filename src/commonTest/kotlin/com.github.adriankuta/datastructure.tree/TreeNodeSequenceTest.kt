package com.github.adriankuta.datastructure.tree

import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TreeNodeSequenceTest {

    private fun sample() = tree(1) {
        child(2) {
            child(4)
            child(5)
        }
        child(3) {
            child(6)
        }
    }

    @Test
    fun preOrderSequence() =
        assertContentEquals(listOf(1, 2, 4, 5, 3, 6), sample().preOrderSequence().map { it.value }.toList())

    @Test
    fun postOrderSequence() =
        assertContentEquals(listOf(4, 5, 2, 6, 3, 1), sample().postOrderSequence().map { it.value }.toList())

    @Test
    fun levelOrderSequence() =
        assertContentEquals(listOf(1, 2, 3, 4, 5, 6), sample().levelOrderSequence().map { it.value }.toList())

    @Test
    fun asSequenceDefaultsToPreOrder() =
        assertContentEquals(listOf(1, 2, 4, 5, 3, 6), sample().asSequence().map { it.value }.toList())

    @Test
    fun asSequenceHonorsExplicitOrder() =
        assertContentEquals(
            listOf(1, 2, 3, 4, 5, 6),
            sample().asSequence(TreeNodeIterators.LevelOrder).map { it.value }.toList(),
        )

    @Test
    fun sequenceShortCircuitsLazily() =
        assertEquals(4, sample().preOrderSequence().map { it.value }.first { it == 4 })
}
