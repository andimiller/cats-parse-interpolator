# cats-parse-interpolator

This library adds a parser interpolator to make [cats-parse](https://github.com/typelevel/cats-parse) slightly easier to use.

## Usage

```scala
libraryDependencies += "net.andimiller" %% "cats-parse-interpolator" % "0.1.0"
```

You then import the library, and probably cats.parse too

```scala mdoc
import net.andimiller.cats.parse.interpolator._
import cats.parse._
```

Then you can create a parser with a `p` prefix like so:

```scala mdoc
p"hello world".parseAll("hello world")
```

You can interpolate other parsers into it:

```scala mdoc
{
  val name = Parser.charsWhile(_.isLetter)
  val number = Parser.charsWhile(_.isDigit).map(_.toInt)
  p"$name = $number".parseAll("bob = 10")
}
```

And it will even type the output correctly as a tuple of all subparsers.
