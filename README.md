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


## Download

    implementation "com.github.adriankuta:tree-structure:$latest_versions"
    
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