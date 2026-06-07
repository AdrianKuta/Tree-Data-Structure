# API Reference (Dokka HTML) on GitHub Pages — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate Dokka to 2.x and publish an aggregated, source-linked multi-module API reference for all four modules to GitHub Pages on each release and on demand.

**Architecture:** Bump Dokka `1.9.20 → 2.2.0` and switch on the Dokka Gradle Plugin v2 (`V2Enabled`). The root project (the published core module) aggregates the three submodules via `dokka(project(...))` dependencies, producing one HTML site at `build/dokka/html`. A new `docs.yml` workflow builds that site and deploys it with the official GitHub Pages Actions. The vanniktech maven-publish plugin (0.34.0) keeps building the Maven Central `-javadoc.jar` — it already supports Dokka V2, so the release pipeline is unaffected.

**Tech Stack:** Kotlin Multiplatform 2.1.0, Gradle 8.5, Dokka Gradle Plugin 2.2.0, vanniktech maven-publish 0.34.0, GitHub Actions (`upload-pages-artifact@v3`, `deploy-pages@v4`).

**Spec:** `docs/superpowers/specs/2026-06-07-publish-api-reference-dokka-github-pages-design.md`

> **Note on "tests":** This is build/CI work, so each task's verification is a Gradle command or a YAML lint with a concrete expected result rather than a unit test. Treat the "verify" steps as the failing/passing check.

---

## File Structure

| File | Responsibility | Action |
| --- | --- | --- |
| `gradle/libs.versions.toml` | Centralized Dokka version | Modify (`dokka = "2.2.0"`) |
| `gradle.properties` | Enable DGP v2 plugin mode | Modify (add 2 flags) |
| `build.gradle.kts` (root) | Aggregate 3 submodules; root site title + source links | Modify (add `dependencies` + `dokka {}` blocks) |
| `tree-structure-serialization/build.gradle.kts` | Source links for this module | Modify (add `dokka {}` block) |
| `tree-structure-coroutines/build.gradle.kts` | Source links for this module | Modify (add `dokka {}` block) |
| `tree-structure-compose/build.gradle.kts` | Source links for this module | Modify (add `dokka {}` block) |
| `.github/workflows/docs.yml` | Build + deploy site to Pages | Create |
| `README.md` | Docs badge + link | Modify |

Work happens on branch `docs/api-reference-github-pages` (already created; spec already committed there).

---

## Task 1: Migrate Dokka to 2.2.0 and enable DGP v2

**Files:**
- Modify: `gradle/libs.versions.toml` (line `dokka = "1.9.20"`)
- Modify: `gradle.properties`

- [ ] **Step 1: Bump the Dokka version in the catalog**

In `gradle/libs.versions.toml`, change the `dokka` version under `[versions]`:

```toml
dokka = "2.2.0"
```

(Leave the `dokka = { id = "org.jetbrains.dokka", version.ref = "dokka" }` plugin line unchanged.)

- [ ] **Step 2: Enable the Dokka Gradle Plugin v2**

Append these two lines to `gradle.properties` (current content is only `kotlin.code.style=official`):

```properties
# Dokka Gradle Plugin v2 (https://kotl.in/dokka-gradle-migration)
org.jetbrains.dokka.experimental.gradle.pluginMode=V2Enabled
org.jetbrains.dokka.experimental.gradle.pluginMode.noWarn=true
```

- [ ] **Step 3: Verify the v2 task exists and there is no V1 warning**

Run: `./gradlew :dokkaGeneratePublicationHtml --dry-run --console=plain`

Expected: `BUILD SUCCESSFUL`, a list of `:dokkaGenerate*` tasks printed, and **no** message containing `Dokka Gradle plugin V1` or `migration guide`. (At this point only the root/core module is documented — aggregation is added in Task 2.)

- [ ] **Step 4: Verify the Maven Central javadoc jar still builds under Dokka 2.x**

Run: `./gradlew javadocJar --console=plain`

