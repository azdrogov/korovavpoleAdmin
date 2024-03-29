import sbt.addCompilerPlugin

name := "korovavpoleAdmin"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-ember-server" % "0.23.16",
    "dev.zio" %% "zio" % "2.0.21",
    "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.9.6",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % "1.9.6",
    "org.typelevel" %% "cats-core" % "2.10.0",
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.9.6",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.9.6",
)

assembly / assemblyMergeStrategy := {
    case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") =>
        MergeStrategy.singleOrError
    case PathList("META-INF", "resources", "webjars", "swagger-ui", _*)               =>
        MergeStrategy.singleOrError
    case PathList("META-INF", _*)                                                     => MergeStrategy.discard // Optional, but usually required
    case x                                                                            =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
}