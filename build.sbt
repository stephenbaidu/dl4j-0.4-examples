name := "dl4j-0.4-examples"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "com.google.guava" % "guava" % "18.0",
  "org.deeplearning4j" % "deeplearning4j-core" % "0.4-rc3.4",
  "org.deeplearning4j" % "deeplearning4j-nlp" % "0.4-rc3.4",
  "org.deeplearning4j" % "deeplearning4j-ui" % "0.4-rc3.4",
  "org.jblas" % "jblas" % "1.2.4",
  "org.nd4j" % "canova-nd4j-image" % "0.0.0.11",
  "org.nd4j" % "nd4j-jblas" % "0.4-rc3.5",
  "org.nd4j" % "nd4j-x86" % "0.4-rc3.5"
)