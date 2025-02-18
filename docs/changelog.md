Minor updates involving cosmetic changes have been omitted from this list. See [commits](../../commits/main)
for a full list.

## Version 8.0 - 2025/02/18

* Removed dependency on Logback.
* Renamed artifactId `root` to `pouch`.
* Renamed artifactId `core` to `pouch-core`.
* Renamed artifactId `dropwizard` to `pouch-dropwizard`.
* Renamed artifactId `jersey` to `pouch-jersey`.

## Version 7.0 - 2025/01/20

* Added SLF4J and Logback dependencies to warn about leaked child scopes.
* Updated `AbstractScope.removeChild(Scope)` to avoid throwing `IllegalStateException` if the parent scope is
  closed. This change is necessary for the cleanup of leaked child scopes.
* Updated `ConcurrentChildScope.add(Scope)` to throw `IllegalArgumentException` instead of returning a
  `boolean` to indicate that a child scope was already added to the parent.

## Version 6.0 - 2024/12/29

* Breaking change: `ConcurrentChildScopes.add()`, `remove()` now take a `Scope` argument instead of
  `AutoCloseable`.
* Added `AbstractScope`.
* Bugfix: `ConcurrentChildScopes.add()` was never returning true

## Version 5.3 - 2024/12/29

* Added `Scope`.

## Version 5.2 - 2024/12/12

* Bugfix:
  [Fail on WrappedCheckedException validation before calling super](https://github.com/cowwoc/pouch/pull/9)
  by [Berk Koprucu](https://github.com/bkoprucu)

## Version 5.1 - 2024/10/31

* Javadoc fixes.

## Version 5.0 - 2024/10/29

* `WrappedCheckedException.wrap(Runnable)` and `wrap(Callable)` now returns a wrapped `Runnable`/`Callable`
  instead of executing the task immediately.

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