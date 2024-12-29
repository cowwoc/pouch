[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.cowwoc.pouch/java/badge.svg)](https://search.maven.org/search?q=g:com.github.cowwoc.pouch) [![API](https://img.shields.io/badge/api_docs-5B45D5.svg)](http://cowwoc.github.io/pouch/5.4/docs/api/) [![Changelog](https://img.shields.io/badge/changelog-A345D5.svg)](docs/Changelog.md)
[![build-status](../../workflows/Build/badge.svg)](../../actions?query=workflow%3ABuild)

# <img alt="pouch" src="docs/pouch.svg" width="128" height="146"/> Pouch: Inversion of Control for the Masses

An [Inversion of Control](http://martinfowler.com/articles/injection.html) design pattern that is:

* Easy to use (no magic!)
  * No config files
  * No reflection
  * No bytecode generation
  * No proxies
  * No annotations
* Easy to debug (dependency graph is verified at compile-time)
* Dependency-free

To get started, add this Maven dependency:

```xml
<dependency>
  <groupId>com.github.cowwoc.pouch</groupId>
  <artifactId>core</artifactId>
  <version>5.4</version>
</dependency>
```

# Getting Started

## Scopes

[Service Locators](docs/frequently_asked_questions.md#not-your-mothers-service-locator) are registries
that contain one or more *values*.
Values can be bound to one or more *scopes*.
A scope is the context within which a value is defined.
Scopes guarantee that values will remain unchanged during their lifetime.

## Example

Pouch doesn't advocate specific designs. You're expected to use whatever design best suits your use-case.
Here is one design that has worked for me:

### Scope Types

```java
public enum RunMode
{
  DEBUG,
  RELEASE
}
```

```java
import com.github.cowwoc.pouch.core.Scope;

public interface JvmScope extends Scope
{
  RunMode getRunMode();
}
```

```java
import javax.sql.DataSource;

public interface DatabaseScope extends JvmScope
{
  DataSource getDataSource();
}
```

```java
import javax.sql.DataSource;

public interface TransactionScope extends DatabaseScope
{
  Connection getConnection();
}
```

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ServerScope extends DatabaseScope
{
  HttpScope createRequest(HttpServletRequest request, HttpServletResponse response);
}
```

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RequestScope extends ServerScope
{
  HttpServletRequest getRequest();

  HttpServletResponse getResponse();
}
```

```java
import org.eclipse.jetty.client.HttpClient;

import java.net.URI;

public interface ClientScope extends Scope
{
  URI getServer();

  HttpClient getHttpClient();
}
```

### Scope Inheritance

Imagine we have:

* `JvmScope`: values specific to the lifetime of the current JVM
* `DatabaseScope`: values specific to the lifetime of the current database connection
* `RequestScope`: values specific to the lifetime of the current HTTP request

Notice that a `JvmScope` contains values whose lifetime span multiple database connections.
Further, `DatabaseScope` contains values whose lifetime span multiple HTTP requests.
In light of this, we define `RequestScope` as extending `DatabaseScope` and `DatabaseScope` as extending
`JvmScope`.
When a new `RequestScope` is constructed, we pass in the parent scope.

The lifetime of child scopes must be equal to or less than the lifetime of the parent scope.
This means that child scopes can't exist without the parent.
For example, the above conceptual model assumes that
an `RequestScope` cannot exist without a `DatabaseScope` but a `DatabaseScope` may exist without an `RequestScope`.
This enables a worker thread to interact with the database outside an HTTP request.
But an HTTP request can't exist without a database connection.

When a child scope is asked for a value that is bound to its parent, it delegates to the parent.
For example, notice how `AbstractDatabaseScope.getRunMode()` delegates to `JvmScope.getRunMode()` below.

### Waiting for Child Scopes to Shut Down

When running in a multithreaded environment, such as a web server, you might want to wait for ongoing HTTP
requests to complete before shutting down the server.
You can use the
[ConcurrentChildScopes](https://cowwoc.github.io/pouch/5.4/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/ConcurrentChildScopes.html)
class to implement this as follows:

```java
import com.github.cowwoc.pouch.core.ConcurrentChildScopes;
import com.github.cowwoc.pouch.core.Scopes;
import com.github.cowwoc.pouch.core.ConcurrentChildScopes;

public abstract class AbstractJvmScope implements JvmScope
{
  private final RunMode runMode;
  private final Duration closeTimeout;
  private final ConcurrentChildScopes children = new ConcurrentChildScopes();

  protected AbstractJvmScope(RunMode runMode)
  {
    this.runMode = runMode;
    this.closeTimeout = switch (runMode)
    {
      case DEBUG -> Duration.ofMinutes(10);
      case RELEASE -> Duration.ofSeconds(30);
    };
  }

  public void addChild(AutoCloseable child)
  {
    children.add(child);
  }
  
  public void removeChild(AutoCloseable child)
  {
    children.remove(child);
  }

  @Override
  public void close()
  {
    Scopes.runAll(() -> children.shutdown(closeTimeout));
  }
}
```

### Implementing DatabaseScope

```java
import javax.sql.DataSource;

public abstract class AbstractDatabaseScope extends AbstractJvmScope
  implements DatabaseScope
{
  private final JvmScope parent;

  protected AbstractDatabaseScope(JvmScope parent)
  {
    this.parent = parent;
  }

  public RunMode getRunMode()
  {
    return parent.getRunMode();
  }

  @Override
  public void close()
  {
  }
}
```

```java
import javax.sql.DataSource;

public final class DefaultTransactionScope implements TransactionScope
{
  private final DatabaseScope parent;

  public DefaultTransactionScope(DatabaseScope parent)
  {
    this.parent = parent;
  }

  public RunMode getRunMode()
  {
    return parent.getRunMode();
  }

  public DataSource getDataSource()
  {
    return parent.getDataSource();
  }

  @Override
  public void close()
  {
  }
}
```

```java
public final class MainDatabaseScope extends AbstractDatabaseScope
{
  private final Factory<? extends DataSource> dataSource;

  public MainDatabaseScope(JvmScope parent)
  {
    super(parent);
    this.dataSource = new MainDataSourceFactory(this, configuration);
    parent.addChild(this)
  }

  @Override
  public DataSource getDataSource()
  {
    return dataSource.getValue();
  }

  @Override
  public void close()
  {
    dataSource.close();
    super.close();
    parent.removeChild(this);
  }
}
```

### Unit tests

What's interesting about this design is that you can easily isolate unit tests from each other,
as if they were running in separate JVMs.

```java
public class UnitTest
{
  @Test
  public first()
  {
    try (ServerScope scope = new TestServerScope())
    {
      Datasource ds = scope.getDataSource();
      // ...
    }
  }

  @Test
  public second()
  {
    try (ServerScope scope = new TestServerScope())
    {
      Datasource ds = scope.getDataSource();
      // ...
    }
  }
}
```

You can even configure each test to run against a separate database:

```java
public final class TestDatabaseScope extends AbstractDatabaseScope
{
  private final Factory<? extends DataSource> dataSource;

  public TestDatabaseScope(JvmScope parent, Configuration configuration, int id)
  {
    super(parent, configuration);
    dataSource = new TestDataSourceFactory(this, configuration, id);
  }

  @Override
  public DataSource getDataSource()
  {
    return dataSource.getValue();
  }

  @Override
  public void close()
  {
    dataSource.close();
    super.close();
  }
}
```

```java
import javax.sql.DataSource;

public final class MainDataSourceFactory extends ConcurrentLazyFactory<DataSource>
{
  private final DatabaseScope scope;

  public MainDataSourceFactory(DatabaseScope scope)
  {
    this.scope = scope;
  }

  @Override
  protected DataSource createValue()
  {
    // Return a DataSource pointing to the main database
  }

  @Override
  protected void disposeValue(DataSource dataSource)
  {
    dataSource.close();
  }
}
```

```java
import com.github.cowwoc.pouch.core.ConcurrentLazyFactory;

import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestDataSourceFactory extends ConcurrentLazyFactory<DataSource>
{
  private final DatabaseScope scope;
  private final String databaseName;

  public TestDataSourceFactory(DatabaseScope scope, int id)
  {
    this.scope = scope;
    this.databaseName = "test-" + id;
  }

  private Connection getConnection()
  {
    // Return connection to the database
  }

  @Override
  protected DataSource createValue()
  {
    // Create a test-specific database
    try (Connection connection = getConnection())
    {
      connection.setAutoCommit(true);
      try (Statement statement = connection.createStatement())
      {
        statement.executeUpdate("DROP DATABASE IF EXISTS " + databaseName);
        statement.executeUpdate("CREATE DATABASE " + databaseName);
      }
    }
    catch (SQLException e)
    {
      throw new RuntimeException("", e);
    }
    // Return a DataSource pointing to the test-specific database
  }

  @Override
  protected void disposeValue(DataSource dataSource)
  {
    try (Connection connection = dataSource.getConnection())
    {
      try (Statement statement = connection.createStatement())
      {
        statement.executeUpdate("DROP DATABASE IF EXISTS " + configuration.databaseName());
      }
    }
    catch (SQLException e)
    {
      throw new DataAccessException("", e);
    }
    dataSource.close();
  }
}
```

### Scopes that Return Different Values Over Their Lifetime

Scopes that need to return different values over their lifetime can return
a [Builder](https://en.wikipedia.org/docs/Builder_pattern)
or [Supplier](https://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html) that will, in turn,
return different values on every invocation.

Here is a contrived example:

```java
public interface ClientScope
{
  Supplier<LocalDateTime> getServerTime();
}

  public Main
  {
    public static void main (String[]args)
    {
      try (ClientScope scope = new MainClientScope())
      {
        Supplier<LocalTime> serverTime = scope.getServerTime();
        for (int i = 0; i < 10; ++i)
          System.out.println("Server time: " + serverTime.get());
      }
    }
  }
```

### How do I break circular dependencies?

The easiest way to break circular dependencies is using lazy initialization.

Dependency injection frameworks recommend injecting `Provider<Foo>` in place of `Foo`,
where the former constructs `Foo` lazily.

````java
class DogTrainer
{
  private final Cat cat;
  private final Dog dog;

  public DogTrainer(Cat cat, Dog dog)
  {
    this.cat = cat;
    this.dog = dog;
  }

  void meow()
  {
    cat.meow();
  }

  void bark()
  {
    dog.bark();
  }
}
````

becomes:

````java
class DogTrainer
{
  private final Provider<Cat> catProvider;
  private final Provider<Dog> dogProvider;

  public DogTrainer(Provider<Cat> catProvider, Provider<Dog> dogProvider)
  {
    this.catProvider = catProvider;
    this.dogProvider = dogProvider;
  }

  void meow()
  {
    Cat cat = catProvider.get();
    cat.meow();
  }

  void bark()
  {
    Dog dog = dogProvider.get();
    dog.bark();
  }
}
````

Pouch recommends using `Reference<Foo>` in place of `Foo`, where the former constructs `Foo` lazily.
Alternatively, you could replace `Foo` by a scope that constructs it lazily.

````java
class DogTrainer
{
  private final Cat cat;
  private final Dog dog;

  public DogTrainer(Cat cat, Dog dog)
  {
    this.cat = cat;
    this.dog = dog;
  }

  void meow()
  {
    cat.meow();
  }

  void bark()
  {
    dog.bark();
  }
}
````

becomes:

````java
class AnimalPen
{
  private final Reference<Cat> catReference;
  private final Reference<Dog> dogReference;

  public AnimalPen(Reference<Cat> catReference, Reference<Dog> dogReference)
  {
    this.catReference = catReference;
    this.dogReference = dogReference;
  }

  void meow()
  {
    Cat cat = catReference.get();
    cat.meow();
  }

  void bark()
  {
    Dog dog = dogReference.get();
    dog.bark();
  }
}
````

or

````java
class DogTrainer
{
  private final JvmScope scope;

  public DogTrainer(JvmScope scope)
  {
    this.scope = scope;
  }

  void meow()
  {
    Cat cat = scope.getCat();
    cat.meow();
  }

  void bark()
  {
    Dog dog = scope.getDog();
    dog.bark();
  }
}
````

The scope approach makes it easier to look up multiple values, or pass the scope to another object.

### What's the difference between LazyFactory and ConcurrentLazyFactory?

The library contains two types of classes: ones that are thread-safe and ones that are not.

For example,
[ConcurrentLazyFactory](https://cowwoc.github.io/pouch/5.4/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/ConcurrentLazyFactory.html)
is the thread-safe equivalent
of [LazyFactory](https://cowwoc.github.io/pouch/5.4/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/LazyFactory.html).
`LazyFactory` is faster than `ConcurrentLazyFactory`, but doesn't support access from multiple threads.
Classes that are required to support multithreaded access
(such as the application scope) must use the thread-safe classes.

## Try it!

The [jersey plugin](jersey/src) contains a working example. Download a copy and try it for yourself.

# Class guide

![class-guide.png](docs/class-guide.png)

# Plugin modules

The following sample code demonstrates integration with various third-party libraries:

* [Jersey](jersey/): Integrates pouch with Jersey.
* [Dropwizard](dropwizard/): Integrates pouch with Dropwizard.

# Related projects

* [Requirements](https://github.com/cowwoc/requirements.java/): Fluent Design by Contract for Java APIs.
* [JayWire](https://github.com/vanillasource/jaywire): the power of dependency injection
  without the "magic".

# License

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0

* Icons made by Flat Icons from www.flaticon.com is licensed by CC 3.0 BY
