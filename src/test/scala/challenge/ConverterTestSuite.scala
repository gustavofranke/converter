package challenge

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.TableDrivenPropertyChecks._

class ConverterTestSuite extends AnyFunSuite {

  test("parseThenRender obtained values should match the expected values") {
    val table = Table(
      ("input", "output"),
      ("[PM] 0:00.000", "00:00 – PRE_MATCH"),
      ("[H1] 0:15.025", "00:15 – FIRST_HALF"),
      ("[H1] 3:07.513", "03:08 – FIRST_HALF"),
      ("[H1] 45:00.001", "45:00 +00:00 – FIRST_HALF"),
      ("[H1] 46:15.752", "45:00 +01:16 – FIRST_HALF"),
      ("[HT] 45:00.000", "45:00 – HALF_TIME"),
      ("[H2] 45:00.500", "45:01 – SECOND_HALF"),
      ("[H2] 90:00.908", "90:00 +00:01 – SECOND_HALF"),
      ("[FT] 90:00.000", "90:00 +00:00 – FULL_TIME"),
      ("90:00", Invalid.invalid),
      ("[H3] 90:00.000", Invalid.invalid),
      ("[PM] -10:00.000", Invalid.invalid),
      ("FOO", Invalid.invalid)
    )

    forAll(table) { (input: String, output: String) =>
      assert(Converter.parseThenRender(input) == output)
    }
  }

  test("parseMatchTime shows the result of all parsing rules, and error messages when it fails") {
    val table = Table(
      ("input", "output"),
      ("[PM] 0:00.000", MatchTime("PM", "0", "0", "0")),
      ("[H1] 0:15.025", MatchTime("H1", "0", "15", "25")),
      ("[H1] 3:07.513", MatchTime("H1", "3", "7", "513")),
      ("[H1] 45:00.001", MatchTime("H1", "45", "0", "1")),
      ("[H1] 46:15.752", MatchTime("H1", "46", "15", "752")),
      ("[HT] 45:00.000", MatchTime("HT", "45", "0", "0")),
      ("[H2] 45:00.500", MatchTime("H2", "45", "0", "500")),
      ("[H2] 90:00.908", MatchTime("H2", "90", "0", "908")),
      ("[FT] 90:00.000", MatchTime("FT", "90", "0", "0")),
      ("90:00", Left("90:00")),
      ("[H3] 90:00.000", Left("H3 is an Unsupported Period Format")),
      ("[PM] -10:00.000", Left(Minutes.creationImpossible)),
      ("FOO", Left("FOO"))
    )

    forAll(table) { (input: String, output: Either[String, MatchTime]) =>
      assert(Converter.parseMatchTime(input) == output)
    }
  }

  test("domain representations, error messages should be returned on invalid inputs") {
    val invalidSecondInt    = 67
    val invalidSecondString = "-67"
    assert(Seconds(invalidSecondInt) === Left(Seconds.creationImpossible))
    assert(Seconds(invalidSecondString) === Left(Seconds.creationImpossible))
    assert(Minutes(invalidSecondString) === Left(Minutes.creationImpossible))
    assert(Minutes("-167") === Left(Minutes.creationImpossible))
    assert(MilliSeconds(invalidSecondString) === Left(MilliSeconds.creationImpossible))
    assert(MilliSeconds("1000") === Left(MilliSeconds.creationImpossible))
  }

  test("parseInt") {
    val i = 123
    assert(parseInt("123") === Right(i))
    assert(parseInt("asdf").isLeft)
  }

  test("overflowing Second creation when rounding from MilliSeconds, fails to create an instance ") {
    import cats.implicits._
    assert(MatchTime("PM", "0", "60", "501").show == Right(Invalid.invalid).toString)
  }
}
