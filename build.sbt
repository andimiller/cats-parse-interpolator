import xerial.sbt.Sonatype._
import sbtwelcome._

ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.13.14"

val commonSettings = List(
  logo := "cats-parse-interpolator",
  usefulTasks := Seq(
    UsefulTask("+test", "test on all versions").alias("t"),
    UsefulTask("readmeJVM / mdoc", "rebuild readme from the docs folder").alias("r"),
    UsefulTask("scalafmtSbt; scalafmtAll", "reformat all files").alias("f")
  )
)

lazy val `cats-parse-interpolator` = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(commonSettings)
  .settings(
    organization           := "net.andimiller",
    name                   := "cats-parse-interpolator",
    crossScalaVersions     := List("2.13.14", "3.4.1"),
    libraryDependencies ++= List(
      "org.typelevel" %%% "cats-parse" % "1.0.0",
      "org.scalameta" %%% "munit"      % "1.0.0-RC1" % Test
    ),
    publishTo              := sonatypePublishTo.value,
    licenses               := Seq("Apache 2.0" -> url("https://opensource.org/license/apache-2-0")),
    sonatypeProjectHosting := Some(GitHubHosting("andimiller", "cats-parse-interpolator", "andi at andimiller dot net")),
    developers             := List(
      Developer(id = "andimiller", name = "Andi Miller", email = "andi@andimiller.net", url = url("http://andimiller.net"))
    )
  )

lazy val readme = crossProject(JVMPlatform)
  .in(file("docs"))
  .settings(commonSettings)
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    mdocIn        := file("./docs/readme.md"),
    mdocOut       := file("./readme.md")
  )
  .dependsOn(`cats-parse-interpolator`)
  .enablePlugins(MdocPlugin)