# Design: Publish API reference (Dokka HTML) to GitHub Pages

- **Issue:** [#32](https://github.com/AdrianKuta/Tree-Data-Structure/issues/32) — *Publish API reference (Dokka HTML) to GitHub Pages*
- **Date:** 2026-06-07
- **Status:** Approved (design); pending spec review

## Summary

Generate a browsable, multi-module API reference with Dokka and host it on GitHub
Pages. The site aggregates the core (`tree-structure`) plus the `-serialization`,
`-coroutines`, and `-compose` modules, links every symbol back to its source on
GitHub, and is (re)deployed on each GitHub release or on demand. The README points
to it.

## Background / current state

- **Dokka 1.9.20** is applied to all four modules (root core + three submodules) via
  `alias(libs.plugins.dokka)`. Today it runs only because the **vanniktech
  maven-publish plugin (0.34.0)** uses it to build the `-javadoc.jar` that Maven
  Central requires. There is no aggregation config, no docs site, no source links.
- Plugin/library versions are centralized in `gradle/libs.versions.toml`.
- The **root project is itself the published core module** and the natural Dokka
  aggregation root.
- Existing workflows: `test.yml` (reusable matrix: JVM/JS/Wasm/Native + `apiCheck`,
  plus iOS on macOS) and `publishRelease.yml` (on GitHub release → tests →
  `./gradlew publishToMavenCentral`). Both use `actions/checkout@v4` +
  `actions/setup-java@v4` (temurin, JDK 21) with no Gradle cache action.
- Gradle wrapper **8.5**; Kotlin **2.1.0**.

### Verified compatibility (de-risking)

- **Dokka 2.2.0** (latest stable) requires **Gradle 7.6+** and **Kotlin 1.9+** → our
  8.5 / 2.1.0 satisfy both. **No wrapper bump, no Kotlin bump.**
- **vanniktech 0.34.0** already supports Dokka `V2Enabled` (added in 0.30.0), so the
  Maven Central `-javadoc.jar` keeps building under Dokka 2.x. **No vanniktech bump.**
  (For reference, 0.36.0 *removes* Dokka v1 support entirely — so V2 is the forward
  direction regardless.)

## Goals

1. Migrate Dokka `1.9.20` → `2.2.0` (DGP v2 / `V2Enabled`) without breaking the
   release pipeline's javadoc jar.
2. Produce one aggregated multi-module HTML site for all four modules.
3. Link every documented symbol to its source on GitHub.
4. Deploy the site to GitHub Pages on each release and on manual dispatch.
5. Link the site from the README.

## Non-goals (per the issue's "follow-up" note)

- Versioned / per-release docs (one published version at a time, tracking the latest
  release / manual run).
- Long-form `Module.md` package descriptions.
- Changing the publishing tool (vanniktech stays; out of scope for #32).
- Adding a Gradle cache action to CI (keep parity with existing workflows).

## Detailed design

### 1. Dokka 2.x migration

**`gradle/libs.versions.toml`**

```toml
dokka = "2.2.0"   # was 1.9.20
```

**`gradle.properties`** — enable DGP v2 and silence the migration notice:

```properties
org.jetbrains.dokka.experimental.gradle.pluginMode=V2Enabled
org.jetbrains.dokka.experimental.gradle.pluginMode.noWarn=true
```

The four `alias(libs.plugins.dokka)` plugin applications stay unchanged.

### 2. Multi-module aggregation (root `build.gradle.kts`)

Add a top-level `dependencies { }` block declaring the three submodules as Dokka
aggregation inputs. The root documents itself and pulls in the three:

```kotlin
dependencies {
    dokka(project(":tree-structure-serialization"))
    dokka(project(":tree-structure-coroutines"))
    dokka(project(":tree-structure-compose"))
}
```

- Generating task: **`:dokkaGeneratePublicationHtml`** (root project).
- Output directory: **`build/dokka/html`** (the aggregated site, default location).

### 3. Source links + site title

A `dokka { }` block is added to **each of the four module build files**. The
`sourceLink` derives the per-module GitHub path from the project layout so each
module is correct without hardcoding paths:

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

- For the **root** module, `module` is empty → links resolve to `…/blob/master/src/…`.
- For a submodule (e.g. serialization) → `…/blob/master/tree-structure-serialization/src/…`.
- Links point at the **`master`** branch. Trade-off: a previously deployed page links
  to current `master`, which may have drifted. Accepted for simplicity; tag-accurate
  links are a possible follow-up.

The **root** module additionally sets a friendly site title:

```kotlin
dokka {
    moduleName.set("Tree Data Structure")
    // ...sourceLink block as above...
}
```

Submodules keep their default module names (`tree-structure-serialization`,
`tree-structure-coroutines`, `tree-structure-compose`).

### 4. Pages workflow — `.github/workflows/docs.yml`

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

- Triggers: `release: [released]` (matches the issue) **+** `workflow_dispatch`
  (manual rebuild without a release).
- Official GitHub Pages Actions (no `gh-pages` branch).
- Runs on `ubuntu-latest` to match `publishRelease.yml`. **Fallback:** if Dokka
  cannot resolve the Apple source sets on Linux CI, switch the `build` job to
  `macos-latest` (as the iOS test job already does).

### 5. README

In the badge row near the top, add a docs badge and a one-line pointer:

```markdown
[![API docs](https://img.shields.io/badge/docs-API%20reference-blue?style=plastic)](https://adriankuta.github.io/Tree-Data-Structure/)
```

Plus a short line under the badges:

```markdown
📖 **[API reference](https://adriankuta.github.io/Tree-Data-Structure/)** — full KDoc for all modules.
```

Site URL: `https://adriankuta.github.io/Tree-Data-Structure/`.

### 6. One-time manual step (repo owner, not code)

GitHub Pages must be enabled once: **Settings → Pages → Source = "GitHub Actions"**.
Until then the `deploy` job fails. This will be called out in the PR / final summary.

## Verification (before claiming done)

1. `./gradlew :dokkaGeneratePublicationHtml --console=plain` locally on **macOS**
   (covers Apple source sets) → confirm `build/dokka/html/index.html` exists and the
   site lists **all four** modules, with working source links.
2. `./gradlew javadocJar --console=plain` → confirm the vanniktech javadoc jar(s)
   still build under Dokka 2.x (release pipeline unaffected). Optionally
   `./gradlew publishToMavenLocal -Psnapshot=true` for an end-to-end check.
3. `./gradlew apiCheck` and the existing test tasks still pass (no API/source impact).
4. Validate `docs.yml` YAML (well-formed, correct action versions).

## Risks & mitigations

| Risk | Mitigation |
| --- | --- |
| Dokka 2.x breaks the javadoc jar | vanniktech 0.34.0 supports `V2Enabled`; verify with `javadocJar` step 2 above. |
| Dokka can't resolve Apple source sets on Linux CI | Verify locally on macOS; fallback `macos-latest` for the build job. |
| Source-link paths wrong for a module | Derived from `projectDir.relativeTo(rootDir)`; visually verify links in step 1. |
| Pages deploy fails on first run | Documented manual prerequisite (Settings → Pages → GitHub Actions). |

## Files touched

- `gradle/libs.versions.toml` — Dokka version bump.
- `gradle.properties` — `V2Enabled` flags.
- `build.gradle.kts` (root) — aggregation `dependencies`, `dokka { moduleName + sourceLink }`.
- `tree-structure-serialization/build.gradle.kts` — `dokka { sourceLink }`.
- `tree-structure-coroutines/build.gradle.kts` — `dokka { sourceLink }`.
- `tree-structure-compose/build.gradle.kts` — `dokka { sourceLink }`.
- `.github/workflows/docs.yml` — new Pages workflow.
- `README.md` — docs badge + link.
