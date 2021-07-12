// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.crossProject
import PgpKeys.publishSigned

name := "webmodels"

val currentScalaVersion = "2.12.12"
val scala213Version     = "2.13.4"
val circeVersion        = "0.13.0"
val specs2Version       = "4.10.5"

val flagsFor12 = Seq(
  "-Xlint:_",
  "-Ywarn-infer-any",
  "-opt-inline-from:<sources>"
)

val flagsFor13 = Seq(
  "-Xlint:_",
  "-opt-inline-from:<sources>"
)

scalaVersion in ThisBuild       := currentScalaVersion
crossScalaVersions in ThisBuild := Seq(currentScalaVersion, scala213Version)
scalacOptions in Test in ThisBuild ++= Seq("-Yrangepos")

lazy val root = project
  .in(file("."))
  .aggregate(webmodelsJS, webmodelsJVM)
  .settings(
    publish       := {},
    publishLocal  := {},
    publishSigned := {}
  )

lazy val webmodels = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    name         := "webmodels",
    organization := "org.mdedetrich",
    homepage     := Some(url("https://github.com/mdedetrich/webmodels")),
    scmInfo      := Some(ScmInfo(url("https://github.com/mdedetrich/webmodels"), "git@github.com:mdedetrich/webmodels.git")),
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
    pomIncludeRepository    := (_ => false),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n == 13 =>
          flagsFor13
        case Some((2, n)) if n == 12 =>
          flagsFor12
      }
    }
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "io.circe"   %% "circe-core"   % circeVersion,
      "org.specs2" %% "specs2-core"  % specs2Version % Test,
      "io.circe"   %% "circe-parser" % circeVersion  % Test
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.circe"   %%% "circe-core"   % circeVersion,
      "org.specs2" %%% "specs2-core"  % specs2Version % Test,
      "io.circe"   %%% "circe-parser" % circeVersion  % Test
    )
  )

lazy val webmodelsJVM = webmodels.jvm
lazy val webmodelsJS  = webmodels.js
