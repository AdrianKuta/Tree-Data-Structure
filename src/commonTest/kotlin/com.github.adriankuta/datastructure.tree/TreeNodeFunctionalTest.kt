package com.github.adriankuta.datastructure.tree

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TreeNodeFunctionalTest {

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
    fun findNode() {
        assertEquals(6, sample().findNode { it == 6 }?.value)
        assertNull(sample().findNode { it == 99 })
    }

    @Test
    fun filterNodes() =
        assertContentEquals(listOf(2, 4, 6), sample().filterNodes { it % 2 == 0 }.map { it.value })

    @Test
    fun anyNode() {
        assertTrue(sample().anyNode { it == 6 })
        assertFalse(sample().anyNode { it == 99 })
    }

    @Test
    fun allNodes() {
        assertTrue(sample().allNodes { it > 0 })
        assertFalse(sample().allNodes { it < 5 })
    }

    @Test
    fun countNodes() = assertEquals(3, sample().countNodes { it > 3 })

    @Test
    fun foldNodes() = assertEquals(21, sample().foldNodes(0) { acc, node -> acc + node.value })

    @Test
    fun mapPreservesStructureAndTransformsValues() {
        val mapped = sample().mapValues { it * 10 }
        assertContentEquals(
            listOf(10, 20, 40, 50, 30, 60),
            mapped.preOrderSequence().map { it.value }.toList(),
        )
    }

    @Test
    fun deepCopyIsStructurallyEqualButDistinct() {
        val original = sample()
        val copy = original.deepCopy()
        assertNotSame(original, copy)
        assertTrue(original.structurallyEquals(copy))
    }

    @Test
    fun structurallyEqualsDistinguishesByValueAndShape() {
        assertTrue(sample().structurallyEquals(sample()))
        val different = tree(1) {
            child(2) { child(4) }
            child(3) { child(6) }
        }
        assertFalse(sample().structurallyEquals(different))
    }
}
