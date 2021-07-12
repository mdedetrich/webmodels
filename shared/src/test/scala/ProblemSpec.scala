import cats.syntax.either._
import io.circe.parser._
import io.circe.syntax._
import io.circe.{JsonObject, Printer}
import org.mdedetrich.webmodels.Problem
import org.mdedetrich.webmodels.circe._
import org.specs2.Specification

class ProblemSpec extends Specification {

  def is = s2"""
      encode a problem1    correctly $encodeProblem1
      decode a problem1    correctly $decodeProblem1
      encode a problem1 #2 correctly $encodeProblem1
      encode a problem2    correctly $encodeProblem2
      decode a problem2    correctly $decodeProblem2
    """

  val problem1 = Problem(
    "test",
    None,
    Some(200),
    None,
    None
  )

  val problem1Again = Problem(
    "test",
    None,
    Some(200),
    None,
    None,
    JsonObject.empty
  )

  val problem2 = Problem(
    "test",
    None,
    Some(200),
    None,
    None,
    JsonObject.fromMap(
      Map(
        "code" -> 500.asJson
      )
    )
  )

  val problem1AsJson = """{"type":"test","status":200}"""

  val problem2AsJson = """{"type":"test","status":200,"code":500}"""

  def encodeProblem1 = (name: String) => {

    val optionalProblem = for {
      parsed    <- parse(problem1AsJson).toOption
      asProblem <- parsed.as[Problem].toOption
    } yield asProblem

    optionalProblem must beSome(problem1)
  }

  def encodeProblem1Again = (name: String) => {

    val optionalProblem = for {
      parsed    <- parse(problem1AsJson).toOption
      asProblem <- parsed.as[Problem].toOption
    } yield asProblem

    optionalProblem must beSome(problem1Again)
  }

  def decodeProblem1 = (name: String) => {

    val asJson = problem1.asJson.pretty(Printer.noSpaces.copy(dropNullValues = true))

    val problem1AsJsonWithoutNewline = problem1AsJson.trim

    asJson must beEqualTo(problem1AsJsonWithoutNewline)
  }

  def encodeProblem2 = (name: String) => {

    val optionalProblem = for {
      parsed    <- parse(problem2AsJson).toOption
      asProblem <- parsed.as[Problem].toOption
    } yield asProblem

    optionalProblem must beSome(problem2)
  }

  def decodeProblem2 = (name: String) => {

    val asJson = problem2.asJson.pretty(Printer.noSpaces.copy(dropNullValues = true))

    val problem1AsJsonWithoutNewline = problem2AsJson.trim

    asJson must beEqualTo(problem1AsJsonWithoutNewline)
  }

}
