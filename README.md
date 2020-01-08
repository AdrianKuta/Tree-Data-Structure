# Tree (Data Structure)
[![maven](https://img.shields.io/maven-central/v/com.github.adriankuta/tree-structure?style=plastic)](https://mvnrepository.com/artifact/com.github.adriankuta/tree-structure)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/AdrianKuta/Design-Patterns-Kotlin/blob/master/LICENSE)

Simple implementation to store object in tree structure. Method `toString()` is overrided to provide nice tree view in logs.

## Usage

```kotlin
val root = TreeNode<String>("Root")
val beveragesNode = TreeNode<String>("Beverages")
val curdNode = TreeNode<String>("Curd")
root.addChild(beveragesNode)
root.addChild(curdNode)

val teaNode = TreeNode<String>("tea")
val coffeeNode = TreeNode<String>("coffee")
val milkShakeNode = TreeNode<String>("Milk Shake")
beveragesNode.addChild(teaNode)
beveragesNode.addChild(coffeeNode)
beveragesNode.addChild(milkShakeNode)

val gingerTeaNode = TreeNode<String>("ginger tea")
val normalTeaNode = TreeNode<String>("normal tea")
teaNode.addChild(gingerTeaNode)
teaNode.addChild(normalTeaNode)

val yogurtNode = TreeNode<String>("yogurt")
val lassiNode = TreeNode<String>("lassi")
curdNode.addChild(yogurtNode)
curdNode.addChild(lassiNode)

println(root)
System.out.println("Remove: ${curdNode.value}")
root.removeChild(curdNode)
System.out.println("Remove: ${gingerTeaNode.value}")
root.removeChild(gingerTeaNode)
println(root)
```

*Output:*

<img src="https://github.com/AdrianKuta/Tree-Collection/blob/master/images/console_output.png" width=400>
