// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.crossProject
import PgpKeys.publishSigned

name := "webmodels"

val currentScalaVersion = "2.12.14"
val scala213Version     = "2.13.6"
val circeVersion        = "0.14.1"
val specs2Version       = "4.12.3"

val flagsFor12 = Seq(
  "-Xlint:_",
  "-Ywarn-infer-any",
  "-opt-inline-from:<sources>",
  "-opt:l:method"
)

val flagsFor13 = Seq(
  "-Xlint:_",
  "-opt-inline-from:<sources>",
  "-opt:l:method"
)

ThisBuild / crossScalaVersions   := Seq(currentScalaVersion, scala213Version)
ThisBuild / scalaVersion         := (ThisBuild / crossScalaVersions).value.last
ThisBuild / mimaFailOnNoPrevious := false // Set this to true when we start caring about binary compatibility
ThisBuild / versionScheme        := Some(VersionScheme.EarlySemVer)

ThisBuild / Test / scalacOptions += "-Yrangepos"

ThisBuild / githubWorkflowPublishTargetBranches := Seq()

ThisBuild / organization := "org.mdedetrich"
ThisBuild / homepage     := Some(url("https://github.com/mdedetrich/webmodels"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/mdedetrich/webmodels"), "git@github.com:mdedetrich/webmodels.git")
)
ThisBuild / developers := List(
  Developer("mdedetrich", "Matthew de Detrich", "mdedetrich@gmail.com", url("https://github.com/mdedetrich"))
)
ThisBuild / licenses += ("BSD 2 Clause", url("https://opensource.org/licenses/BSD-2-Clause"))
ThisBuild / publishMavenStyle      := true
ThisBuild / publishTo              := sonatypePublishTo.value
ThisBuild / Test / publishArtifact := false
ThisBuild / pomIncludeRepository   := (_ => false)
ThisBuild / scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n == 13 =>
      flagsFor13
    case Some((2, n)) if n == 12 =>
      flagsFor12
  }
}

lazy val root = project
  .in(file("."))
  .aggregate(webmodelsJS, webmodelsJVM)
  .settings(
    publish / skip       := true,
    publishSigned / skip := true,
    publishLocal / skip  := true
  )

lazy val webmodels = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    name := "webmodels"
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

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(List("mimaReportBinaryIssues"), name = Some("Report binary compatibility issues")),
  WorkflowStep.Sbt(List("clean", "coverage", "test"), name = Some("Build project"))
)

ThisBuild / githubWorkflowBuildPostamble ++= Seq(
  // See https://github.com/scoverage/sbt-coveralls#github-actions-integration
  WorkflowStep.Sbt(
    List("coverageReport", "coverageAggregate", "coveralls"),
    name = Some("Upload coverage data to Coveralls"),
    env = Map(
      "COVERALLS_REPO_TOKEN" -> "${{ secrets.GITHUB_TOKEN }}",
      "COVERALLS_FLAG_NAME"  -> "Scala ${{ matrix.scala }}"
    )
  )
)

// This is causing problems with env variables being passed in, see
// https://github.com/sbt/sbt/issues/6468
ThisBuild / githubWorkflowUseSbtThinClient := false

ThisBuild / githubWorkflowPublishTargetBranches := Seq()

import ReleaseTransformations._

releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeReleaseAll"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
