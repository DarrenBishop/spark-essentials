
# Scala Recap

## Expressions
```scala mdoc
val aBoolean: Boolean = false

val anIfExpression = if (2 > 3) "bigger" else "smaller"
```
## Instructions vs expressions
```scala mdoc
println("Hello, Scala") // Unit = "no meaningful value" = void in other languages
```

## Functions
```scala mdoc
def myFunction(x: Int) = 42
```

  ## Object-Oriented Programming
```scala mdoc
class Animal
class Cat extends Animal
trait Carnivore {
  def eat(animal: Animal): Unit
}
class Crocodile extends Animal with Carnivore {
  override def eat(animal: Animal): Unit = println("Crunch!")
}
```

## Singleton Pattern
```scala mdoc
object MySingleton
```

## Companions
```scala mdoc:nest
trait Carnivore {
  def eat(animal: Animal): Unit
}

object Carnivore
```

## Generics
```scala mdoc
trait MyList[A]
```

## Method notation
```scala mdoc
val x = 1 + 2
val y = 1.+(2)
```

## Functional Programming
```scala mdoc
val incrementer: Int => Int = x => x + 1
val incremented = incrementer(42)
```

## Map, flatMap, filter
```scala mdoc
val processList = List(1, 2, 3).map(incrementer)
```

## Pattern Matching
```scala mdoc
val unknown: Any = 45
val original = unknown match {
  case 1 => "first"
  case 2 => "second"
  case _ => "unknown"
}
```

## Try-Catch
```scala mdoc
try {
  throw new NullPointerException
} catch {
  case e: NullPointerException => "some returned value"
  case _: Throwable => "something else"
}
```

## Future
```scala mdoc:invisible
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
```
```scala mdoc
val aFuture = Future {
   // some expensive computation, runs on another thread
  42
}

aFuture.onComplete {
  case Success(meaningOfLife) => println(s"I've found $meaningOfLife")
  case Failure(ex) => println(s"I have failed: $ex")
}
```

```scala mdoc:invisible
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
Await.ready(aFuture, Duration.Inf)
```

## Partial Functions
```scala mdoc
val aPartialFunction = (x: Int) => x match {
  case 1 => 43
  case 0 => 56
  case _ => 999
}
```

## Implicits

### Auto-injection by the compiler
```scala mdoc
def methodWithImplicitArgument(implicit x: Int) = x + 43
implicit val implicitInt: Int = 67

val implicitCall = methodWithImplicitArgument
```

### Implicit conversions - implicit defs
```scala mdoc
case class Person(name: String) {
  def greet: Unit = println(s"Hi, my name is $name")
}
implicit  def fromStringToPerson(name: String): Person = Person(name)

"Bob".greet
```

### Implicit conversion - implicit classes
```scala mdoc
implicit class Dog(name: String) {
  def bark = println("Bark!")
}

"Lassie".bark
```

### Implicit Resolution
- Local scope
- Imported scope
- Companion objects of the types involved in the method call

```scala mdoc
List(1, 2, 3).sorted 
```
Here, `sorted` finds `Ordering[Int]` in `object Int` companion.
