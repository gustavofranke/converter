package challenge

import cats.Show
import cats.implicits._

/**
 * Write a converter that can take a String representing match time in one format,
 *  and convert it to a String representing match time in another format.
 *
 * The input match time is in the format
 *   [period] minutes:seconds.milliseconds
 *
 * The output should be in the format
 * normalTimeMinutes:normalTimeSeconds - period
 *
 * For the output format,
 *  minutes should be padded to two digits and
 *  milliseconds should be rounded up or down to the nearest whole second.
 *  Periods are represented in short-form on the input format and long-form on output format i.e.
 *
 * When a given period goes into additional time
 *  (i.e. > 45:00.000 for first half, > 90.00.000 for the second half),
 *  the added minutes and seconds are represented separately in the format
 *
 * normalTimeMinutes:normalTimeSeconds +additionalMinutes:additionalSeconds - period
 *
 * Any input which does not meet the required input format should result in an output of INVALID Examples
 */
object Invalid {
  val invalid = "INVALID"
}

object Converter {
  def parseMatchTime(s: String): Either[String, MatchTime] = s match {
    case s"[$period] $minutes:$seconds.$milliseconds" =>
      MatchTime(period, minutes, seconds, milliseconds)
    case unmatched => Left(unmatched)
  }

  def renderMatchTime(emt: Either[String, MatchTime]): String = emt.fold(
    _ => Invalid.invalid,
    mt => mt.show
  )

  def parseThenRender(s: String): String = renderMatchTime(parseMatchTime(s))
}

sealed trait Period extends Product with Serializable
case object  PreMatch   extends Period
case object  FirstHalf  extends Period
case object  HalfTime   extends Period
case object  SecondHalf extends Period
case object  FullTime   extends Period

object Period {
  def apply(s: String): Either[String, Period] = s match {
    case "PM" => Right(PreMatch)
    case "H1" => Right(FirstHalf)
    case "HT" => Right(HalfTime)
    case "H2" => Right(SecondHalf)
    case "FT" => Right(FullTime)
    case otherwise => Left(s"$otherwise is an Unsupported Period Format")
  }

  def unapply(p: Period): String = p match {
    case PreMatch   => "PRE_MATCH"
    case FirstHalf  => "FIRST_HALF"
    case HalfTime   => "HALF_TIME"
    case SecondHalf => "SECOND_HALF"
    case FullTime   => "FULL_TIME"
  }

  implicit val showPeriod: Show[Period] = Show.show[Period] (unapply)
}

/**
 * minutes domain representation
 * @param s ranges from 0 to 120, which is 90 + a 30 mins additional time
 */
sealed abstract case class Minutes(s: Int)

object Minutes {
  def apply(s: String): Either[String, Minutes] = for {
    i <- parseInt(s)
    r <- Either.cond(i >= 0 && i <= 120, new Minutes(i) {}, "Minutes values are >= 0 and <= 120")
  } yield r
}

/**
 * seconds domain representation
 * @param s ranges from 0 to 60
 */
sealed abstract case class Seconds(s: Int)
object Seconds {
  def apply(s: String): Either[String, Seconds] = for {
    i <- parseInt(s)
    r <- Either.cond(i >= 0 && i <= 60, new Seconds(i) {}, "Seconds values are >= 0 and <= 60")
  } yield r

  def apply(i: Int): Either[String, Seconds] =
    Either.cond(i >= 0 && i <= 60, new Seconds(i) {}, "Seconds values are >= 0 and <= 60")
}

/**
 * milliseconds domain representation
 * @param s ranges from 0 to 999
 */
sealed abstract case class MilliSeconds(s: Int)
object MilliSeconds {
  def apply(s: String): Either[String, MilliSeconds] =  for {
    i <- parseInt(s)
    r <- Either.cond(i >= 0 && i <= 999, new MilliSeconds(i) {}, "MilliSeconds values are >= 0 and <= 999")
  } yield r
}

object parseInt {
  import scala.util.Try
  def apply(s: String): Either[String, Int] =
    Try(s.toInt)
      .toEither
      .leftMap(_.getStackTrace.toList.mkString("\n"))
}

/**
 * MatchTime representation
 * @param period subject to only valid states as per Period
 * @param minutes subject to only valid states as per Minutes
 * @param seconds subject to only valid states as per Seconds
 * @param milliseconds subject to only valid states as per MilliSeconds
 */
sealed abstract case class MatchTime(period: Period,
                                     minutes: Minutes,
                                     seconds: Seconds,
                                     milliseconds: MilliSeconds
                                    )

object MatchTime {
  def apply(period: String,
            minutes: String,
            seconds: String,
            milliseconds: String): Either[String, MatchTime] = for {
    per <- Period(period)
    min <- Minutes(minutes)
    sec <- Seconds(seconds)
    mil <- MilliSeconds(milliseconds)
  } yield new MatchTime(per, min, sec, mil) {}

  implicit val showMatchTime: Show[MatchTime] = Show.show[MatchTime] { mt =>
    def pad(i: Int): String = f"$i%02d"
    Seconds(if (mt.milliseconds.s >= 500) mt.seconds.s + 1 else mt.seconds.s)
      .fold(
        _ => Invalid.invalid,
        roundedMillisInSecs =>
          mt.minutes.s match {
            case 45 if mt.period == FirstHalf => s"${45}:00 +00:${pad(roundedMillisInSecs.s)} – ${mt.period.show}"
            case m if m >= 0 && m <= 45 => s"${pad(m)}:${pad(roundedMillisInSecs.s)} – ${mt.period.show}"
            case m if m > 45 && m < 90 => s"${45}:00 +${pad(m - 45)}:${pad(roundedMillisInSecs.s)} – ${mt.period.show}"
            case m if m >= 90 => s"${90}:00 +${pad(m - 90)}:${pad(roundedMillisInSecs.s)} – ${mt.period.show}"
          }
      )
  }
}

/**
 * Entry point of the application, this is the imperative shell
 * that performs impure, unsafe operations, and triggers the pure core.
 */
object Main extends App {
  require(args.length == 1, "\n Usage: sbt \"run the-input-match-time\"")
  println(Converter.parseThenRender(args(0)))
}
