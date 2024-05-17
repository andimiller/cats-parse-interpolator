package net.andimiller.cats.parse.interpolator

import cats.parse._

class Interpolator0Spec extends munit.FunSuite {

  test("Zero values") {
    val parser      = p0"hello world"
    assertEquals(
      parser.parseAll("hello world"),
      Right(())
    )
    val emptyParser = p0""
    assertEquals(
      emptyParser.parseAll(""),
      Right(())
    )
  }

  val name = Parser.charsWhile0(_.isLetter)

  test("One value") {
    List(
      p0"$name"  -> "bob",
      p0"$name " -> "bob ",
      p0" $name" -> " bob"
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right("bob")
      )
    }
    assertEquals(
      p0"$name".parseAll(""),
      Right("")
    )
  }

  val number = Parser.charsWhile0(_.isDigit).map(s => if (s.isEmpty) 0 else s.toInt)

  test("Two values") {
    List(
      p0"$name$number"   -> "bob10",
      p0"$name $number"  -> "bob 10",
      p0" $name $number" -> " bob 10",
      p0"$name $number " -> "bob 10 "
    ).foreach { case (p, input) =>
      assertEquals(
        p.parseAll(input),
        Right(("bob", 10))
      )
    }
    assertEquals(
      p0"$name$number".parseAll("bob"),
      Right(("bob", 0))
    )
  }

}