Expected: `BUILD SUCCESSFUL`. This is the vanniktech-generated jar that Maven Central requires; it must keep working. (If the task name isn't found, list it with `./gradlew tasks --all | grep -i javadoc` and run the reported task — it is the per-module `javadocJar`.)

- [ ] **Step 5: Commit**

```bash
git add gradle/libs.versions.toml gradle.properties
git commit -m "build: migrate Dokka 1.9.20 -> 2.2.0 (DGP v2) (#32)"
```

---

## Task 2: Aggregate all modules + root site title and source links

**Files:**
- Modify: `build.gradle.kts` (root)

- [ ] **Step 1: Add the Dokka aggregation dependencies**

Add this top-level block to the root `build.gradle.kts` (place it after the `repositories { mavenCentral() }` block, at the top level — not inside `kotlin {}`):

```kotlin
dependencies {
    dokka(project(":tree-structure-serialization"))
    dokka(project(":tree-structure-coroutines"))
    dokka(project(":tree-structure-compose"))
}
```

- [ ] **Step 2: Add the root `dokka {}` configuration (site title + source links)**

Add this top-level block to the root `build.gradle.kts` (e.g. right after the `dependencies {}` block from Step 1):

```kotlin
dokka {
    moduleName.set("Tree Data Structure")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            val module = projectDir.relativeTo(rootDir).invariantSeparatorsPath
            val prefix = if (module.isEmpty()) "" else "$module/"
            remoteUrl("https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/${prefix}src")
            remoteLineSuffix.set("#L")
        }
    }
}
```

- [ ] **Step 3: Build the aggregated site**

Run: `./gradlew :dokkaGeneratePublicationHtml --console=plain`

Expected: `BUILD SUCCESSFUL` and the file `build/dokka/html/index.html` exists.

Run: `ls build/dokka/html`

Expected: per-module output directories including `tree-structure`, `tree-structure-serialization`, `tree-structure-coroutines`, and `tree-structure-compose` (plus `index.html`, `navigation.html`, assets).

- [ ] **Step 4: Verify the four modules and root source links are present**

Run: `grep -roh "tree-structure-serialization\|tree-structure-coroutines\|tree-structure-compose" build/dokka/html/index.html | sort -u`

Expected: all three submodule names listed (confirming aggregation).

Run: `grep -rl "github.com/AdrianKuta/Tree-Data-Structure/blob/master/src/" build/dokka/html/tree-structure | head -1`

Expected: at least one file path printed (confirming the root/core module's source links resolve to `.../blob/master/src/...`). Optionally open `build/dokka/html/index.html` in a browser and click a core class's "source" link to confirm it lands on the right GitHub file.

- [ ] **Step 5: Commit**

```bash
git add build.gradle.kts
git commit -m "docs: aggregate all modules into one Dokka HTML site with source links (#32)"
```

---

## Task 3: Source links for the three submodules

The submodules are documented by the aggregation but need their own `sourceLink` so their symbols point at the correct subdirectory on GitHub. The block below is **path-derived and identical** for every module — add the exact same block to all three files.

**Files:**
- Modify: `tree-structure-serialization/build.gradle.kts`
- Modify: `tree-structure-coroutines/build.gradle.kts`
- Modify: `tree-structure-compose/build.gradle.kts`

- [ ] **Step 1: Add the `dokka {}` block to `tree-structure-serialization/build.gradle.kts`**

Add this top-level block (after the `repositories {}` block, before `kotlin {}`):

```kotlin
dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            val module = projectDir.relativeTo(rootDir).invariantSeparatorsPath
            val prefix = if (module.isEmpty()) "" else "$module/"
            remoteUrl("https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/${prefix}src")
            remoteLineSuffix.set("#L")
        }
    }
}
```

- [ ] **Step 2: Add the same block to `tree-structure-coroutines/build.gradle.kts`**

Add the identical block (after `repositories {}`, before `kotlin {}`):

```kotlin
dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            val module = projectDir.relativeTo(rootDir).invariantSeparatorsPath
            val prefix = if (module.isEmpty()) "" else "$module/"
            remoteUrl("https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/${prefix}src")
            remoteLineSuffix.set("#L")
        }
    }
}
```

- [ ] **Step 3: Add the same block to `tree-structure-compose/build.gradle.kts`**

Add the identical block (after the `repositories { mavenCentral(); google() }` block, before `kotlin {}`):

```kotlin
dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            val module = projectDir.relativeTo(rootDir).invariantSeparatorsPath
            val prefix = if (module.isEmpty()) "" else "$module/"
            remoteUrl("https://github.com/AdrianKuta/Tree-Data-Structure/blob/master/${prefix}src")
            remoteLineSuffix.set("#L")
        }
    }
}
```

- [ ] **Step 4: Rebuild the site**

Run: `./gradlew :dokkaGeneratePublicationHtml --console=plain`

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Verify each submodule's source links resolve to its subdirectory**

Run:
```bash
for m in serialization coroutines compose; do
  echo -n "tree-structure-$m: "
  grep -rl "github.com/AdrianKuta/Tree-Data-Structure/blob/master/tree-structure-$m/src/" build/dokka/html/tree-structure-$m | head -1 || echo "MISSING"
done
```

Expected: a file path printed for each of the three modules (none "MISSING").

- [ ] **Step 6: Commit**

```bash
git add tree-structure-serialization/build.gradle.kts tree-structure-coroutines/build.gradle.kts tree-structure-compose/build.gradle.kts
git commit -m "docs: add Dokka source links to serialization/coroutines/compose modules (#32)"
```

---

## Task 4: GitHub Pages workflow

**Files:**
- Create: `.github/workflows/docs.yml`

- [ ] **Step 1: Create the workflow file**

Create `.github/workflows/docs.yml` with exactly:

```yaml
name: Docs

on:
  release:
    types: [released]
  workflow_dispatch:

permissions:
  contents: read
  pages: write
  id-token: write

concurrency:
  group: pages
  cancel-in-progress: false

jobs:
  build:
    name: Build Dokka HTML
    runs-on: ubuntu-latest
    steps:
      - name: Check out code
        uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
      - name: Generate API docs
        run: ./gradlew :dokkaGeneratePublicationHtml --console=plain
      - name: Upload Pages artifact
        uses: actions/upload-pages-artifact@v3
        with:
          path: build/dokka/html

  deploy:
    name: Deploy to GitHub Pages
    needs: build
    runs-on: ubuntu-latest
    environment:
      name: github-pages
      url: ${{ steps.deployment.outputs.page_url }}
    steps:
      - name: Deploy
        id: deployment
        uses: actions/deploy-pages@v4
```

- [ ] **Step 2: Validate the YAML is well-formed**

Run: `ruby -ryaml -e "YAML.load_file('.github/workflows/docs.yml'); puts 'OK'"`

Expected: prints `OK` with no error. (Ruby ships with macOS. Alternatively, if `actionlint` is installed: `actionlint .github/workflows/docs.yml` → no output.)

- [ ] **Step 3: Commit**

```bash
git add .github/workflows/docs.yml
git commit -m "ci: add docs workflow to deploy Dokka HTML to GitHub Pages (#32)"
```

---

## Task 5: Link the site from the README

**Files:**
- Modify: `README.md` (top badge block, lines 1-6)

- [ ] **Step 1: Add the API docs badge**

In `README.md`, immediately after the existing `Publish` badge line:

```markdown
[![Publish](https://github.com/AdrianKuta/Tree-Data-Structure/actions/workflows/publishRelease.yml/badge.svg)](https://github.com/AdrianKuta/Tree-Data-Structure/actions/workflows/publishRelease.yml)
```

add this new line:

```markdown
[![API docs](https://img.shields.io/badge/docs-API%20reference-blue?style=plastic)](https://adriankuta.github.io/Tree-Data-Structure/)
```

- [ ] **Step 2: Add a one-line pointer under the badges**

After the badge block and its following blank line, and **before** the intro paragraph that starts `A lightweight n-ary tree for Kotlin Multiplatform.`, insert:

```markdown
📖 **[API reference](https://adriankuta.github.io/Tree-Data-Structure/)** — full KDoc for the core and all modules.

```

- [ ] **Step 3: Verify the link is present**

Run: `grep -n "adriankuta.github.io/Tree-Data-Structure" README.md`

Expected: two matches (the badge and the 📖 line).

- [ ] **Step 4: Commit**

```bash
git add README.md
git commit -m "docs: link the published API reference from the README (#32)"
```

---

## Task 6: Full verification, push, and pull request

**Files:** none (verification + integration)

- [ ] **Step 1: Full local verification**

Run each and confirm `BUILD SUCCESSFUL`:

```bash
./gradlew :dokkaGeneratePublicationHtml --console=plain   # site builds, all 4 modules
./gradlew javadocJar --console=plain                       # release javadoc jar intact
./gradlew apiCheck --console=plain                         # binary-compat baseline unchanged
```

Expected: all three succeed. `apiCheck` must pass with no diff — this change touches only build config and docs, not public API. Optionally open `build/dokka/html/index.html` in a browser for a final visual check (module nav + a couple of source links).

- [ ] **Step 2: Push the branch**

```bash
git push -u origin docs/api-reference-github-pages
```

- [ ] **Step 3: Open the pull request**

```bash
gh pr create --base master --head docs/api-reference-github-pages \
  --title "Publish API reference (Dokka HTML) to GitHub Pages (#32)" \
  --body "$(cat <<'EOF'
Closes #32.

Migrates Dokka 1.9.20 → 2.2.0 (DGP v2) and publishes an aggregated, source-linked
multi-module API reference to GitHub Pages.

## What changed
- **Dokka 2.2.0 / DGP v2** (`V2Enabled` in `gradle.properties`). Gradle 8.5 and Kotlin
  2.1.0 already satisfy Dokka 2.2.0's minimums (7.6+ / 1.9+), so no wrapper/Kotlin bump.
- **Aggregation**: the root (core) module pulls in `-serialization`, `-coroutines`, and
  `-compose` via `dokka(project(...))` → one site at `build/dokka/html`
  (`:dokkaGeneratePublicationHtml`).
- **Source links** on every module, pointing each symbol at its source on `master`.
- **`.github/workflows/docs.yml`**: builds the site and deploys via the official Pages
  Actions on each release and on manual `workflow_dispatch`.
- **README**: docs badge + link to https://adriankuta.github.io/Tree-Data-Structure/

## Release pipeline unaffected
vanniktech 0.34.0 already supports Dokka `V2Enabled`, so the Maven Central
`-javadoc.jar` keeps building (verified locally with `./gradlew javadocJar`).

## ⚠️ One-time manual step required before the site goes live
Enable Pages: **Settings → Pages → Source = "GitHub Actions"**. Until then the
`deploy` job will fail. After enabling, run the **Docs** workflow once via
*Actions → Docs → Run workflow* (`workflow_dispatch`) to publish without waiting for a
release.
EOF
)"
```

Expected: PR URL printed.

- [ ] **Step 4: Post-merge manual steps (call these out to the repo owner)**

1. **Settings → Pages → Source = "GitHub Actions"** (one-time; otherwise `deploy` fails).
2. Trigger **Actions → Docs → Run workflow** (`workflow_dispatch`) to publish immediately.
3. Confirm the site is live at https://adriankuta.github.io/Tree-Data-Structure/ and the module nav + source links work.

---

## Self-Review

**Spec coverage:**
- Dokka 1.9.20 → 2.2.0 + V2 mode → Task 1 ✓
- Multi-module aggregation in root → Task 2 ✓
- Source links (root + 3 submodules) + site title → Tasks 2 & 3 ✓
- `docs.yml` (release + workflow_dispatch, official Pages Actions) → Task 4 ✓
- README badge + link → Task 5 ✓
- Verify javadoc jar / apiCheck / local docs build → Tasks 1, 2, 6 ✓
- Manual Pages-enable prerequisite → Tasks 4/6 PR body + post-merge steps ✓
- Out-of-scope items (versioned docs, Module.md, vanniktech swap, CI cache) → correctly excluded ✓

**Placeholder scan:** No TBD/TODO/"handle edge cases"/"similar to Task N". The identical source-link block is repeated in full in each of Task 3's steps (not referenced) ✓

**Type/name consistency:** Task name `:dokkaGeneratePublicationHtml`, output dir `build/dokka/html`, config functions (`moduleName.set`, `dokkaSourceSets.configureEach`, `sourceLink { localDirectory.set / remoteUrl / remoteLineSuffix.set }`), and `dokka(project(...))` aggregation are used identically across Tasks 1–6 and the workflow ✓

**Open risk carried from spec:** if Dokka cannot resolve Apple source sets on `ubuntu-latest` in CI (Task 4), switch the `build` job's `runs-on` to `macos-latest` (mirrors the iOS test job). Local verification runs on macOS, which covers Apple source sets.
