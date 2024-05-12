lazy val commonSettings = Seq(
  organization := "gg.warcraft",
  version := "20.2.4-SNAPSHOT",
  scalaVersion := "2.13.12",
  scalacOptions ++= Seq(
    "-language:implicitConversions"
  ),
  resolvers ++= Seq(
    Resolver.mavenLocal
  )
)

lazy val assemblySettings = Seq(
  assembly / assemblyJarName := s"${name.value}-${version.value}-all.jar",
  assembly / assemblyOption ~= { _.withIncludeScala(false) },
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", _ @ _*) => MergeStrategy.discard
    case "module-info.class"           => MergeStrategy.discard
    case it                            => (assembly / assemblyMergeStrategy).value(it)
  }
)

lazy val api = (project in file("chat-api"))
  .settings(
    name := "chat-api",
    commonSettings,
    libraryDependencies ++= Seq(
      "gg.warcraft" %% "monolith-api" % "20.2.4-SNAPSHOT" % Provided
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
      "io.papermc.paper" % "paper-api" % "1.20.2-R0.1-SNAPSHOT" % Provided,
      "gg.warcraft" %% "monolith-spigot" % "20.2.4-SNAPSHOT" % Provided
    )
  )
  .dependsOn(api)
