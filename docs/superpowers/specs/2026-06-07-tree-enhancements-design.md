# Design — four additive enhancements to `tree-structure` (target 4.1.0)

Date: 2026-06-07. Source issues: #34, #35, #36, #33. Integration: **four separate PRs**,
one per issue (`Closes #NN`), matching the repo's per-issue PR convention (#32).

## Shared rules (every issue)

- `explicitApi()` is on everywhere → every new public declaration is `public` with KDoc.
- New tests live in `commonTest`, use `kotlin("test")`, and mirror the existing test style
  (`TreeNodeV4Test.kt`, `TreeNodeNavigationTest.kt`, …).
- Add a bullet to `CHANGELOG.md` under `## [Unreleased]` → `### Added` (do **not** bump
  `PUBLISH_VERSION`; releases are a separate manual step).
- Regenerate the binary-compatibility baseline with `./gradlew apiDump`; verify with `apiCheck`.
- Do not modify the core public surface in a breaking way — these are additive only.

## #34 — Structural mutation helpers → **members on `TreeNode`**

Members (not extensions) because they need the private `_children`/`_parent` and the cycle/parent
validation.

- Extract the existing inline validation from `addChild` into a private
  `validateAttachable(child)` and reuse it.
- `insertChild(index: Int, child)` — validated insert at index (bounds `0.._children.size`).
- `removeChildAt(index: Int): TreeNode<T>` — remove and return the detached child.
- `replaceChild(index: Int, child): TreeNode<T>` — swap, return the old (now detached) child.
- `moveChild(child, toIndex): Boolean` — reorder an existing direct child; no re-parent/cycle
  check needed (it is already a child); `false` if `child` is not a direct child.
- `addChildren(vararg children)` — validated append of several (per-child `addChild` semantics).
- `sortChildren(comparator: Comparator<TreeNode<T>>)` — stable in-place reorder.

## #35 — Query algorithms → **extensions** in new `TreeNodeQueryExt.kt`

Built on the public API (`ancestors()`, `parent`, `depth()`, sequences). Return `null` when the
two nodes are unrelated (different trees).

- `lowestCommonAncestor(other): TreeNode<T>?` — deepest common node; includes self/other as
  candidates (`LCA(a, a) == a`, `LCA(a, descendantOfA) == a`).
- `distance(other): Int?` — `depth(this) + depth(other) - 2 * depth(lca)`.
- `pathBetween(other): List<TreeNode<T>>?` — `[this … lca … other]`.
- `contains(value): Boolean` — value search over the subtree, including the receiver.

Document complexity (parent-walk based; O(depth) for LCA/distance, O(n) for `contains`).

## #36 — Customizable `prettyString()` → **extension** in new `TreeNodePrettyPrintExt.kt`

- `data class TreeConnectors(branch, lastBranch, vertical, empty)` with a `companion`:
  `Default` (current Unicode box-drawing) and `Ascii`.
- `fun TreeNode<T>.prettyString(connectors = TreeConnectors.Default,
  render: (value: T, depth: Int, isLast: Boolean) -> String = { v, _, _ -> v.toString() }): String`.
- The no-arg member `prettyString()` is unchanged; member resolution wins for `node.prettyString()`,
  so existing behaviour and output are byte-for-byte identical.

## #33 — Immutable variant → **new module `:tree-structure-immutable`**

Largest item; fully isolated from the other three except for `settings.gradle.kts`,
`libs.versions.toml`, the root `build.gradle.kts` Dokka aggregation block, and `CHANGELOG.md`.

- `settings.gradle.kts`: `include(":tree-structure-immutable")`.
- New `build.gradle.kts` mirroring the serialization module's KMP target matrix; deps
  `api(project(":"))` + `kotlinx-collections-immutable`.
- `libs.versions.toml`: add `kotlinx-collections-immutable` (a recent stable, e.g. 0.3.8) and
  wire it.
- Root `build.gradle.kts`: add `dokka(project(":tree-structure-immutable"))` to the aggregation.
- `ImmutableTreeNode<T>` backed by `persistentListOf` children; `addChild` / `removeChild` /
  `mapValues` return a new root with unchanged subtrees structurally shared. Mirror pre/post/
  level-order traversal helpers.
- Its own `tree-structure-immutable/api/` baseline via `apiDump`.

## Orchestration

- Four git worktrees off `origin/master`, one branch each (`feat/...`), for full isolation — no
  collisions on `TreeNode.kt`, `api/*.api`, or `CHANGELOG.md`.
- A workflow pipelines each issue: implement (code + KDoc + tests + CHANGELOG + `apiDump`) and
  verify locally (`jvmTest` + `apiCheck`), then an adversarial reviewer checks issue-conformance,
  test quality, invariants, explicit API, `.api` freshness, and that it builds. Local checks are
  JVM-only for speed; CI validates the full KMP matrix on each PR.
- Then push each passing branch and open a PR (`Closes #NN`); remove the worktrees afterward.
