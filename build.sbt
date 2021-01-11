import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val catsV = "2.3.0"
val sttpV = "3.0.0-RC15"
val disciplineScalaTest = "2.1.1"
val catsLawsV = "2.0.0"
val scalaTestV = "3.2.3"
val scalaTestPlusScalaCheckV = "3.2.3.0"

val kindProjectorV = "0.11.2"
val betterMonadicForV = "0.3.1"

// Projects
lazy val `sttp-cats` = project.in(file("."))
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .aggregate(core.jvm, core.js)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "sttp-cats"
  )

lazy val site = project.in(file("site"))
  .disablePlugins(MimaPlugin)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(MdocPlugin)
  .enablePlugins(NoPublishPlugin)
  .settings(commonSettings)
  .dependsOn(core.jvm)
  .settings{
    import microsites._
    Seq(
      micrositeName := "sttp-cats",
      micrositeDescription := "Cats instances for sttp",
      micrositeAuthor := "Brian Holt",
      micrositeGithubOwner := "bpholt",
      micrositeGithubRepo := "sttp-cats",
      micrositeBaseUrl := "/sttp-cats",
      micrositeDocumentationUrl := "https://www.javadoc.io/doc/com.planetholt/sttp-cats_2.13",
      micrositeGitterChannelUrl := "bpholt/libraries", // Feel Free to Set To Something Else
      micrositeFooterText := None,
      micrositeHighlightTheme := "atom-one-light",
      micrositePalette := Map(
        "brand-primary" -> "#3e5b95",
        "brand-secondary" -> "#294066",
        "brand-tertiary" -> "#2d5799",
        "gray-dark" -> "#49494B",
        "gray" -> "#7B7B7E",
        "gray-light" -> "#E5E5E6",
        "gray-lighter" -> "#F4F3F4",
        "white-color" -> "#FFFFFF"
      ),
      micrositeCompilingDocsTool := WithMdoc,
      scalacOptions in Tut --= Seq(
        "-Xfatal-warnings",
        "-Ywarn-unused-import",
        "-Ywarn-numeric-widen",
        "-Ywarn-dead-code",
        "-Ywarn-unused:imports",
        "-Xlint:-missing-interpolator,_"
      ),
      micrositePushSiteWith := GitHub4s,
      micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
      micrositeExtraMdFiles := Map(
          file("CODE_OF_CONDUCT.md")  -> ExtraMdFileConfig("code-of-conduct.md",   "page", Map("title" -> "code of conduct",   "section" -> "code of conduct",   "position" -> "100")),
          file("LICENSE")             -> ExtraMdFileConfig("license.md",   "page", Map("title" -> "license",   "section" -> "license",   "position" -> "101"))
      )
    )
  }

// General Settings
lazy val commonSettings = Seq(
  scalaVersion := "2.13.4",
  crossScalaVersions := Seq(scalaVersion.value, "2.12.12"),

  addCompilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorV cross CrossVersion.full),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % betterMonadicForV),

  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % catsV,
    "com.softwaremill.sttp.client3" %%% "core" % sttpV,
    "org.typelevel" %% "cats-laws" % catsLawsV % Test,
    "org.typelevel" %% "discipline-scalatest" % disciplineScalaTest % Test,
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
    "org.scalatestplus" %% "scalacheck-1-15" % scalaTestPlusScalaCheckV % Test,
  )
)

// General Settings
inThisBuild(List(
  organization := "com.planetholt",
  developers := List(
    Developer("bpholt", "Brian Holt", "bholt+sttp-cats@planetholt.com", url("https://github.com/bpholt"))
  ),

  homepage := Some(url("https://github.com/bpholt/sttp-cats")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),

  pomIncludeRepository := { _ => false},
  scalacOptions in (Compile, doc) ++= Seq(
      "-groups",
      "-sourcepath", (baseDirectory in LocalRootProject).value.getAbsolutePath,
      "-doc-source-url", "https://github.com/bpholt/sttp-cats/blob/v" + version.value + "€{FILE_PATH}.scala"
  )
))
