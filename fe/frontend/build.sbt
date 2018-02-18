enablePlugins(ScalaJSPlugin)
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

name := "scalable-client"
scalaVersion := "2.12.4"

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
scalaJSModuleKind := ModuleKind.CommonJSModule

skip in packageJSDependencies := false
jsDependencies += "org.webjars" % "firebase" % "3.6.4" / "firebase.js"

libraryDependencies ++= {
  // Dependencies
  val scalajsDomVersion = "0.9.4"
  val scalajsReactVersion = "1.1.1"
  val scalajsReactComponentsVersion = "0.8.0"
  val circeVersion = "0.9.1"
  val diodeVersion = "1.1.2"

  Seq(
    "org.scala-js"                      %%% "scalajs-dom"               % scalajsDomVersion,

    "com.github.japgolly.scalajs-react" %%% "core"                      % scalajsReactVersion,
    "com.github.japgolly.scalajs-react" %%% "extra"                     % scalajsReactVersion,
    "com.github.japgolly.scalajs-react" %%% "test"                      % scalajsReactVersion,

    "com.olvind"                        %%% "scalajs-react-components"  % scalajsReactComponentsVersion,

    "io.circe"                          %%% "circe-core"                % circeVersion,
    "io.circe"                          %%% "circe-parser"              % circeVersion,
    "io.circe"                          %%% "circe-generic"             % circeVersion,
    "io.circe"                          %%% "circe-generic-extras"      % circeVersion,
    "io.circe"                          %%% "circe-optics"              % circeVersion,

    "io.suzaku"                         %%% "diode"                     % diodeVersion,
    "io.suzaku"                         %%% "diode-devtools"            % diodeVersion,
    "io.suzaku"                         %%% "diode-react"               % diodeVersion
  )
}

