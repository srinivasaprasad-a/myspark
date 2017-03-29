import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession


object ReadaJSON {
  def main(args: Array[String]): Unit = {
    /*val conf = new SparkConf().setAppName("ReadaJSON")
    val sc = new SparkContext(conf)*/

    val spark = SparkSession.builder().appName("ReadaJSON").getOrCreate()
    import spark.implicits._
    
    val jsonDF = spark.read.json(args(0))
    jsonDF.printSchema()
    jsonDF.rdd.foreach(println)
    jsonDF.createOrReplaceTempView("temptable")
    spark.sql("SELECT * FROM temptable")
    
    // Alternatively, a DataFrame can be created for a JSON dataset represented by
    // an RDD[String] storing one JSON object per string
    val otherPeopleRDD = spark.sparkContext.makeRDD(
      """{"name":"Yin","address":{"city":"Columbus","state":"Ohio"}}""" :: Nil)
    val otherPeople = spark.read.json(otherPeopleRDD)
    otherPeople.show()
      }
}