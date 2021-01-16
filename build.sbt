lazy val commonSettings = Seq(
  organization := "gg.warcraft",
  version := "16.0.0-SNAPSHOT",
  scalaVersion := "2.13.4",
  scalacOptions ++= Seq(
    "-language:implicitConversions"
  ),
  resolvers ++= Seq(
    Resolver.mavenLocal
  )
)

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := s"${name.value}-${version.value}-all.jar",
  assemblyOption in assembly :=
    (assemblyOption in assembly).value.copy(includeScala = false),
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", it @ _*) => MergeStrategy.discard
    case "module-info.class"           => MergeStrategy.discard
    case it                            => (assemblyMergeStrategy in assembly).value(it)
  }
)

lazy val api = (project in file("chat-api"))
  .settings(
    name := "chat-api",
    commonSettings,
    libraryDependencies ++= Seq(
      "gg.warcraft" %% "monolith-api" % "16.0.0-SNAPSHOT" % Provided
    ) ++ Seq(
      "org.scalatest" %% "scalatest" % "3.2.+" % Test
    )
  )

lazy val spigot = (project in file("chat-spigot"))
  .settings(
    name := "chat-spigot",
    commonSettings,
    assemblySettings,
    resolvers ++= Seq(
      "PaperMC" at "https://papermc.io/repo/repository/maven-public/"
    ),
    libraryDependencies ++= Seq(
      "gg.warcraft" %% "monolith-spigot" % "16.0.0-SNAPSHOT" % Provided,
      "com.destroystokyo.paper" % "paper-api" % "1.16.4-R0.1-SNAPSHOT" % Provided
    )
  )
  .dependsOn(api)
