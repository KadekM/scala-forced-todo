organization  := "com.marekkadek"

name := "forcedtodo"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/KadekM/scala-forced-todo"))

organization := "com.marekkadek"

version := "0.0.1-SNAPSHOT"

scalaVersion  := "2.11.8"

crossScalaVersions := Seq("2.10.4", "2.11.8")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
  "com.lihaoyi" %% "utest" % "0.3.1" % Test
)

testFrameworks += new TestFramework("utest.runner.Framework")

unmanagedSourceDirectories in Test <+= baseDirectory(_ / "src" / "test" / "resources")


publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}


pomExtra :=
  <scm>
    <url>https://github.com/kadekm/scala-forced-todo</url>
    <connection>scm:git://github.com/kadekm/.git</connection>
  </scm>
    <developers>
      <developer>
        <id>kadekm</id>
        <name>Marek Kadek</name>
        <url>https://github.com/KadekM</url>
      </developer>
    </developers>
