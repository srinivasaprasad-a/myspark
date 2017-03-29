
package srny.streaming

import kafka.serializer.StringDecoder
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkContext

object StatefulStreamingClient {
  def parser(json: String): String = {
    return json
  }
  
  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println(s"""
        |Usage: DirectKafkaWordCount <brokers> <topics>
        |  <brokers> is a list of one or more Kafka brokers
        |  <topics> is a list of one or more kafka topics to consume from
        |
        """.stripMargin)
      System.exit(1)
    }
    
    var opponent_strength = 30D
    val Array(brokers, topics) = args

    val sparkConf = new SparkConf()
      .set("spark.driver.allowMultipleContexts", "true")
      .set("spark.executor.memory", "4g")
      .set("spark.driver.memory", "4g")
      .setAppName("SparkStreamingClient")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    //val spark = SparkSession.builder().appName("SparkStreamingClient").getOrCreate()
    //import spark.implicits._

    /*val initialRDD = sc.parallelize(List(("oppstrength", 30D)))
    val stateSpec = StateSpec.function(trackStateFunc _).initialState(initialRDD).timeout(Seconds(60))*/
    
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)

    val lines = messages.map(_._2).map(parser)
    lines.print()
    lines.foreachRDD(rdd => {
      val sqlCtx = new SQLContext(rdd.sparkContext)
      if (!rdd.partitions.isEmpty) {
        println("JSON RDD is not empty")
        //val jsonRdd = sqlCtx.read.json(rdd).createOrReplaceTempView("tempTable")
        sqlCtx.jsonRDD(rdd).registerTempTable("tempTable")
        val lineDF = sqlCtx.sql("select action, weapon, target, strength from tempTable")
        lineDF.select(lineDF("style"), lineDF("action"), lineDF("weapon"), lineDF("strength")).show()
        //lineDF.printSchema()
        
        /*var act, body, wep = ""
        var stren:Double = 0
        lineDF.rdd.map(t => {
          act = t.getAs[String]("action")
          body = t.getAs[String]("target")
          wep = t.getAs[String]("weapon")
          stren = t.getAs[Double]("strength")
        })*/
        
        /*val tempDF = lineDF.select(lineDF("action"), lineDF("weapon"), lineDF("target"), lineDF("strength")).collectAsList()
        val body = tempDF.get(0).getAs[String]("target")
        val stren = tempDF.get(0).getAs[Double]("strength")
        val act = tempDF.get(0).getAs[String]("action")
        val wep = tempDF.get(0).getAs[String]("weapon")
        
        act match {
          case "KICK" => {
            println(s"$stren damage is recorded on the $body. Body has a total life of ${opponent_strength - stren} ($opponent_strength - $stren)")
            opponent_strength = opponent_strength - stren
            if (opponent_strength<=0D) {
              println(s"wepon he used on the killing blow was the $wep")
              opponent_strength = 30D
            }
          }
          case "PUNCH" => {
            println(s"$stren damage is recorded on the $body. Body has a total life of ${opponent_strength - stren} ($opponent_strength - $stren)")
            opponent_strength = opponent_strength - stren
            if (opponent_strength<=0D) {
              println(s"wepon he used on the killing blow was the $wep")
              opponent_strength = 30D
            }
          }
          case _ => {
            println(s"$act is ignored")
          }
        }*/
        
      }
    })
    
    ssc.start()
    ssc.awaitTermination()
  }
  
  /*def trackStateFunc(batchTime: Time, key: String, value: Option[Int], state: State[Long]): Option[(String, Long)] = {
    val sum = value.getOrElse(0).toLong + state.getOption.getOrElse(0L)
    val output = (key, sum)
    state.update(sum)
    Some(output)
  }*/
  /*def trackStateFunc(batchTime: Time, key: String, value: Option[Double], state: State[Double]): Option[(String, Double)] = {
    val diff = state.getOption.getOrElse(0D) - value.getOrElse(0D)
    val output = (key, diff)
    state.update(diff)
    Some(output)
  }*/
}
