package part1.recap

import scala.concurrent.Future
import scala.math.Equiv.Implicits
import scala.util.{Failure, Success}

object ScalaRecap extends App {
  // values and variables
  val aBoolean: Boolean = false

  // expressions
  val anIfExpression = if (2 > 3) "bigger" else "smaller"

  // instructions vs expressions
  val theUnit = println("Hello, Scala") // Unit = "no meaningful value" = void in other languages

  // functions
  def myFunction(x: Int) = 42

  // OOP
  class Animal
  class Cat extends Animal
  trait Carnivore {
    def eat(animal: Animal): Unit
  }
  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Crunch!")
  }

  // singleton pattern
  object MySingleton

  // companions
  object Carnivore

  // generics
  trait MyList[A]

  // method notation
  val x = 1 + 2
  val y = 1.+(2)

  // Functional Programming
val incrementer: Int => Int = x => x + 1
  val incremented = incrementer(42)

  // map, flatMap, filter
  val processList = List(1, 2, 3).map(incrementer)

  // Pattern Matching
  val unknown: Any = 45
  val original = unknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "unknown"
  }

  // try-catch
  try {
    throw new NullPointerException
  } catch {
    case e: NullPointerException => "some returned value"
    case _ => "something else"
  }

  // Future
  import scala.concurrent.ExecutionContext.Implicits.global
  val aFuture = Future {
    // some expensive computation, runs on another thread
    42
  }

  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"I've found $meaningOfLife"
    case Failure(ex) => println(s"I have failed: $ex")
  }

  // Partial functions
  val aPartialFunction = (x: Int) => x match {
    case 1 => 43
    case 0 => 56
    case _ => 999
  }

  // Implicits

  // auto-injection by the compiler
  def methodWithImplicitArgument(implicit x: Int) = x + 43
  implicit val implicitInt = 67

  val implicitCall = methodWithImplicitArgument

  // Implicit conversions - implicit defs
  case class Person(name: String) {
    def greet: Unit = println(s"Hi, my name is $name")
  }
  implicit  def fromStringToPerson(name: String): Person = Person(name)

  "Bob".greet

  // Implicit conversion - implicit classes
  implicit class Dog(name: String) {
    def bark = println("Bark!")
  }
  "Lassie".bark

  /*
    - Local scope
    - Imported scope
    - Companion objects of the types involved in the method call
   */
  List(1, 2, 3).sorted // finds Ordering[Int] in object Int
}
