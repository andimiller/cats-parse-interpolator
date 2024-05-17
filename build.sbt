import xerial.sbt.Sonatype._
import sbtwelcome._

ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.13.14"

val commonSettings = List(
  logo        := "cats-parse-interpolator",
  usefulTasks := Seq(
    UsefulTask("+test", "test on all versions").alias("t"),
    UsefulTask("readmeJVM / mdoc", "rebuild readme from the docs folder").alias("r"),
    UsefulTask("scalafmtSbt; scalafmtAll", "reformat all files").alias("f"),
    UsefulTask("codegenJVM/run", "run code generator").alias("c")
  )
)

lazy val codegen = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("codegen"))
  .settings(
    publishArtifact := false,
    libraryDependencies ++= List(
      "co.fs2" %%% "fs2-io" % "3.10.2"
    )
  )

lazy val `cats-parse-interpolator` = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    organization           := "net.andimiller",
    name                   := "cats-parse-interpolator",
    crossScalaVersions     := List("2.13.14", "3.4.1"),
    libraryDependencies ++= List(
      "org.typelevel" %%% "cats-parse"         % "1.0.0",
      "org.scalameta" %%% "munit"              % "1.0.0-M11" % Test, // these have been lowered in version because I'm waiting for cats to get on scala native 0.5
      "org.scalameta" %%% "munit-scalacheck"   % "1.0.0-M11" % Test,
      "eu.timepit"    %%% "refined-scalacheck" % "0.11.1"    % Test
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
    publishArtifact := false,
    mdocVariables   := Map(
      "VERSION" -> version.value
    ),
    mdocIn          := file("./docs/readme.md"),
    mdocOut         := file("./readme.md")
  )
  .dependsOn(`cats-parse-interpolator`)
  .enablePlugins(MdocPlugin)
