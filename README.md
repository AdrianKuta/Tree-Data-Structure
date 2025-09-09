# Tree (Data Structure)
[![maven](https://img.shields.io/maven-central/v/com.github.adriankuta/tree-structure?style=plastic)](https://mvnrepository.com/artifact/com.github.adriankuta/tree-structure)
[![License: MIT](https://img.shields.io/github/license/AdrianKuta/Tree-Data-Structure?style=plastic)](https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/LICENSE)
[![Publish](https://github.com/AdrianKuta/Tree-Data-Structure/actions/workflows/publishRelease.yml/badge.svg)](https://github.com/AdrianKuta/Tree-Data-Structure/actions/workflows/publish.yml)

Simple implementation to store object in tree structure.

## Usage

**Kotlin**
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
println(root.prettyString())
```

**Pretty Kotlin**

```kotlin
val root =
    tree("World") {
        child("North America") {
            child("USA")
        }
        child("Europe") {
            child("Poland")
            child("Germany")
        }
    }
```

**Java**
```java
TreeNode<String> root = new TreeNode<>("World");
TreeNode<String> northA = new TreeNode<>("North America");
TreeNode<String> europe = new TreeNode<>("Europe");
root.addChild(northA);
root.addChild(europe);

TreeNode<String> usa = new TreeNode<>("USA");
northA.addChild(usa);

TreeNode<String> poland = new TreeNode<>("Poland");
TreeNode<String> france = new TreeNode<>("France");
europe.addChild(poland);
europe.addChild(france);
System.out.println(root.prettyString());
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

---

## Publishing (Maven Central migration)

This project is configured to publish via Sonatype's s01.oss.sonatype.org (Nexus) which is compatible with the new Central (central.sonatype.com). The old oss.sonatype.org host is no longer used.

Environment variables supported by the build:
- CENTRAL_USERNAME / CENTRAL_PASSWORD — Central Portal user/token (preferred)
- OSSRH_USERNAME / OSSRH_PASSWORD — legacy credentials (fallback)
- SIGNING_KEY_ID / SIGNING_KEY / SIGNING_PASSWORD — PGP signing (ASCII-armored key)
- SNAPSHOT — set to true to append -SNAPSHOT to version

Gradle tasks:
- Publish all publications to Sonatype: `./gradlew publishAllPublicationsToSonatypeS01Repository`
- Or, standard publish (selects snapshots vs releases by version): `./gradlew publish`

Notes:
- Releases are uploaded to: https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
- Snapshots are uploaded to: https://s01.oss.sonatype.org/content/repositories/snapshots/
- Staging/release close and promote are handled by Sonatype. If you use CI, set the env vars in your secrets.
