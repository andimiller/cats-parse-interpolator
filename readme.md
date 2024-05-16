# cats-parse-interpolator

This library adds a parser interpolator to make [cats-parse](https://github.com/typelevel/cats-parse) slightly easier to use.

## Included Interpolators:

| Prefix  | Description   | Inputs                            | Output    |
| ------- | -----------   | --------                          | --------  | 
| p       | Parser        | all `Parser`                      | `Parser`  |
| p0      | Parser0       | all `Parser0`                     | `Parser0` |
| pm      | Parser Monad  | `Parser`,  then either afterwards | `Parser0` |

## Usage

```scala
libraryDependencies += "net.andimiller" %% "cats-parse-interpolator" % "0.1.0"
```

You then import the library, and probably cats.parse too

```scala
import net.andimiller.cats.parse.interpolator._
import cats.parse._
```

Then you can create a parser with a `p` prefix like so:

```scala
p"hello world".parseAll("hello world")
// res0: Either[Parser.Error, Unit] = Right(value = ())
```

You can interpolate other parsers into it:

```scala
{
  val name = Parser.charsWhile(_.isLetter)
  val number = Parser.charsWhile(_.isDigit).map(_.toInt)
  p"$name = $number".parseAll("bob = 10")
}
// res1: Either[Parser.Error, (String, Int)] = Right(value = ("bob", 10))
```

And it will even type the output correctly as a tuple of all subparsers.

There is also an interpreter for `Parser0` if you need that:

```scala
{
  val name = Parser.charsWhile0(_.isLetter).map(s => Option(s).filter(_.nonEmpty))
  val number = Parser.charsWhile0(_.isDigit).map(s => if (s.isEmpty) 0 else s.toInt)
  p0"$name = $number".parseAll(" = ")
}
// res2: Either[Parser.Error, (Option[String], Int)] = Right(value = (None, 0))
```

## Motivation

For a comparison of how a parser is written with this, here's a parser for env variables, taking input like:

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
// res3: Either[Parser.Error, cats.data.NonEmptyList[(String, String)]] = Right(
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
// res4: Either[Parser.Error, cats.data.NonEmptyList[(String, String)]] = Right(
//   value = NonEmptyList(head = ("FOO", "abc"), tail = List(("BAR", "def")))
// )
```

</td>
</tr>
</table>


