package sttp.cats

import cats._
import cats.syntax.all._
import sttp.client3._

trait SttpInstances {
  implicit val sttpResponseTraverseInstance: Traverse[Response] = new Traverse[Response] {
    override def map[A, B](fa: Response[A])
                          (f: A => B): Response[B] =
      fa.copy(body = f(fa.body))

    override def traverse[G[_] : Applicative, A, B](fa: Response[A])
                                                   (f: A => G[B]): G[Response[B]] =
      f(fa.body).map(b => fa.copy(body = b))

    override def foldLeft[A, B](fa: Response[A],
                                b: B)
                               (f: (B, A) => B): B =
      f(b, fa.body)

    override def foldRight[A, B](fa: Response[A],
                                 lb: Eval[B])
                                (f: (A, Eval[B]) => Eval[B]): Eval[B] =
      f(fa.body, lb)
  }
}

object instances extends SttpInstances
