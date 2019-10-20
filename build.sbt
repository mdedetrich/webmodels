// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.crossProject
import PgpKeys.publishSigned

name := "webmodels"

val currentScalaVersion = "2.12.9"
val scala213Version     = "2.13.1"
val circeLatestVersion  = "0.12.2" // for Scala 2.12 and 2.13
val circeOldVersion     = "0.11.1" // only for scala 2.11
val specs2OldVersion    = "4.3.4"
val specs2LatestVersion = "4.8.0"

def circeVersion(scalaVer: String): String =
  if (scalaVer.startsWith("2.11")) circeOldVersion else circeLatestVersion

def specs2Version(scalaVer: String): String =
  if (scalaVer.startsWith("2.11")) specs2OldVersion else specs2LatestVersion

val flagsFor11 = Seq(
  "-Xlint:_",
  "-Yconst-opt",
  "-Ywarn-infer-any",
  "-Yclosure-elim",
  "-Ydead-code",
  "-Xsource:2.12" // required to build case class construction
)

val flagsFor12 = Seq(
  "-Xlint:_",
  "-Ywarn-infer-any",
  "-opt-inline-from:<sources>"
)

val flagsFor13 = Seq(
  "-Xlint:_",
  "-opt-inline-from:<sources>"
)

scalaVersion in ThisBuild := currentScalaVersion
crossScalaVersions in ThisBuild := Seq("2.11.12", currentScalaVersion, scala213Version)

scalacOptions in Test in ThisBuild ++= Seq("-Yrangepos")

lazy val root = project
  .in(file("."))
  .aggregate(webmodelsJS, webmodelsJVM)
  .settings(
    publish := {},
    publishLocal := {},
    publishSigned := {}
  )

lazy val webmodels = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    name := "webmodels",
    organization := "org.mdedetrich",
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
    pomIncludeRepository := (_ => false),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n == 13 =>
          flagsFor13
        case Some((2, n)) if n == 12 =>
          flagsFor12
        case Some((2, n)) if n == 11 =>
          flagsFor11
      }
    }
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "io.circe"   %% "circe-core"   % circeVersion(scalaVersion.value),
      "org.specs2" %% "specs2-core"  % specs2Version(scalaVersion.value) % Test,
      "io.circe"   %% "circe-parser" % circeVersion(scalaVersion.value) % Test
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.circe"   %%% "circe-core"   % circeVersion(scalaVersion.value),
      "org.specs2" %%% "specs2-core"  % specs2Version(scalaVersion.value) % Test,
      "io.circe"   %%% "circe-parser" % circeVersion(scalaVersion.value) % Test
    )
  )

lazy val webmodelsJVM = webmodels.jvm
lazy val webmodelsJS  = webmodels.js
