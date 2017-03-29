import org.apache.spark.sql.SparkSession


object SampleUDF {
  def main(args: Array[String]): Unit = {
    /* This Code runs fine on Spark REPL
    val conf = new SparkConf().setAppName("DataFrames")
    val sc = new SparkContext(conf)
    val newinputRDD = sc.parallelize(List(("2017","Nothing",3),("2016","CMC_xCP_Upgrade",12),("2015","eSign and DCS",12),("2014","CMC Phase 3",12),("2013","Nothing",12)))
    val newDF = newinputRDD.toDF("Year","Project","Months")
    val finalDF = newDF.map(x=>(x.getAs[String](0),if(x.getAs[String](1)=="Nothing") "BigData" else x.getAs[String](1),x.getAs[Int](2)))
		*/
    
    val spark = SparkSession.builder().appName("SampleUDF").getOrCreate()
    import spark.implicits._
    
    //Use UDF to replace column values to a new table
    val inputDF = spark.read.option("header", true).csv(args(0))
    inputDF.rdd.foreach(println)
    inputDF.createOrReplaceTempView("tempTable")
    spark.udf.register("updateNothing", (ip: String)=>(if(ip=="Nothing") "BigData" else ip))
    val FinalDF = spark.sql("select Year, updateNothing(Project) as ProjectF, Months from tempTable")
    FinalDF.rdd.foreach(println)
  }
}