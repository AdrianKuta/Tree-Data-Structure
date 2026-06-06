# Changelog

All notable changes to this project are documented here. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

[Unreleased]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v3.4.0...HEAD
[3.4.0]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v3.1.5...v3.4.0
[3.1.5]: https://github.com/AdrianKuta/Tree-Data-Structure/compare/v3.1.3...v3.1.5
[3.1.4]: https://github.com/AdrianKuta/Tree-Data-Structure/releases/tag/v3.1.4
[3.1.3]: https://github.com/AdrianKuta/Tree-Data-Structure/releases/tag/v3.1.3
