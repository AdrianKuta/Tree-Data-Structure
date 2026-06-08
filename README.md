# Tree (Data Structure)
[![maven](https://img.shields.io/maven-central/v/com.github.adriankuta/tree-structure?style=plastic)](https://mvnrepository.com/artifact/com.github.adriankuta/tree-structure)
[![License: MIT](https://img.shields.io/github/license/AdrianKuta/Tree-Data-Structure?style=plastic)](https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/LICENSE)
[![Publish](https://github.com/AdrianKuta/Tree-Data-Structure/actions/workflows/publishRelease.yml/badge.svg)](https://github.com/AdrianKuta/Tree-Data-Structure/actions/workflows/publishRelease.yml)
[![API docs](https://img.shields.io/badge/docs-API%20reference-blue?style=plastic)](https://adriankuta.github.io/Tree-Data-Structure/)

📖 **[API reference](https://adriankuta.github.io/Tree-Data-Structure/)** — full KDoc for the core and all modules.

A lightweight n-ary tree for Kotlin Multiplatform. You get a generic `TreeNode<T>`, a small DSL for
building trees, three traversal orders, lazy `Sequence` traversal, and a set of navigation and
functional helpers. The core artifact has no third-party dependencies.

It fits homogeneous trees of arbitrary depth: UI component hierarchies, file-system views, org
charts, and category menus. For fixed, typed hierarchies (like a compiler AST) a sealed class is
usually a better fit.

## Features

- Kotlin Multiplatform: JVM, Android, JS, Wasm, iOS, and a native host target.
- Build trees with a `tree { child(...) }` DSL or node by node with `addChild`.
- Pre-order, post-order, and level-order traversal, as iterators or lazy `Sequence`s.
- Navigation: `root()`, `ancestors()`, `siblings()`, `leaves()`, `descendants()`, `isLeaf`, `degree`.
- Functional helpers: `findNode`, `filterNodes`, `anyNode`, `allNodes`, `foldNodes`, `mapValues`, `deepCopy`, `structurallyEquals`.
- Utilities: `nodeCount()`, `height()`, `depth()`, `path()`, `prettyString()`.
- Stack-safe: traversal and `height()`/`nodeCount()`/`clear()` handle very deep trees without `StackOverflowError`.

## Installation

Gradle (Kotlin DSL):
```kotlin
// commonMain for KMP projects, or any sourceSet/module where you need it
dependencies {
    implementation("com.github.adriankuta:tree-structure:4.1.1") // latest version is on the badge above
}
```

Gradle (Groovy):
```groovy
dependencies {
    implementation "com.github.adriankuta:tree-structure:4.1.1"
}
```

Maven:
```xml
<dependency>
  <groupId>com.github.adriankuta</groupId>
  <artifactId>tree-structure</artifactId>
  <version>4.1.1</version>
</dependency>
```

## Building a tree

The DSL is the shortest way to build one:
```kotlin
val root = tree("World") {
    child("North America") { child("USA") }
    child("Europe") {
        child("Poland")
        child("Germany")
    }
}
```

The same node-by-node API works from Kotlin and Java:
```java
TreeNode<String> root = new TreeNode<>("World");
TreeNode<String> northAmerica = new TreeNode<>("North America");
root.addChild(northAmerica);
northAmerica.addChild(new TreeNode<>("USA"));

TreeNode<String> europe = new TreeNode<>("Europe");
root.addChild(europe);
europe.addChild(new TreeNode<>("Poland"));
europe.addChild(new TreeNode<>("Germany"));
```

`prettyString()` renders the tree for logs and debugging:
```
World
├── North America
│   └── USA
└── Europe
    ├── Poland
    └── Germany
```

## Traversal

Iterating a node visits the node and all of its descendants. The default order is set in the
constructor (pre-order by default) and is read-only. Pass an order per call when you need a
different one:
```kotlin
for (node in root) println(node.value)                            // default pre-order
for (node in root.asSequence(TreeNodeIterators.PostOrder)) println(node.value)
```

Traversal is also exposed as a lazy `Sequence`, so it composes with the standard library and stops
early instead of materializing the whole tree:
```kotlin
root.preOrderSequence().map { it.value }.toList()       // [World, North America, USA, Europe, Poland, Germany]
root.levelOrderSequence().first { it.value == "USA" }   // stops as soon as it is found
root.asSequence(TreeNodeIterators.PostOrder).count()    // 6
```

## Navigation
```kotlin
val usa = root.findNode { it == "USA" }!!

usa.isLeaf                          // true
usa.depth()                        // 2
usa.root().value                   // "World"
usa.ancestors().map { it.value }   // [North America, World]
root.leaves().map { it.value }     // [USA, Poland, Germany]
```

## Functional operations
```kotlin
root.anyNode { it == "Poland" }        // true
root.filterNodes { it.length > 5 }     // nodes whose value is longer than 5 characters
root.countNodes { it.startsWith("U") } // 1

val lengths: TreeNode<Int> = root.mapValues { it.length } // a new tree; the original is untouched
val copy = root.deepCopy()
root.structurallyEquals(copy)          // true: same values and shape, different nodes
```

## Utilities
```kotlin
root.nodeCount()  // number of descendants, excluding the root
root.height()     // edges on the longest path down to a leaf
root.depth()      // edges from this node up to the root
root.path(usa)    // [USA, North America, World], or null if usa is not a descendant
```

## Mutating a tree
```kotlin
// addChild rejects a node that already has a parent or that would create a cycle.
root.addChild(TreeNode("Asia"))

// removeChild removes a direct child of the receiver and returns true if it was present.
root.removeChild(root.children.first())

// detach() unhooks a node from wherever it currently lives.
root.findNode { it == "Germany" }?.detach()

// clear() removes every descendant of the node.
root.clear()
```

## Optional modules

The core artifact has no third-party dependencies. Each integration is a separate, opt-in artifact
that depends on the core.

### Serialization (`tree-structure-serialization`)

`kotlinx.serialization` support. A `TreeNode` keeps a reference back to its parent, so it cannot be
`@Serializable` directly. Convert to and from the acyclic `TreeNodeDto` instead.

```kotlin
implementation("com.github.adriankuta:tree-structure-serialization:4.1.1")
```
```kotlin
val json = Json.encodeToString(root.toDto())
val restored = Json.decodeFromString<TreeNodeDto<String>>(json).toTreeNode()
```

### Coroutines (`tree-structure-coroutines`)

Traverse a tree as a cold `Flow`, which is handy inside coroutine and `ViewModel` pipelines.

```kotlin
implementation("com.github.adriankuta:tree-structure-coroutines:4.1.1")
```
```kotlin
root.preOrderFlow().collect { println(it.value) }
root.asFlow(TreeNodeIterators.LevelOrder).map { it.value }
```

### Compose UI (`tree-structure-compose`)

A `LazyTree` composable for Compose Multiplatform (JVM/desktop, Android, iOS, Wasm). Only the visible
nodes are composed.

```kotlin
implementation("com.github.adriankuta:tree-structure-compose:4.1.1")
```

For the common case, the no-content overload renders each node with the built-in `TreeNodeRow`
(a clickable, indented row with a `▾`/`▸` marker — foundation-only, no Material dependency):

```kotlin
LazyTree(root)                         // sensible default
LazyTree(root, label = { it.name })    // map a node's value to its text
```

Or supply your own row for full control:

```kotlin
LazyTree(root) { node, depth, expanded, toggle ->
    Row(Modifier.padding(start = (depth * 16).dp).clickable(onClick = toggle)) {
        if (!node.isLeaf) Text(if (expanded) "▾ " else "▸ ")
        Text(node.value.toString())
    }
}
```

A runnable Android demo lives in the [`samples-android`](samples-android) module.

### Immutable (`tree-structure-immutable`)

A persistent `ImmutableTreeNode` with structural sharing. Every operation (`addChild`,
`removeChild`, `mapValues`) returns a **new** root and leaves the original untouched; unchanged
subtrees are reused, so updates are cheap and old roots stay valid. Backed by
`kotlinx.collections.immutable`.

```kotlin
implementation("com.github.adriankuta:tree-structure-immutable:4.1.1")
```
```kotlin
val root = ImmutableTreeNode("World").addChild(ImmutableTreeNode("Europe"))
val bigger = root.addChild(ImmutableTreeNode("Asia")) // root is unchanged; bigger is a new tree

bigger.preOrder().forEach { println(it.value) } // pre/post/level-order, nodeCount(), height()
```

## Examples

A runnable `:samples` module bundles compile-checked, assertion-verified examples of the core API
and the serialization, coroutines, and immutable modules. Run them with:

```
./gradlew :samples:run
```

## Notes

`TreeNode` is mutable and not thread-safe. Add your own synchronization if you share a tree across
threads, and do not modify a tree while you iterate it. Equality is by reference; use
`structurallyEquals` to compare two trees by value and shape.

Coming from 3.x? See [CHANGELOG.md](CHANGELOG.md) for the 4.0 migration notes.

## Releasing (maintainers)

Releases go to Maven Central through the Sonatype Central Portal using the
`com.vanniktech.maven.publish` plugin. Creating a GitHub release runs
`.github/workflows/publishRelease.yml`, which signs and uploads every module; the deployment is then
published from central.sonatype.com. The published version comes from `PUBLISH_VERSION` in
`build.gradle.kts`. CI needs the `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_PASSWORD`, `SIGNING_KEY`,
and `SIGNING_PASSWORD` repository secrets. To publish from a local machine, set the matching
`ORG_GRADLE_PROJECT_*` properties and run `./gradlew publishToMavenCentral` (add `-Psnapshot=true`
for a snapshot build).

## License

MIT License

Copyright (c) 2020 Adrian Kuta

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
