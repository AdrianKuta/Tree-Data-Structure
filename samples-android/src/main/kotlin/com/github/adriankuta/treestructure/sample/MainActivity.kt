package com.github.adriankuta.treestructure.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.adriankuta.datastructure.tree.TreeNode
import com.github.adriankuta.datastructure.tree.compose.LazyTree
import com.github.adriankuta.datastructure.tree.compose.TreeNodeRow
import com.github.adriankuta.datastructure.tree.tree
import org.jetbrains.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TreeSampleScreen()
                }
            }
        }
    }
}

/** Renders the [sampleTree] with the library's default [TreeNodeRow] via the one-line [LazyTree]. */
@Composable
fun TreeSampleScreen() {
    LazyTree(sampleTree())
}

private fun sampleTree(): TreeNode<String> = tree("World") {
    child("North America") {
        child("USA")
        child("Canada")
    }
    child("Europe") {
        child("Poland")
        child("Germany")
        child("Spain")
    }
    child("Asia") {
        child("Japan")
        child("India")
    }
}

@Preview
@Composable
private fun TreeSampleScreenPreview() {
    MaterialTheme {
        Surface {
            TreeSampleScreen()
        }
    }
}

@Preview
@Composable
private fun TreeNodeRowPreview() {
    MaterialTheme {
        TreeNodeRow(
            node = TreeNode("Europe").apply { addChild(TreeNode("Poland")) },
            depth = 1,
            expanded = true,
            toggle = {},
        )
    }
}
