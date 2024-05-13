package net.andimiller.cats.parse

import cats.parse._
import cats.implicits._

import scala.annotation.nowarn

package object interpolator {
  @nowarn("msg=not.*?exhaustive") // we do a lot of things here with matching on Lists which are a known length
  implicit class ParserHelper(val sc: StringContext) extends AnyVal {
    private def nonEmptyParts: List[Option[String]]                 =
      sc.parts.toList.map(s => Option(s).filter(_.nonEmpty))
    private def pair[T](s: Option[String], p: Parser[T]): Parser[T] = s match {
      case Some(value) => Parser.string(value) *> p
      case None        => p
    }
    private def endParser(last: Option[String]): Parser0[Unit]      =
      last match {
        case Some(value) => Parser.string(value)
        case None        => Parser.unit
      }

    def p(): Parser[Unit]                                                                                                    = Parser.string(sc.parts.mkString)
    def p[A](pa: Parser[A]): Parser[A]                                                                                       = nonEmptyParts match {
      case a :: b :: Nil =>
        (pair(a, pa)) <* endParser(b)
    }
    def p[A, B](pa: Parser[A], pb: Parser[B]): Parser[(A, B)]                                                                = nonEmptyParts match {
      case a :: b :: c :: Nil =>
        ((pair(a, pa), pair(b, pb)).tupled) <* endParser(c)
    }
    def p[A, B, C](pa: Parser[A], pb: Parser[B], pc: Parser[C]): Parser[(A, B, C)]                                           = nonEmptyParts match {
      case a :: b :: c :: d :: Nil =>
        ((pair(a, pa), pair(b, pb), pair(c, pc)).tupled) <* endParser(d)
    }
    def p[A, B, C, D](pa: Parser[A], pb: Parser[B], pc: Parser[C], pd: Parser[D]): Parser[(A, B, C, D)]                      = nonEmptyParts match {
      case a :: b :: c :: d :: e :: Nil =>
        ((pair(a, pa), pair(b, pb), pair(c, pc), pair(d, pd)).tupled) <* endParser(e)
    }
    def p[A, B, C, D, E](pa: Parser[A], pb: Parser[B], pc: Parser[C], pd: Parser[D], pe: Parser[E]): Parser[(A, B, C, D, E)] =
      nonEmptyParts match {
        case a :: b :: c :: d :: e :: f :: Nil =>
          ((pair(a, pa), pair(b, pb), pair(c, pc), pair(d, pd), pair(e, pe)).tupled) <* endParser(f)
      }
    def p[A, B, C, D, E, F](
        pa: Parser[A],
        pb: Parser[B],
        pc: Parser[C],
        pd: Parser[D],
        pe: Parser[E],
        pf: Parser[F]
    ): Parser[(A, B, C, D, E, F)] = nonEmptyParts match {
      case a :: b :: c :: d :: e :: f :: g :: Nil =>
        ((pair(a, pa), pair(b, pb), pair(c, pc), pair(d, pd), pair(e, pe), pair(f, pf)).tupled) <* endParser(g)
    }
    def p[A, B, C, D, E, F, G](
        pa: Parser[A],
        pb: Parser[B],
        pc: Parser[C],
        pd: Parser[D],
        pe: Parser[E],
        pf: Parser[F],
        pg: Parser[G]
    ): Parser[(A, B, C, D, E, F, G)] = nonEmptyParts match {
      case a :: b :: c :: d :: e :: f :: g :: h :: Nil =>
        ((pair(a, pa), pair(b, pb), pair(c, pc), pair(d, pd), pair(e, pe), pair(f, pf), pair(g, pg)).tupled) <* endParser(h)
    }
  }

}
