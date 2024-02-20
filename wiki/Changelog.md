Minor updates involving cosmetic changes have been omitted from this list. See [commits](../../commits/master)
for a full list.

## Version 4.4 - 2024/02/20

* Updated dependencies

## Version 4.3 - 2024/02/19

* Restored `WrappedCheckedException.wrap(Callable)` and `wrap(Task)`

## Version 4.2 - 2024/02/19 (skipped)

## Version 4.1 - 2024/02/19

* Changes
    * `ConcurrentChildScopes.shutdown()` throws an exception if the thread is interrupted while waiting for
      child scopes to shut down.
* Improvements
    * Removed slf4j dependency.

## Version 4.0 - 2023/07/10

* Breaking changes
    * Replaced `ConcurrentChildScopes.createChildScope()` with `add()`, and `ConcurrentChildScopes.onClosed()`
      with `remove()`.
    * `Scopes.runAll()` now accepts tasks that throw checked exceptions.

## Version 3.0 - 2023/02/28

* Breaking changes
    * Replaced `Closeables` with `WrappedCheckedException`.
* Improvements
    * The library now requires JDK 8 instead of JDK 11.

## Version 2.1 - 2020/03/30

* Breaking changes
    * Changed groupId from `com.github.cowwoc` to `com.github.cowwoc.pouch`.
    * Changed artifactId from `pouch.core` to `core`.

## Version 2.0 - 2020/03/24

* Breaking changes
    * Migrated to GitHub. The new groupId is `com.github.cowwoc`. The new package name is
      `com.github.cowwoc.pouch.core`.