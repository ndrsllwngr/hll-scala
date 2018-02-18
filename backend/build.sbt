lazy val commonSettings = Seq(
  name := "scalable",
  organization := "de.scalable",
  version := "0.1",
  scalaVersion := "2.12.4",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation",
    "-encoding", "utf8", "-Xfatal-warnings")
)

mainClass := Some("de.scalable.Main")

libraryDependencies ++= {
  // Dependencies
  val akkaVersion     = "2.4.20"
  val akkaHttpVersion = "10.0.11"
  val slickVersion    = "3.2.1"
  val slickPgVersion  = "0.15.4"
  val jwtVersion      = "1.2.2"

  val postgresVersion       = "42.1.4"
  val logbackVersion        = "1.2.3"

  Seq(
    "com.typesafe.akka"   %% "akka-actor"           % akkaVersion,
    "com.typesafe.akka"   %% "akka-slf4j"           % akkaVersion,
    "com.typesafe.akka"   %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka"   %% "akka-http-core"       % akkaHttpVersion,
    "com.typesafe.akka"   %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.slick"  %% "slick"                % slickVersion,
    "com.typesafe.slick"  %% "slick-hikaricp"       % slickVersion,
    "com.github.tminglei" %% "slick-pg"             % slickPgVersion,
    "com.github.tminglei" %% "slick-pg_spray-json"  % slickPgVersion,
    "io.igl"              %% "jwt"                  % jwtVersion,
    "ch.megard"           %% "akka-http-cors"       % "0.2.2",

  "org.postgresql"        % "postgresql"            % postgresVersion,
  "ch.qos.logback"        % "logback-classic"       % logbackVersion

  )
}

initialCommands in console :=
  """import de.scalable._
    |import de.scalable.database._
    |import de.scalable.database.queries._
    |import de.scalable.model._
    |import slick.backend.DatabaseConfig
    |import slick.jdbc.JdbcProfile
    |import de.scalable.database.ScalablePostgresProfile.api._
  """.stripMargin
