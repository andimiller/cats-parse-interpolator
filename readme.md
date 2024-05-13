# cats-parse-interpolator

This library adds a parser interpolator to make [cats-parse](https://github.com/typelevel/cats-parse) slightly easier to use.

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
