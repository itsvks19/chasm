# Wasm Corpus Plugin

`corpus` is an included Gradle build that generates Kotlin tests from
[`CharlieTap/wasm-corpus`](https://github.com/CharlieTap/wasm-corpus) fixtures.
The generated tests live under the consuming project's build directory and call
a runtime-owned `CorpusRunner`, so Chasm always tests the local source tree
rather than a published artifact.

## Chasm Tasks

```sh
./gradlew :chasm:generateCorpusTests
./gradlew :chasm:jvmTest --tests '*corpus.generated*'
./gradlew corpus
```

Chasm runs invocation fixtures for its configured core Wasm versions while
excluding unsupported features and explicitly excluded targets. The `corpus {}`
block in `chasm/build.gradle.kts` can filter by versions, source languages,
required or excluded features, included or excluded canonical tags, maximum
binary size, maximum test duration, and target names.

`cleanCorpusTests` removes the generated fixture index and test sources. The
synced corpus checkout is left in place so repeated runs do not need to reclone.

## Plugin Shape

The plugin registers:

- `syncWasmCorpus` to clone/fetch and checkout the pinned corpus ref.
- `resolveCorpusFixtures` to invoke the corpus repository's Node CLI.
- `generateCorpusTests` to emit generated Kotlin tests.
- `corpusMatrix` to print fixture counts by version.
- `cleanCorpusTests` to remove generated fixture metadata and tests.

Generated tests treat `CorpusResult.Skipped` as an explicit skip path and fail
only on `CorpusResult.Failure`.
