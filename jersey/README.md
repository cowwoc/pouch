# Introduction

This guide explains how to inject [Pouch](../../../pouch/) scopes into [Jersey 2](https://jersey.java.net/)
resources and vice-versa (inject Jersey types into Pouch scopes).

## Step 1: Define scope interfaces ##

Example:

[JvmScope](http://cowwoc.github.io/pouch/src/main/java/com/github/cowwoc/pouch/jersey/scope/JvmScope.java):
values specific to the current JVM

[TransactionScope](http://cowwoc.github.io/pouch/src/main/java/com/github/cowwoc/pouch/jersey/scope/TransactionScope.java):
values specific to a database transaction

[HttpScope](http://cowwoc.github.io/pouch/src/main/java/com/github/cowwoc/pouch/jersey/scope/HttpScope.java):
values specific to an HTTP request

## Step 2: Implement scopes ##

Example:

[MainJvmScope](http://cowwoc.github.io/pouch/src/main/java/com/github/cowwoc/pouch/jersey/scope/MainJvmScope.java):
JvmScope for production

[TestJvmScope](http://cowwoc.github.io/pouch/src/test/java/com/github/cowwoc/pouch/jersey/scope/TestJvmScope.java):
JvmScope for tests

## Step 3: Integrate Pouch with Jersey ##

HK2 is Jersey's internal dependency-injection mechanism. HK2 Binders are equivalent
to [Guice Modules](https://github.com/google/guice/wiki/GettingStarted)
or [Spring @Configuration](http://docs.spring.io/autorepo/docs/spring/3.2.x/spring-framework-reference/html/beans.html#beans-java)

First, you'll need to implement an HK2 Binder (
e.g. [MainPouchBinder](http://cowwoc.github.io/pouch/src/main/java/com/github/cowwoc/pouch/jersey/scope/MainPouchBinder.java)),
then you'll need to register the Binder with Jersey (
e.g. [MainApplication](http://cowwoc.github.io/pouch/src/main/java/com/github/cowwoc/pouch/jersey/application/MainApplication.java)).

## Step 4: Inject scopes into Jersey resources ##

```java

@Path("helloworld")
public final class HelloWorldResource
{
  @Inject
  public HelloWorldResource(HttpScope scope)
  {
    this.scope = scope;
  }

  @GET
  @Produces("text/plain")
  public String getHello()
  {
    return "Hello world!\n" +
      "HTTP-scoped value       : " + scope.getRequestedUri() + "\n" +
      "Application-scoped value: " + scope.getMode();
  }
}
```

## Step 5: Inject scopes into unit tests ##

```java
public final class TestDatabase
{
  @Test
  public void test1()
  {
    try (JvmScope jvmScope = new TestJvmScope();
         TransactionScope transaction = jvmScope.createTransactionScope())
    {
      Connection connection = transaction.getConnection();
      System.out.println("test1() running against " + connection.getMetaData().getURL());
    }
  }
}
```

## Try it! ##

The [code repository](src/) contains a working example. Download a copy and try it for yourself.

# License #

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0