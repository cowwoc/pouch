### Not Your Mother's Service Locator

The term *Service Locator* means different things to different people.
Historically, this was used to refer to a singleton that held references to global variables.
As Martin Fowler
[points out](http://martinfowler.com/articles/injection.html), "[a] service locator is a registry not a
singleton.
A singleton provides a simple way of implementing a registry, but that implementation decision is
easily changed."

A Service Locator, thought of as a registry, is a perfectly valid counterpart to
*Dependency Injection*.
The goal is *Inversion of Control*, not Service Locator or Dependency Injection.
Both are perfectly valid options with their own strengths and weaknesses.

### What's the difference between Inversion of Control and Dependency Injection?

Imagine an accounting firm with accountants in different geographical regions (locales).
Each `Accountant` calculates their customer's taxes using a `TaxCalculator` instance.
Because each region calculates taxes differently, `TaxCalculator.getTaxDue()` requires the tax region
alongside the customer's taxable income.

Historically, `Accountant` would pull a `TaxCalculator` instance and calculate the tax due as follows:

```java
public class Accountant
{
  private final Locale locale;

  public Accountant(Locale locale)
  {
    this.locale = locale;
  }

  public double getTaxDue(double taxableIncome)
  {
    return TaxCalculator.getInstance().getTaxDue(locale, taxableIncome);
  }
}
```

Inversion of Control refers to the fact that dependencies are passed into a class instead of being pulled in:

```java
public class Accountant
{
  private final TaxCalculator calculator;
  private final Locale locale;

  public Accountant(TaxCalculator calculator, Locale locale)
  {
    this.calculator = calculator;
    this.locale = locale;
  }

  public double getTaxDue(double taxableIncome)
  {
    return calculator.getTaxDue(taxableIncome);
  }
}
```

Inversion of Control doesn't mandate *how* the dependencies are passed into `Accountant` nor how the class
is constructed.

* `TaxCalculator` could be passed directly to the constructor, or through some intermediary object.
* `Accountant` could be constructed by the developer, or by some other mechanism.

Dependency Injection is a form of Inversion of Control where a framework constructs `Accountant` on the
developer's behalf, preventing the developer from instantiating `Accountant` themselves:

```java
public class Person
{
  private final TaxCalculator calculator;
  private Locale local;

  @Inject
  public Person(TaxCalculator calculator)
  {
    this.calculator = calculator;
  }

  public void setLocale(double locale)
  {
    this.locale = locale;
  }

  public double getTaxDue(double salary)
  {
    return calculator.dueFor(locale, salary);
  }
}

public class Main
{
  public static void main(String[] args)
  {
    DependencyInjector injector = DependencyInjector.getInstance();
    Accountant accountant = injector.getInstance(Person.class);
    accountant.setLocale(locale);
    double taxDue = accountant.getTaxDue(salary);
  }
}
```

Notice that we're no longer able to declare `locale` as a `final` variable. This means that:

* Instances aren't guaranteed to be valid after construction.
  What happens if someone forgets to invoke the setter method?
* Instances are no longer immutable.

### What about Constructor Injection?

To reiterate, both the Service Locator and Dependency Injection design patterns are valid instances of
Inversion of Control, but each has its own strengths and weaknesses.

The main downside of Dependency Injection frameworks is their inability to mix constructor injection with
user-provided parameters.

```java
@Inject
public Accountant(TaxCalculator calculator,Locale locale)
{
}
```

Dependency Injection fails to handle this gracefully,
forcing you to deal with long-winded factories and hard-to-read configuration.
If multiple parameters are of the same type, you're forced to jump through even more hoops.

This conceptual hole is reminiscent of the
[impedance mismatch](http://en.wikipedia.org/wiki/Object-relational_impedance_mismatch)
suffered by ORM systems.

The main benefit of the Dependency Injection pattern is that it'll construct transitive dependencies on your
behalf.
Meaning, if `A` depends on `B` and `B` depends on `C`,
you just need to ask the framework for `A` and it'll construct `B` and `C` on your behalf.
Service Locators don't typically construct instances on your behalf, so you have to do some more legwork.

If you use constructor injection, you can just look at the constructor parameters to see the class
dependencies.

On the flip side, if you use a Service Locator, you will benefit from the following:

* **Compile-time checking**: the full dependency graph is validated at compile-time, unlike (most)
  Dependency Injection frameworks which fail at runtime
  ([Dagger](https://dagger.dev/) being a notable exception).
* **No magic**: All classes are plain Java objects and are instantiated using the *new* operator.
* The resulting code is cleaner and easier to maintain.

### Why would I use this library instead of Guava's [Supplier](https://guava.dev/releases/32.1.1-jre/api/docs/com/google/common/base/Supplier.html) which does the same or even more?

Ease of use and substantially reduced size.

Guava provides comparable functionality:

* [Reference](https://cowwoc.github.io/pouch/4.0/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/Reference.html) <->
  [Supplier](https://guava.dev/releases/32.1.1-jre/api/docs/com/google/common/base/Supplier.html)
* [ConstantReference](https://cowwoc.github.io/pouch/4.0/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/ConstantReference.html)
  <->
  [Suppliers.ofInstance()](https://guava.dev/releases/32.1.1-jre/api/docs/com/google/common/base/Suppliers.html#ofInstance-T-)
* [LazyReference](https://cowwoc.github.io/pouch/4.0/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/LazyReference.html) <->
  [Suppliers.memoize()](https://guava.dev/releases/32.1.1-jre/api/docs/com/google/common/base/Suppliers.html#memoize-com.google.common.base.Supplier-)

While it is true
that [Suppliers](https://guava.dev/releases/32.1.1-jre/api/docs/com/google/common/base/Suppliers.html) provides
some very powerful general-purpose functionality, it isn't as convenient for implementing the
[Service Locator](http://martinfowler.com/articles/injection.html#UsingAServiceLocator) design pattern as
this library.

For example:

1. [Suppliers.memoize()](https://guava.dev/releases/32.1.1-jre/api/docs/com/google/common/base/Suppliers.html#memoize-com.google.common.base.Supplier-)
   doesn't provide a mechanism for checking whether the underlying value has been initialized.
   This is important because, when implementing
   [Factory.close()](https://cowwoc.github.io/pouch/4.0/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/Factory.html#close()),
   you'll want to avoid initializing values that've never been initialized before.
2. This library provides convenience classes such as
   [LazyFactory](https://cowwoc.github.io/pouch/4.0/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/LazyFactory.html)
   which unifies classes
   [LazyReference](https://cowwoc.github.io/pouch/4.0/docs/api/com.github.cowwoc.pouch.core/com/github/cowwoc/pouch/core/LazyReference.html)
   and [Closeable](http://docs.oracle.com/javase/8/docs/api/java/io/Closeable.html) into a single class.

The size of the Guava library is 2.8MB.
The size of the Pouch's targeted functionality is easier to use and smaller with a file size of 18KB.
