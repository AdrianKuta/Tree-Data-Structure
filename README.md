# Tree (Data Structure)
[![maven](https://img.shields.io/maven-central/v/com.github.adriankuta/tree-structure?style=plastic)](https://search.maven.org/artifact/com.github.adriankuta/tree-structure)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=plastic)](https://github.com/AdrianKuta/Design-Patterns-Kotlin/blob/master/LICENSE)
[![CircleCI](https://img.shields.io/circleci/build/github/AdrianKuta/Tree-Data-Structure/master?label=CircleCI&style=plastic)](https://circleci.com/gh/AdrianKuta/Tree-Data-Structure)

Simple implementation to store object in tree structure. Method `toString()` is overrided to provide nice tree view in logs.

## Usage

```kotlin
val root = TreeNode("World")
val northA = TreeNode("North America")
val europe = TreeNode("Europe")
root.addChild(northA)
root.addChild(europe)

val usa = TreeNode("USA")
northA.addChild(usa)

val poland = TreeNode("Poland")
val france = TreeNode("France")
europe.addChild(poland)
europe.addChild(france)
println(root)
```

Or in more Kotlin like style:

```kotlin
val root = treeNode("World") {
            treeNode("North America") {
                treeNode("USA")
            }
            treeNode("Europe") {
                treeNode("Poland")
                treeNode("Germany")
            }
        }
```

*Output:*

```
World
├── North America
│   └── USA
└── Europe
    ├── Poland
    └── France
```


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
