import com.lightbend.lagom.core.LagomVersion

ThisBuild / organization := "com.example"
ThisBuild / version ~= (_.replace('+', '-'))
ThisBuild / dynver ~= (_.replace('+', '-'))

// the Scala version that will be used for cross-compiled libraries
ThisBuild / scalaVersion := "2.13.6"

// External (unmanaged) services need to be configured for the development environment's service locator to find them.
ThisBuild / lagomUnmanagedServices := Map(
  "esco" -> "https://esco.example.com"
)
// Disable Cassandra for the development environment
ThisBuild / lagomCassandraEnabled := false

val overrideJavacOptions = Seq(
  "-g",
  "-encoding", "UTF-8",
  "-source", "11",
  "-target", "11",
  "-parameters",
  "-Xlint:unchecked",
  "-Xlint:deprecation"
)

val akkaManagementVersion = "1.0.5"

// Common dependencies and their versions.
val postgres            = "org.postgresql"                 % "postgresql"                    % "42.2.23"
val testH2              = "com.h2database"                 % "h2"                            % "1.4.200" % Test
val testMockito         = "org.mockito"                    % "mockito-core"                  % "3.12.4" % Test

// Non-implementation dependencies
val `api-deps` = Seq(
  lagomJavadslApi,
  lagomJavadslJackson,
  lagomJavadslImmutables,
  lagomJavadslTestKit,
  testMockito
)

// Project representing the complete platform
lazy val `lagom-external-service-example` = (project in file("."))
  .aggregate(
    `esco-gateway-api`,
    `esco-gateway`,
    `esco-api`
  )

// ESCO Gateway - API description
lazy val `esco-gateway-api` = (project in file("esco-gateway-api"))
  .settings(
    libraryDependencies ++= `api-deps`,
    Compile / compile / javacOptions ++= Seq("-s", (Compile / managedSourceDirectories).value.head.getAbsolutePath),
    Compile / managedClasspath ++= (Compile / managedSourceDirectories).value
  )
  .dependsOn(`esco-api`)

// ESCO Gateway - implementation
lazy val `esco-gateway` = (project in file("esco-gateway"))
  .enablePlugins(LagomJava)
  .settings(
    Compile / javacOptions := overrideJavacOptions,
    Test / javacOptions := overrideJavacOptions
  )
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceJdbc,
      lagomLogback,
      lagomJavadslTestKit,
      lagomJavadslImmutables,
      postgres,
      testH2,
      testMockito,
    ),
    lagomForkedTestSettings,
    Compile / compile / javacOptions ++= Seq("-s", (Compile / managedSourceDirectories).value.head.getAbsolutePath),
    Compile / managedClasspath ++= (Compile / managedSourceDirectories).value,
  )
  .dependsOn(`esco-gateway-api`)
  .dependsOn(`esco-api`)

// ESCO - API description
lazy val `esco-api` = (project in file("esco-api"))
  .settings(
    libraryDependencies ++= `api-deps`,
    Compile / compile / javacOptions ++= Seq("-s", (Compile / managedSourceDirectories).value.head.getAbsolutePath),
    Compile / managedClasspath ++= (Compile / managedSourceDirectories).value
  )
