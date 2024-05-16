package net.andimiller.cats.parse.interpolator

import cats.parse._

class InterpolatorSpec extends munit.FunSuite {

  test("Zero values") {
    val parser = p"hello world"
    assertEquals(
      parser.parseAll("hello world"),
      Right(())
    )
  }

  val name = Parser.charsWhile(_.isLetter)

  test("One value") {
    List(
      p"$name"  -> "bob",
      p"$name " -> "bob ",
      p" $name" -> " bob"
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right("bob")
      )
    }
  }

  test("One value - Monad") {
    List(
      pm"$name"  -> "bob",
      pm"$name " -> "bob ",
      pm" $name" -> " bob"
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right("bob")
      )
    }
  }

  val number = Parser.charsWhile(_.isDigit).map(_.toInt)

  test("Two values") {
    List(
      p"$name$number"   -> "bob10",
      p"$name $number"  -> "bob 10",
      p" $name $number" -> " bob 10",
      p"$name $number " -> "bob 10 "
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right(("bob", 10))
      )
    }
  }

  test("Two values - Monad") {
    List(
      pm"$name$number"   -> "bob10",
      pm"$name $number"  -> "bob 10",
      pm" $name $number" -> " bob 10",
      pm"$name $number " -> "bob 10 "
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right(("bob", 10))
      )
    }
  }

  val equals = Parser.string("=")

  test("Three values") {
    List(
      p"$name$equals$number"   -> "bob=10",
      p"$name $equals $number" -> "bob = 10"
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right(("bob", (), 10))
      )
    }
  }

  test("Three values - Monad") {
    List(
      pm"$name$equals$number"   -> "bob=10",
      pm"$name $equals $number" -> "bob = 10"
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right(("bob", (), 10))
      )
    }
  }

  test("Four values") {
    List(
      p"$name vs $name $equals $number" -> "bob vs mary = 10"
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right("bob", "mary", (), 10)
      )
    }
  }

  test("Four values - Monad") {
    List(
      pm"$name vs $name $equals $number" -> "bob vs mary = 10"
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right("bob", "mary", (), 10)
      )
    }
  }

}
