name := "converter"

version := "0.1"

scalaVersion := "2.13.2"

scalacOptions in ThisBuild ++= Seq(
  "-language:_",
  "-Xfatal-warnings",
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.2.0-M2",
  "org.scalatest" %% "scalatest" % "3.2.0-M4" % Test
)

scalastyleFailOnWarning := true

addCommandAlias("fullCoverage", ";clean;coverage;test;coverageReport;coverageOff")
