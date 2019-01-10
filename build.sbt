enablePlugins(SbtTwirl)

TaskKey[Unit]("generateSources") := {
  val outdir = target.value / "sources"
  val classpath = (fullClasspath in Compile).value
  val scalaRun = (runner in run).value
  val log = streams.value.log
  val baseDir = baseDirectory.value

  // Clear the output directory first
  IO.delete(outdir)

  // Find the templates
  val templates = (sources in (Compile, TwirlKeys.compileTemplates)).value pair Path.relativeTo((sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value)

  val templateClasses = templates.map {
    case(_, name) =>
      val splitted = name.split('/')
      val fileName = splitted.last
      val Array(clazz, _, t) = fileName.split('.')
      (splitted.dropRight(1) ++ Seq(t, clazz)).mkString(".")
  }

  scalaRun.run("SourcesGenerator", Attributed.data(classpath), Seq(outdir.getAbsolutePath) ++ templateClasses, log).failed foreach (sys error _.getMessage)
}
