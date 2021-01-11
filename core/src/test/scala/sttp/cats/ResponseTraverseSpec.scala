package sttp.cats

import cats._
import cats.laws.discipline.TraverseTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FlatSpecDiscipline
import sttp.client3.Response
import sttp.model._
import sttp.cats.instances._

import scala.collection.immutable

class ResponseTraverseSpec extends AnyFlatSpec with FlatSpecDiscipline with Configuration {

  private implicit def arbStatusCode: Arbitrary[StatusCode] = Arbitrary {
    Gen.frequency(
      80 -> 200,
      4 -> Gen.choose(100, 103),
      4 -> Gen.choose(201, 226),
      4 -> Gen.choose(300, 308),
      4 -> Gen.choose(400, 451),
      4 -> Gen.choose(500, 511),
    ).map(StatusCode.apply)
  }

  private implicit val arbHeader: Arbitrary[Header] = Arbitrary {
    for {
      name <- Gen.alphaNumStr
      value <- Gen.asciiPrintableStr
    } yield Header(name, value)
  }

  private implicit def arbResponse[A: Arbitrary]: Arbitrary[Response[A]] = Arbitrary {
    for {
      body <- arbitrary[A]
      statusCode <- arbitrary[StatusCode]
      statusText <- Gen.alphaNumStr
      headers <- arbitrary[immutable.Seq[Header]]
    } yield Response(body, statusCode, statusText, headers)
  }

  private implicit def eqResponse[A: Eq]: Eq[Response[A]] = Eq.fromUniversalEquals

  checkAll("Traverse.Response", TraverseTests[Response].traverse[Int, String, String, Set[String], Option, Option])
}
