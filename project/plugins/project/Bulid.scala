import sbt._

object PluginDef extends Build {
  lazy val root = Project("root", file(".")) /* dependsOn (
    uri("git://github.com/ijuma/sbt-idea.git#sbt-0.10")
  ) */
}
