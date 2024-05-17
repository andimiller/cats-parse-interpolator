import cats.implicits._
import cats.effect.{IO, IOApp}
import fs2.io.file.Files

object Generate extends IOApp.Simple {

  val generators: List[CodeGen[IO]] = List(
    SyntaxGen,
    TestGen
  )

  override def run: IO[Unit] =
    generators.traverse { gen =>
      IO.println(s"generating ${gen.file}") *>
        gen.contents
          .through(
            Files[IO].writeUtf8Lines(gen.file)
          )
          .compile
          .drain
    }.void

}
