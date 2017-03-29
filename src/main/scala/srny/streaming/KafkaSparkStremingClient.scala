package srny.streaming

import kafka.serializer.StringDecoder
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

object KafkaSparkStremingClient {
  def parser(json: String): String = {
    return json
  }
  
  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println(s"""
        |Usage: KafkaSparkStremingClient <brokers> <topics>
        |  <brokers> is a list of one or more Kafka brokers
        |  <topics> is a list of one or more kafka topics to consume from
        |
        """.stripMargin)
      System.exit(1)
    }

    val Array(brokers, topics) = args

    val sparkConf = new SparkConf()
                    .setMaster("local[*]")
                    .set("spark.driver.allowMultipleContexts", true.toString())
                    .setAppName("KafkaSparkStremingClient")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    
    implicit val myClassFormat = Json.format[entry]
    
    val msg = messages.map(m => Json.parse(m._2).as[entry])
    msg.print()
    msg.foreachRDD(rdd => {
      println(rdd.first().action)
      println(rdd.first().strength)
      println(rdd.first().style)
      println(rdd.first().target)
      println(rdd.first().weapon)
    })

    /*val lines = messages.map(_._2).map(parser)
    lines.print()
    lines.foreachRDD(rdd => {
      val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)
      import spark.implicits._
            
      //val tempRDD = rdd.map(r => entry(r(0).toString(),r(1).toDouble,r(2).toString(),r(3).toString(),r(4).toString(),r(5).toString()))
      //val tempDF = tempRDD.toDF()
      val tempDF = spark.read.json(rdd)
      tempDF.createOrReplaceTempView("tempTable")
      val rowDF = spark.sql("select * from tempTable").toDF("action","strength","style","target","timestamp","weapon")
      rowDF.show()
    })*/
    
    ssc.start()
    ssc.awaitTermination()
  }
  
  case class entry(action:String,strength:Double,style:String,target:String,timestamp:String,weapon:String)
  
  object SparkSessionSingleton {
    @transient  private var instance: SparkSession = _
  
    def getInstance(sparkConf: SparkConf): SparkSession = {
      if (instance == null) {
        instance = SparkSession
          .builder
          .config(sparkConf)
          .getOrCreate()
      }
      instance
    }
  }
  
}