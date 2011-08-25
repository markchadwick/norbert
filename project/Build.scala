import sbt._
import Keys._


object BuildSettings {
  val buildOrganization   = "com.linkedin.norberg"
  val buildName           = "norbert"
  val buildVersion        = "0.6.9"
  val buildScalaVersion   = "2.9.0"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization  := buildOrganization,
    name          := buildName,
    version       := buildVersion,
    scalaVersion  := buildScalaVersion
  )
}


object Resolvers {
  val snapshots = ScalaToolsSnapshots
  val jboss = "JBoss Maven 2 Repository" at
    "http://repository.jboss.org/nexus/content/groups/public"
}


object Dependencies {
  val cglib       = "cglib" % "cglib" % "2.1_3" % "test"
  val log4j       = "log4j" % "log4j" % "1.2.16"
  val mockito     = "org.mockito" % "mockito-all" % "1.8.4" % "test"
  val netty       = "org.jboss.netty" % "netty" % "3.2.3.Final"
  val objenesis   = "org.objenesis" % "objenesis" % "1.0" % "test"
  val protobuf    = "com.google.protobuf" % "protobuf-java" % "2.4.0a"
  val slf4j       = "org.slf4j" % "slf4j-api" % "1.5.6"
  val slf4jLog4j  = "org.slf4j" % "slf4j-log4j12" % "1.5.6"
  val specs       = "org.scala-tools.testing" %% "specs" % "1.6.7" % "test"
  val zookeeper   = "org.apache.zookeeper" % "zookeeper" % "3.3.0" from
    "http://repo1.maven.org/maven2/org/apache/zookeeper/zookeeper/3.3.0/zookeeper-3.3.0.jar"

  val clusterDependencies =
    cglib ::
    log4j ::
    mockito ::
    objenesis ::
    protobuf ::
    specs ::
    zookeeper ::
    Nil

  val networkDependencies =
    netty ::
    slf4j ::
    slf4jLog4j ::
    Nil
}


object NorbertBuild extends Build {

  lazy val norbert = Project(
    id        = "norbert",
    base      = file("."),
    settings  = BuildSettings.buildSettings
  ) aggregate (cluster, network, javaCluster, javaNetwork, examples)

  lazy val cluster = Project(
    id        = "Norbert Cluster",
    base      = file("cluster"),
    settings  = BuildSettings.buildSettings ++
                Seq(libraryDependencies ++= Dependencies.clusterDependencies)
  )

  lazy val network = Project(
    id        = "Norbert Network",
    base      = file("network"),
    settings  = BuildSettings.buildSettings ++
                Seq(libraryDependencies ++= Dependencies.networkDependencies)
  ) dependsOn (cluster)

  lazy val javaCluster = Project(
    id        = "Norbert Java Cluster",
    base      = file("java-cluster"),
    settings  = BuildSettings.buildSettings
  ) dependsOn (cluster)

  lazy val javaNetwork = Project(
    id        = "Norbert Java Network",
    base      = file("java-cluster"),
    settings  = BuildSettings.buildSettings
  ) dependsOn (cluster, javaCluster, network)

  lazy val examples = Project(
    id        = "Norbert Examples",
    base      = file("examples"),
    settings  = BuildSettings.buildSettings
  ) dependsOn (network, javaNetwork)

  /*
  val oneJarName = artifactID + "-" + version + ".jar"

  lazy val oneJar = oneJarTask() dependsOn(publishLocal)

  def oneJarTask(): Task = task {
    FileUtilities.doInTemporaryDirectory(log) { temp: File =>
      projectClosure.dropRight(1).foreach { project =>
          println(project.outputPath)
          val files = (project.outputPath ** "*.jar").getFiles
          val paths = Path.fromFiles(files)
          paths.foreach(path => FileUtilities.unzip(path, Path.fromFile(temp), log))
      }

      Right(FileUtilities.zip(((Path.fromFile(temp) ##) ** "*.class").get,
                        outputPath / oneJarName, true, log))
    }.right.toOption.flatMap(x => x)
  }
  */
}
