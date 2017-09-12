import PgpKeys.publishSigned

name := "webmodels"

val currentScalaVersion = "2.12.3"
val circeVersion = "0.8.0"

scalaVersion in ThisBuild := currentScalaVersion
crossScalaVersions in ThisBuild := Seq("2.11.11", currentScalaVersion)
scalafmtVersion in ThisBuild := "1.1.0"

lazy val root = project
  .in(file("."))
  .aggregate(webmodelsJS, webmodelsJVM)
  .settings(
    publish := {},
    publishLocal := {},
    publishSigned := {}
  )

lazy val webmodels = crossProject
  .in(file("."))
  .settings(
    name := "webmodels",
    organization := "org.mdedetrich",
    version := "0.1.4",
    homepage := Some(url("https://github.com/mdedetrich/webmodels")),
    scmInfo := Some(ScmInfo(url("https://github.com/mdedetrich/webmodels"), "git@github.com:mdedetrich/webmodels.git")),
    developers := List(
      Developer("mdedetrich", "Matthew de Detrich", "mdedetrich@gmail.com", url("https://github.com/mdedetrich"))
    ),
    licenses += ("BSD 3 Clause", url("https://opensource.org/licenses/BSD-3-Clause")),
    pomIncludeRepository := (_ => false)
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.8.0"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % "0.8.0"
    )
  )

lazy val webmodelsJVM = webmodels.jvm
lazy val webmodelsJS  = webmodels.js
