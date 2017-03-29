name := "myspark"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.0"
libraryDependencies += "com.databricks" %% "spark-csv" % "1.5.0"
libraryDependencies += "org.apache.spark" %% "spark-hive" % "2.1.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka" % "1.6.3"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.1.0"
libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.3.0"
libraryDependencies += "org.apache.hbase" % "hbase" % "1.3.0"
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.3.0"
libraryDependencies += "com.typesafe.play" % "play-json_2.11" % "2.5.13"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
   {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
   }
}

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"
