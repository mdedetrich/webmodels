import PgpKeys.publishSigned

name := "webmodels"

val currentScalaVersion = "2.12.4"
val circeVersion        = "0.9.0"
val specs2Version       = "4.0.0"

scalaVersion in ThisBuild := currentScalaVersion
crossScalaVersions in ThisBuild := Seq("2.11.11", currentScalaVersion)
scalafmtVersion in ThisBuild := "1.1.0"

scalacOptions in Test in ThisBuild ++= Seq("-Yrangepos")

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
    version := "0.2.1",
    homepage := Some(url("https://github.com/mdedetrich/webmodels")),
    scmInfo := Some(ScmInfo(url("https://github.com/mdedetrich/webmodels"), "git@github.com:mdedetrich/webmodels.git")),
    developers := List(
      Developer("mdedetrich", "Matthew de Detrich", "mdedetrich@gmail.com", url("https://github.com/mdedetrich"))
    ),
    licenses += ("BSD 3 Clause", url("https://opensource.org/licenses/BSD-3-Clause")),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false)
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "io.circe"   %% "circe-core"   % circeVersion,
      "org.specs2" %% "specs2-core"  % specs2Version % Test,
      "io.circe"   %% "circe-parser" % circeVersion % Test
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.circe"   %%% "circe-core"   % circeVersion,
      "org.specs2" %%% "specs2-core"  % specs2Version % Test,
      "io.circe"   %%% "circe-parser" % circeVersion % Test
    )
  )

lazy val webmodelsJVM = webmodels.jvm
lazy val webmodelsJS  = webmodels.js
