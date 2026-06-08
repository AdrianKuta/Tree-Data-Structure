# Changelog

All notable changes to this project are documented here. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [4.1.1] - 2026-06-08

### Fixed
- Restored the Apple/iOS artifacts (`iosArm64`, `iosX64`, `iosSimulatorArm64`) for the core and every
  module. The release workflow published from a Linux runner, which cannot build Kotlin/Native Apple
  targets, so they were silently omitted from 3.1.5–4.1.0; publishing now runs on macOS. No API or
  behavior changes from 4.1.0 — this is a packaging fix only.

## [4.1.0] - 2026-06-07

### Added
- Structural mutation helpers on `TreeNode`: `insertChild`, `removeChildAt`, `replaceChild`,
  `moveChild`, `addChildren`, and `sortChildren`.
- Tree query extensions: `lowestCommonAncestor`, `distance`, `pathBetween`, and `contains` for
  finding common ancestors, edge distances, the path between two nodes, and value membership.
- Customizable `prettyString(connectors, render)` extension: choose connector glyphs via
  `TreeConnectors` (`Default` box-drawing or `Ascii`) and supply a per-node renderer that receives
  the value, its depth and whether it is its parent's last child. The all-defaults call is
  byte-identical to the existing no-arg `prettyString()`.
- New `tree-structure-immutable` module: a persistent `ImmutableTreeNode` with structural sharing
  (`addChild`/`removeChild`/`mapValues` return new roots; pre/post/level-order traversals,
  `nodeCount`, and `height`).

### Changed
- Rewrote the README for clarity: one consistent example tree, task-oriented sections
  (building, traversal, navigation, functional, utilities, mutating), per-module usage, and a
  condensed maintainer "Releasing" section.

## [4.0.0]

A breaking release that cleans up the core API and enforces an explicit public surface.

### Changed (breaking)
- `TreeNode.treeIterator` is now a read-only `val` (set it via the constructor). Use
  `iterator(order)` or `asSequence(order)` to traverse in a different order per call.
- `removeChild(child)` now only removes a **direct** child of the receiver (previously it removed
  the node from its actual parent regardless). Use `child.detach()` to unhook a node from wherever
  it lives.
- `addChild(child)` now throws `TreeNodeException` if `child` already has a parent or if the
  attachment would create a cycle. Call `detach()` first to move a node.
- `clear()` no longer detaches the receiver from its own parent; it only removes its descendants.
- `path(descendant)` now returns `List<TreeNode<T>>?` (`null` when `descendant` is the root or not a
  descendant) instead of throwing `TreeNodeException`.

### Added
- `TreeNode.detach()` — removes a node from its parent.
- `TreeNode.iterator(order)` — a one-shot iterator in a specific order.
- Strict `explicitApi()` mode across all modules.
- New `tree-structure-compose` module: a `LazyTree` composable for Compose Multiplatform.

### Migration
- `node.treeIterator = PostOrder; for (n in node) { … }` → `for (n in node.asSequence(PostOrder)) { … }`
- `root.removeChild(deepNode)` → `deepNode.detach()`
- `try { node.path(x) } catch (e: TreeNodeException) { … }` → `node.path(x)?.let { … }`

## [3.4.0]

### Added
- Lazy `Sequence` traversal: `asSequence(order)`, `preOrderSequence()`, `postOrderSequence()`,
  `levelOrderSequence()` — composes with the Kotlin stdlib and short-circuits.
- Navigation extensions: `isLeaf`, `degree`, `root()`, `ancestors()`, `siblings()`, `leaves()`,
  `descendants()`.
- Functional extensions: `findNode`, `filterNodes`, `anyNode`, `allNodes`, `countNodes`,
  `foldNodes`, `mapValues`, `deepCopy`, `structurallyEquals` (all stack-safe).
- New optional modules published as separate artifacts:
  - `tree-structure-serialization` — `kotlinx.serialization` support via a `TreeNodeDto`.
  - `tree-structure-coroutines` — `Flow` traversal (`asFlow`, `preOrderFlow`, …).
- `CHANGELOG.md`, expanded README examples, and class-level KDoc (thread-safety / complexity).

### Changed
- `nodeCount()`, `height()`, `clear()` and the post-order iterator are now iterative — deep or
  degenerate (linear) trees no longer throw `StackOverflowError`.
- Migrated to Kotlin 2.x (K2 compiler) and introduced a Gradle version catalog.
- Build now uses `binary-compatibility-validator` (committed `.api` baselines) and Kover.

## [3.1.5]

### Fixed
- Removed a stray `println` in `TreeNode.removeChild()` that printed to stdout on every removal.

### Removed
- Deleted the `Example.ws.kts` worksheet and the `kotlin("script-runtime")` dependency from the
  published JVM artifact, plus the leftover `ExampleUnitTest` template test.

### Changed
- Bumped `actions/checkout` v2 → v4 in CI workflows.

## [3.1.4]
- Updated Kotlin and JS dependencies; added the `wasmJs` target.

## [3.1.3]
- iOS targets and Maven Central (Sonatype Central Portal) publishing.

[Unreleased]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v4.1.1...HEAD
[4.1.1]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v4.1.0...v4.1.1
[4.1.0]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v4.0.0...v4.1.0
[4.0.0]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v3.4.0...v4.0.0
[3.4.0]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v3.1.5...v3.4.0
[3.1.5]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v3.1.3...v3.1.5
[3.1.4]: https://github.com/AdrianKuta/Tree-Data-Structure/releases/tag/v3.1.4
[3.1.3]: https://github.com/AdrianKuta/Tree-Data-Structure/releases/tag/v3.1.3
