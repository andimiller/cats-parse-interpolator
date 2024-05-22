# cats-parse-interpolator

This library adds a parser interpolator to make [cats-parse](https://github.com/typelevel/cats-parse) slightly easier to use.

## Included Interpolators:

| Prefix  | Description          | Inputs                            | Output    |
| ------- | -----------          | --------                          | --------  | 
| p       | Parser  Applicative  | all `Parser`                      | `Parser`  |
| p0      | Parser0 Applicative  | all `Parser0`                     | `Parser0` |
| pm      | Parser Monad         | `Parser`,  then either afterwards | `Parser`  |

## Usage

```scala
libraryDependencies += "net.andimiller" %% "cats-parse-interpolator" % "0.1.0"
```

You then import the library, and probably cats.parse too

```scala
import net.andimiller.cats.parse.interpolator._
import cats.parse._
import cats.implicits._ // if you need extra syntax
```

Then you can create a parser with a `p` prefix like so:

```scala
p"hello world".parseAll("hello world")
// res0: Either[Error, Unit] = Right(value = ())
```

You can interpolate other parsers into it:

```scala
{
  val name = Parser.charsWhile(_.isLetter)
  val number = Parser.charsWhile(_.isDigit).map(_.toInt)
  p"$name = $number".parseAll("bob = 10")
}
// res1: Either[Error, Tuple2[String, Int]] = Right(value = ("bob", 10))
```

And it will even type the output correctly as a tuple of all subparsers.

There is also an interpreter for `Parser0` if you need that:

```scala
{
  val name = Parser.charsWhile0(_.isLetter).map(s => Option(s).filter(_.nonEmpty))
  val number = Parser.charsWhile0(_.isDigit).map(s => if (s.isEmpty) 0 else s.toInt)
  p0"$name = $number".parseAll(" = ")
}
// res2: Either[Error, Tuple2[Option[String], Int]] = Right(value = (None, 0))
```

## Motivation

### LISP Calculator Example

For a simple example, say we want to parse these:

```scala
val calculatorInputs = List(
  "(+ 2 2)",
  "(+ 2 (* 3 6))",
  "42"
)
```

Into these:

```scala
enum Expr:
  case Number(value: Int)
  case Plus(left: Expr, right: Expr)
  case Multiply(left: Expr, right: Expr)
```

<table>
<tr>
<th>Standard</th>

```scala
calculatorInputs.map {
  Parser.recursive[Expr] { recurse =>
    val number   = Numbers.digits.map(_.toInt).map(Expr.Number)  
    val plus     = p"(+ $recurse $recurse)".map(Expr.Plus)
    val multiply = p"(* $recurse $recurse)".map(Expr.Multiply)
    number.orElse(plus).orElse(multiply)
  }.parseAll
}
// res3: List[Either[Error, Expr]] = List(Right(Plus(Number(2),Number(2))), Right(Plus(Number(2),Multiply(Number(3),Number(6)))), Right(Number(42)))
```

<th>Interpolator</th>
</tr>
<tr>
<td>

```scala
calculatorInputs.map {
  Parser.recursive[Expr] { recurse =>
    val number   = Numbers.digits.map(_.toInt).map(Expr.Number)  
    val plus     = (Parser.string("(+ ") *> recurse <* Parser.string(" "), recurse <* Parser.string(")")).mapN(Expr.Plus)
    val multiply = (Parser.string("(* ") *> recurse <* Parser.string(" "), recurse <* Parser.string(")")).mapN(Expr.Multiply)
    number.orElse(plus).orElse(multiply)
  }.parseAll
}
// res4: List[Either[Error, Expr]] = List(Right(Plus(Number(2),Number(2))), Right(Plus(Number(2),Multiply(Number(3),Number(6)))), Right(Number(42)))
```

</td>
</tr>
</table>


### Env Variable Example


For a more complex parser, here's a parser for env variables, taking input like:

`FOO=abcC,BAR=def`

<table>
<tr>
<th>Standard</th>
<th>Interpolator</th>
</tr>
<tr>
<td>

```scala
{
  val keyStartChars = ('a' to 'z').toSet ++ ('A' to 'Z').toSet ++ "_".toSet
  val keyRestChars = keyStartChars ++ ('0' to '9').toSet
  val key = for {
    head <- Parser.charIn(keyStartChars).string
    rest <- Parser.charsWhile0(keyRestChars).string
  } yield head + rest
  val value = Parser.charsWhile(c => !" ,".toSet(c))

  val pair = for {
    k <- key
    _ <- Parser.char('=')
    v <- value
  } yield (k -> v)

  pair.repSep(Parser.char(','))
}.parseAll("FOO=abc,BAR=def")
// res5: Either[Error, NonEmptyList[Tuple2[String, String]]] = Right(
//   value = NonEmptyList(head = ("FOO", "abc"), tail = List(("BAR", "def")))
// )
```

</td>
<td>

```scala
{
  val keyStartChars = ('a' to 'z').toSet ++ ('A' to 'Z').toSet ++ "_".toSet

  val keyStart = Parser.charIn(keyStartChars).string
  val keyRest  = Parser.charsWhile0(keyStartChars ++ ('0' to '9').toSet).string

  val key   = pm"$keyStart$keyRest".string
  val value = Parser.charsWhile(c => !" ,".toSet(c))

  p"$key=$value".repSep(Parser.char(','))
}.parseAll("FOO=abc,BAR=def")
// res6: Either[Error, NonEmptyList[Tuple2[String, String]]] = Right(
//   value = NonEmptyList(head = ("FOO", "abc"), tail = List(("BAR", "def")))
// )
```

</td>
</tr>
</table>


