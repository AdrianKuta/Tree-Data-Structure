package com.github.adriankuta.datastructure.tree.coroutines

import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators
import com.github.adriankuta.datastructure.tree.tree
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TreeNodeFlowTest {

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
    fun preOrderFlowEmitsInPreOrder() = runTest {
        assertEquals(listOf(1, 2, 4, 5, 3, 6), sample().preOrderFlow().map { it.value }.toList())
    }

    @Test
    fun levelOrderFlowEmitsInLevelOrder() = runTest {
        assertEquals(
            listOf(1, 2, 3, 4, 5, 6),
            sample().asFlow(TreeNodeIterators.LevelOrder).map { it.value }.toList(),
        )
    }
}
