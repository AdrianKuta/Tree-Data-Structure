package com.github.adriankuta.samples

import com.github.adriankuta.datastructure.tree.TreeNode
import com.github.adriankuta.datastructure.tree.ancestors
import com.github.adriankuta.datastructure.tree.anyNode
import com.github.adriankuta.datastructure.tree.countNodes
import com.github.adriankuta.datastructure.tree.deepCopy
import com.github.adriankuta.datastructure.tree.distance
import com.github.adriankuta.datastructure.tree.filterNodes
import com.github.adriankuta.datastructure.tree.findNode
import com.github.adriankuta.datastructure.tree.isLeaf
import com.github.adriankuta.datastructure.tree.leaves
import com.github.adriankuta.datastructure.tree.levelOrderSequence
import com.github.adriankuta.datastructure.tree.lowestCommonAncestor
import com.github.adriankuta.datastructure.tree.mapValues
import com.github.adriankuta.datastructure.tree.pathBetween
import com.github.adriankuta.datastructure.tree.preOrderSequence
import com.github.adriankuta.datastructure.tree.structurallyEquals
import com.github.adriankuta.datastructure.tree.tree
import com.github.adriankuta.datastructure.tree.coroutines.asFlow
import com.github.adriankuta.datastructure.tree.coroutines.preOrderFlow
import com.github.adriankuta.datastructure.tree.immutable.ImmutableTreeNode
import com.github.adriankuta.datastructure.tree.immutable.preOrder
import com.github.adriankuta.datastructure.tree.iterators.TreeNodeIterators
import com.github.adriankuta.datastructure.tree.serialization.TreeNodeDto
import com.github.adriankuta.datastructure.tree.serialization.toDto
import com.github.adriankuta.datastructure.tree.serialization.toTreeNode
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private fun sampleTree(): TreeNode<String> = tree("World") {
    child("North America") {
        child("USA")
    }
    child("Europe") {
        child("Poland")
        child("Germany")
    }
}

/** Core API: DSL, pretty-print, traversal, navigation, functional, query, utilities, mutation. */
fun coreSample(): String = buildString {
    val root = sampleTree()

    appendLine("prettyString():")
    append(root.prettyString())
    appendLine()

    appendLine("pre-order:   " + root.preOrderSequence().map { it.value }.toList())
    appendLine("level-order: " + root.levelOrderSequence().map { it.value }.toList())

    val usa = root.findNode { it == "USA" }!!
    val poland = root.findNode { it == "Poland" }!!
    appendLine("usa.depth():       " + usa.depth())
    appendLine("usa.ancestors():   " + usa.ancestors().map { it.value })
    appendLine("root.leaves():     " + root.leaves().map { it.value })
    appendLine("usa.isLeaf:        " + usa.isLeaf)

    appendLine("anyNode == Poland: " + root.anyNode { it == "Poland" })
    appendLine("filterNodes len>5: " + root.filterNodes { it.length > 5 }.map { it.value })
    appendLine("countNodes 'U*':   " + root.countNodes { it.startsWith("U") })
    appendLine("mapValues length:  " + root.mapValues { it.length }.preOrderSequence().map { it.value }.toList())
    appendLine("deepCopy equals:   " + root.structurallyEquals(root.deepCopy()))

    appendLine("lowestCommonAncestor(USA, Poland): " + usa.lowestCommonAncestor(poland)?.value)
    appendLine("pathBetween(USA, Poland):          " + usa.pathBetween(poland)?.map { it.value })
    appendLine("distance(USA, Poland):             " + usa.distance(poland))

    appendLine("nodeCount(): " + root.nodeCount())
    appendLine("height():    " + root.height())
    appendLine("path(USA):   " + root.path(usa)?.map { it.value })

    // Mutation on a copy; the shared sampleTree() stays untouched.
    val mutable = root.deepCopy()
    mutable.addChild(TreeNode("Asia"))
    mutable.findNode { it == "Germany" }?.detach()
    appendLine("after addChild(Asia) + detach(Germany): " + mutable.preOrderSequence().map { it.value }.toList())
}

/** Serialization satellite: TreeNode -> TreeNodeDto -> JSON -> TreeNodeDto -> TreeNode round-trip. */
fun serializationSample(): String = buildString {
    val root = sampleTree()
    val json = Json.encodeToString(root.toDto())
    appendLine("JSON: $json")
    val restored = Json.decodeFromString<TreeNodeDto<String>>(json).toTreeNode()
    appendLine("round-trips structurallyEquals: " + root.structurallyEquals(restored))
}

/** Coroutines satellite: traverse the tree as a cold Flow. */
fun coroutinesSample(): String = buildString {
    val root = sampleTree()
    val preOrder = runBlocking { root.preOrderFlow().map { it.value }.toList() }
    val levelOrder = runBlocking { root.asFlow(TreeNodeIterators.LevelOrder).map { it.value }.toList() }
    appendLine("preOrderFlow():     $preOrder")
    appendLine("asFlow(LevelOrder): $levelOrder")
}

/** Immutable satellite: persistent tree; every op returns a new root, leaving the original intact. */
fun immutableSample(): String = buildString {
    val root = ImmutableTreeNode("World").addChild(ImmutableTreeNode("Europe"))
    val bigger = root.addChild(ImmutableTreeNode("Asia"))
    appendLine("root.children:   " + root.children.map { it.value })
    appendLine("bigger.children: " + bigger.children.map { it.value })
    appendLine("root unchanged:  " + (root.children.size == 1))
    appendLine("bigger.mapValues uppercase preOrder: " + bigger.mapValues { it.uppercase() }.preOrder().map { it.value })
}

fun main() {
    println("== Core ==")
    println(coreSample())
    println("== Serialization ==")
    println(serializationSample())
    println("== Coroutines ==")
    println(coroutinesSample())
    println("== Immutable ==")
    println(immutableSample())
}
