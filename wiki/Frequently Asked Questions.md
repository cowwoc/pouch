### Isn't the Service Locator an Anti-Pattern? ###

The term *Service Locator* means different things to different people. Historically, this was used to refer to a singleton that held references to global variables. As Martin Fowler [points out](http://martinfowler.com/articles/injection.html) "[a] service locator is a registry not a singleton. A singleton provides a simple way of implementing a registry, but that implementation decision is easily changed."

A Service Locator, thought of as a registry, is a perfectly valid counterpart to *Dependency Injection*. The goal is *Inversion of Control*, not Service Locator or Dependency Injection. Both are perfectly valid options with their own strengths and weaknesses.

### What's the difference between Inversion of Control and Dependency Injection? ###

Imagine that `Person` wants to calculate their taxes using a `TaxCalculator` instance. Historically, `Person` would pull a `TaxCalculator` instance as follows:

```java
public class Person
{
	private final double salary;

	public Person(double salary)
	{
		this.salary = salary;
	}

	public double getTaxDue()
	{
		return TaxCalculator.getInstance().dueFor(salary);
	}
}
```

Inversion of Control refers to the fact that dependencies are passed into the class instead of pulling them in:

```java
public class Person
{
	private final double salary;
	private final TaxCalculator calculator;

	public Person(double salary, TaxCalculator calculator)
	{
		this.salary = salary;
		this.calculator = calculator;
	}

	public double getTaxDue()
	{
		return calculator.dueFor(salary);
	}
}
```

Inversion of Control does not mandate *how* the dependencies are passed into `Person` nor how the class is constructed.

* `TaxCalculator` could be passed directly to the constructor, or through some intermediary object.
* `Person` could be constructed by the developer, or by some other mechanism.

Dependency of Injection is a form of Inversion of Control where a framework constructs the `Person` on the developer's behalf. This means that the developer is not supposed to instantiate `Person` themselves:
```java
public class Person
{
	private final TaxCalculator calculator;
	private double salary;

	@Inject
	public Person(TaxCalculator calculator)
	{
		this.calculator = calculator;
	}

	public void setSalary(double salary)
	{
		this.salary = salary;
	}

	public double getTaxDue()
	{
		return calculator.dueFor(salary);
	}
}

public class Main
{
	public static void main(String[] args)
	{
		DependencyInjector injector = ...;
		Person person = injector.getInstance(Person.class);
	}
}
```

Notice that we are no longer able to declare `salary` as a `final` variable. This means that:

* We can no longer depend on instances to be valid after construction. What happens if someone forgets to invoke the setter?
* Instances are no longer immutable.

### What about Dependency Injection? ###

To reiterate, both the Service Locator and Dependency Injection design patterns are valid instances of Inversion of Control, but each has its own strengths and weaknesses.

The main downside of Dependency Injection frameworks is their inability to mix constructor injection with user-provided parameters. For example:

```java

@Inject
public Person(double salary, TaxCalculator calculator)
{
}
```

Dependency Injection fails to handle this gracefully, forcing you to deal with long-winded factories and hard-to-read configuration. If you need to specify parameter ordering (when multiple parameters are of the same type) you are forced to jump through even more hoops.

This is one of those glaring conceptual holes that is reminiscent of the [impedance mismatch](http://en.wikipedia.org/wiki/Object-relational_impedance_mismatch) suffered by ORM systems.

The main benefit of the Dependency Injection pattern is that it'll construct transitive dependencies on your behalf. Meaning, if `A` depends on `B` and `B` depends on `C`, you just need to ask the framework for `A` and it'll construct `B` and `C` on your behalf. Service Locators don't typically construct instances on your behalf, so you have to do some more legwork.

If you use constructor injection, you can just look at the constructor parameters to see the class dependencies.

On the flip side, if you use a Service Locator you will gain the following benefits:

* **Compile-time errors**: the full dependency graph is validated at compile-time, unlike (most) Dependency Injection frameworks which fail at runtime ([Dagger](http://square.github.io/dagger/) being a notable exception).
* **No magic**: All classes are plain Java objects and are instantiated using the *new* operator.
* The resulting code is cleaner and easier to maintain.

### Why would I use this library instead of Guava's [Supplier](https://guava.dev/releases/28.0-jre/api/docs/com/google/common/base/Supplier.html) which does the same or even more? ###

Guava provides comparable functionality:

* [Reference](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/Reference.html) <-> [Supplier](https://guava.dev/releases/28.0-jre/api/docs/com/google/common/base/Supplier.html)
* [ConstantReference](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/ConstantReference.html) <-> [Suppliers.ofInstance()](https://guava.dev/releases/28.0-jre/api/docs/com/google/common/base/Suppliers.html#ofInstance-T-)
* [LazyReference](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/LazyReference.html) <-> [Suppliers.memoize()](https://guava.dev/releases/28.0-jre/api/docs/com/google/common/base/Suppliers.html#memoize-com.google.common.base.Supplier-)

While it is true that [Suppliers](https://guava.dev/releases/28.0-jre/api/docs/com/google/common/base/Suppliers.html) provides some very powerful general-purpose functionality, it isn't as convenient for implementing the [Service Locator](http://martinfowler.com/articles/injection.html#UsingAServiceLocator) design pattern as this library.

Specifically:

1. [Suppliers.memoize()](https://guava.dev/releases/28.0-jre/api/docs/com/google/common/base/Suppliers.html#memoize-com.google.common.base.Supplier-) doesn't provide a mechanism for checking whether the underlying value has been initialized. This is important because when implementing [Factory.close()](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/Factory.html#close()) you want to avoid initializing the value if it has never been initialized before.
2. We provide convenience classes, such as [LazyFactory](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/LazyFactory.html) which unify [LazyReference](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/LazyReference.html) and [Closeable](http://docs.oracle.com/javase/7/docs/api/java/io/Closeable.html) into a single class and in so doing we shield you from thread-safety concerns.

So yes, Guava provides better general-purpose functionality, but for under 10k we provide yous with a more targeted form of this functionality that is easier to use.

### How do I return a new instance of a class on every invocation? ###

In Dependency Injection terms, unscoped `Provider`s return a new instance on every invocation. We don't provide explicit support for unscoped variables so our suggestion is to do one of the following:

* Construct `Foo` yourself (`new Foo()`, no magic), or
* Have the scope return a [Builder](https://en.wikipedia.org/wiki/Builder_pattern) that returns a new instance of `Foo` on every invocation. Java 8's [Supplier](http://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html) is a good fit for most cases.

### How do I return a proxy to an object? ###

In Dependency Injection terms, you can inject `Provider<Foo>` instead of `Foo` if you want to defer the construction of `Foo`.

In our case, you could either inject the scope (e.g. `ApplicationScope`) and look up `Foo` at a later time, or you can inject a `Reference<Foo>` that will look up `Foo` from the scope when [Reference.getValue()](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/Reference.html#getValue()) is invoked. In either case, the scope is your proxy.

### What's the difference between LazyFactory and ConcurrentLazyFactory? ###

The library contains two class hierarchies: one for single-threaded access, and another for multi-threaded access. For example, [ConcurrentLazyFactory](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/ConcurrentLazyFactory.html) is the multi-threaded equivalent of [LazyFactory](http://cowwoc.github.io/pouch/1.15/docs/api/com/github/cowwoc/pouch/LazyFactory.html). `LazyFactory` is faster than `ConcurrentLazyFactory`, but may not be accessed by multiple threads. We recommend using the concurrent classes for multi-threaded scopes (such as the application scope) and the normal classes for single-threaded scopes (such as the request scope).
