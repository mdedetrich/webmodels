name := "webmodels-root"

val currentScalaVersion = "2.12.2"

scalaVersion in ThisBuild := currentScalaVersion
crossScalaVersions in ThisBuild := Seq("2.11.11", currentScalaVersion)

lazy val root = project
  .in(file("."))
  .aggregate(webmodelsJS, webmodelsJVM)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val webmodels = crossProject
  .in(file("."))
  .settings(
    name := "webmodels",
    version := "0.1.0-SNAPSHOT"
  )
  .jvmSettings(
    // Add JVM-specific settings here
  )
  .jsSettings(
    // Add JS-specific settings here
  )

lazy val webmodelsJVM = webmodels.jvm
lazy val webmodelsJS = webmodels.js
