import mdoc.{DocusaurusPlugin, MdocPlugin}
import sbt.Keys.*
import sbt.plugins.JvmPlugin
import sbt.{Def, *}


object Mdoc extends AutoPlugin {

  override def requires: Plugins = JvmPlugin

  override def trigger: PluginTrigger = allRequirements

  import MdocPlugin.autoImport.*

  trait Keys {
    type Settings = Seq[Setting[?]]
    lazy val mdocDirectory = settingKey[File]("Directory containing Markdown docs")

    lazy val mdocumented = settingKey[Boolean]("A flag indicating whether a project is documented for Mdoc i.e. it has a docs subdirectory")
  }

  object autoImport extends Keys

  import autoImport.*

  def isRootProject: Def.Initialize[Boolean] = thisProject.zipWith(LocalRootProject / thisProject)(_ == _)

  def isDocProjectAux(projectName: String, projectBase: File, rootName: String, rootBase: File): Boolean = {
    val names = Set(s"${rootBase.getName}-docs", s"$rootName-docs", "site")
    names(projectName) || names(projectBase.getName)
  }

  def isDocProject: Def.Initialize[Boolean] = Def.setting {
    isDocProjectAux(name.value, baseDirectory.value, (LocalRootProject / name).value, (LocalRootProject / baseDirectory).value)
  }

  def docProject = Def.setting {
    Def.setting((name.value, baseDirectory.value, (LocalRootProject / name).value, (LocalRootProject / baseDirectory).value))
      .all(ScopeFilter(inAggregates(LocalRootProject)))
      .value
      .filter((isDocProjectAux _).tupled)
      .collect {
        case (_, base, _, _) if (base / "docs").isDirectory =>
          Seq(
            "--in", s"$base/docs",
            "--out", s"${target.value}/mdoc",
          )
      }
      .flatten
  }

  def rootDocs = Def.setting { (LocalRootProject / baseDirectory).value / "docs" }

  def mdocumentedDependencies =
    Def.setting ((mdocumented.value, name.value, baseDirectory.value)) .all(ScopeFilter(inDependencies(ThisProject) && -inProjects(LocalRootProject)))

  def mdocumentedInsAndOuts = Def.setting {
    val root = Seq(
      "--in", s"${rootDocs.value}",
      "--out", s"${target.value}/mdoc",
    )
    val readme = Seq((ThisBuild / baseDirectory).value / "README.md")
      .filter(_.exists())
      .flatMap { file =>
        val imported = if ((rootDocs.value / "01-Welcome" / "README.mdx").exists()) "_" else ""
        Seq(
          "--in", s"$file",
          "--out",  s"${target.value}/mdoc/01-Welcome/${imported}README.md",
        )
      }
    val site = docProject.value
    val deps = mdocumentedDependencies.value.collect {
      case (true, name, base) =>
        Seq(
          "--in", s"$base/docs",
          "--out", s"${target.value}/mdoc/$name",
        )
    }
    root ++ readme ++ site ++ deps.flatten
  }

  override def buildSettings: Settings = Seq(
    mdocDirectory := baseDirectory.value / "docs",
    mdocVariables := Map(
      "SCALA_VERSION" -> scalaVersion.value,
      "VERSION" -> version.value
    ),
  )

  override def projectSettings: Settings =
    MdocPlugin.projectSettings ++
    DocusaurusPlugin.projectSettings ++
    Seq(
      moduleName := baseDirectory.value.getName,
      mdocDirectory := baseDirectory.value / "docs",
      mdocVariables := (ThisBuild / mdocVariables).value,
      mdocumented := (mdocDirectory.value ** "*.md").get().nonEmpty,
      mdocExtraArguments ++= Seq(
        "--no-livereload",
        "--verbose true",
        "--allow-code-fence-indented",
        "--exclude **/.DS_Store",
        "--exclude .DS_Store",
      ),
      mdocExtraArguments ++= mdocumentedInsAndOuts.value,
    )
}
