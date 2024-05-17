import fs2.io.file.Path
import fs2._

trait CodeGen[F[_]] {
  def file: Path
  def contents: Stream[F, String]
}
