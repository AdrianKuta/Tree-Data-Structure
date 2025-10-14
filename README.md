# Tree (Data Structure)
[![maven](https://img.shields.io/maven-central/v/com.github.adriankuta/tree-structure?style=plastic)](https://mvnrepository.com/artifact/com.github.adriankuta/tree-structure)
[![License: MIT](https://img.shields.io/github/license/AdrianKuta/Tree-Data-Structure?style=plastic)](https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/LICENSE)
[![Publish](https://github.com/AdrianKuta/Tree-Data-Structure/actions/workflows/publishRelease.yml/badge.svg)](https://github.com/AdrianKuta/Tree-Data-Structure/actions/workflows/publishRelease.yml)

Lightweight Kotlin Multiplatform tree data structure for Kotlin and Java. Includes a small DSL, multiple traversal iterators, and pretty-print support.

- Kotlin Multiplatform (JVM, JS, iOS, and Native host)
- Pre-order, Post-order, and Level-order iteration
- Simple DSL: tree { child(...) }
- Utilities: nodeCount(), height(), depth(), path(), prettyString(), clear(), removeChild()

## Installation

Gradle (Kotlin DSL):
```kotlin
// commonMain for KMP projects, or any sourceSet/module where you need it
dependencies {
    implementation("com.github.adriankuta:tree-structure:3.1.1") // see badge above for the latest version
}
```

Gradle (Groovy):
```groovy
dependencies {
    implementation "com.github.adriankuta:tree-structure:3.1.1" // see badge above for the latest
}
```

Maven:
```xml
<dependency>
  <groupId>com.github.adriankuta</groupId>
  <artifactId>tree-structure</artifactId>
  <version>3.1.1</version>
</dependency>
```

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

**Pretty Kotlin (DSL)**
```kotlin
val root = tree("World") {
    child("North America") { child("USA") }
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

Output:
```
World
├── North America
│   └── USA
└── Europe
    ├── Poland
    └── France
```

### Traversal and utilities
```kotlin
val root = TreeNode("root")
// ... build your tree

// Choose iteration order (default is PreOrder)
root.treeIterator = TreeNodeIterators.PostOrder
for (node in root) println(node.value)

// Utilities
root.nodeCount()   // number of descendants
root.height()      // longest path to a leaf (in edges)
root.depth()       // distance from current node to the root
val path = root.path(root.children.first()) // nodes from descendant up to root

// Mutations
val child = root.children.first()
root.removeChild(child)
root.clear()       // remove entire subtree
```

## Publishing to Maven Central (central.sonatype.com)

This project is configured to publish artifacts to Maven Central via the Sonatype Central Portal.

There are two supported ways to publish:

1) Via GitHub Actions (recommended)
- Create a GitHub Release (tag) in this repository. When a release is published, the workflow .github/workflows/publishRelease.yml runs automatically.
- The workflow uses the Gradle task publishToMavenCentral to upload artifacts through the Central Portal.
- Make sure these repository secrets are configured in GitHub:
  - MAVEN_CENTRAL_USERNAME — Your Sonatype Central username (not email).
  - MAVEN_CENTRAL_PASSWORD — Your Sonatype Central password or a token from central.sonatype.com.
  - SIGNING_KEY — ASCII‑armored GPG private key (exported, single line; for in‑memory signing).
  - SIGNING_PASSWORD — Passphrase for the key above.
- The workflow uses JDK 21 and publishes the version defined in build.gradle.kts.

2) Locally via Gradle
- Ensure you have a Sonatype Central account and that the groupId com.github.adriankuta is verified in central.sonatype.com (Namespace Rules → Verify).
- Export the same credentials/signing values as environment variables or pass them as Gradle properties:
  - ORG_GRADLE_PROJECT_mavenCentralUsername
  - ORG_GRADLE_PROJECT_mavenCentralPassword
  - ORG_GRADLE_PROJECT_signingInMemoryKey
  - ORG_GRADLE_PROJECT_signingInMemoryKeyPassword
- Then run:
  - ./gradlew publishToMavenCentral
- For snapshot publishing, set -Psnapshot=true (the version is derived from PUBLISH_VERSION with -SNAPSHOT).

Notes
- Publishing is powered by the com.vanniktech.maven.publish Gradle plugin and Sonatype Central Portal (no legacy Nexus staging URLs needed).
- The plugin is configured to sign all publications. Coordinates and POM metadata are defined in build.gradle.kts.
- If using the combined task is preferred, you can also run publishAndReleaseToMavenCentral when automatic release is enabled; this repository currently uploads with publishToMavenCentral from CI.

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
