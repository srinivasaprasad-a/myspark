
package srny.streaming

import kafka.serializer.StringDecoder
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}

object CustLog extends Serializable {
  @transient lazy val log = Logger.getLogger(getClass.getName)
}

object SparkStreamingClient {
  def parser(json: String): String = {
    return json
  }
  
  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println(s"""
        |Usage: SparkStreamingClient <brokers> <topics>
        |  <brokers> is a list of one or more Kafka brokers
        |  <topics> is a list of one or more kafka topics to consume from
        |
        """.stripMargin)
      System.exit(1)
    }
    
    val Array(brokers, topics) = args

    val sparkConf = new SparkConf().set("spark.driver.allowMultipleContexts", "true").setAppName("SparkStreamingClient")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    val spark = SparkSession.builder().appName("SparkStreamingClient").getOrCreate()
    import spark.implicits._

    val opponent_strength = sc.accumulator(30D, "opponent_strength")
    val wushu_style_cntr = sc.accumulator(0, "wushu_style_cntr")
    val kungfu_style_cntr = sc.accumulator(0, "kungfu_style_cntr")
    val drunken_style_cntr = sc.accumulator(0, "drunken_style_cntr")

    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)

    val lines = messages.map(_._2).map(parser)
    lines.print()
    
    lines.foreachRDD(rdd => {
      val sqlCtx = new SQLContext(rdd.sparkContext)

      if (!rdd.partitions.isEmpty) {
        sqlCtx.jsonRDD(rdd).registerTempTable("tempTable")
        val lineDF = sqlCtx.sql("select * from tempTable")
        
        if (lineDF.count() != 0) {
          val tempDF = lineDF.select($"strength",$"target",$"action",$"weapon",$"style",$"timestamp").collectAsList()
          val stren = tempDF.get(0).getAs[Double]("strength")
          val body = tempDF.get(0).getAs[String]("target")
          val act = tempDF.get(0).getAs[String]("action")
          val wep = tempDF.get(0).getAs[String]("weapon")
          val style = tempDF.get(0).getAs[String]("style")
          val ts = tempDF.get(0).getAs[String]("timestamp")
          
          CustLog.log.error(s"$ts: stren,body,$act,$wep,$style")
          
          style match {
            case "WUSHU" => {wushu_style_cntr += 1}
            case "KUNG_FU" => {kungfu_style_cntr += 1}
            case "DRUNKEN_BOXING" => {drunken_style_cntr += 1}
          }
        
          act match {
            case "KICK" | "PUNCH" => {
                       println(s"$stren damage is recorded on the $body. Body has a total life of ${opponent_strength.value - stren} (${opponent_strength.value} - $stren)")
                       CustLog.log.error(s"$stren damage is recorded on the $body. Body has a total life of ${opponent_strength.value - stren} (${opponent_strength.value} - $stren)")
                       
                       opponent_strength.value_=({opponent_strength.value - stren})
                       if (opponent_strength.value<=0D) {
                          if (wushu_style_cntr.value > kungfu_style_cntr.value && wushu_style_cntr.value > drunken_style_cntr.value) {
                            CustLog.log.error(s"His favorite style is WUSHU as he used it ${wushu_style_cntr.value} times and the weapon he used on the killing blow was the $wep")
                          } else if (kungfu_style_cntr.value > wushu_style_cntr.value && kungfu_style_cntr.value > drunken_style_cntr.value) {
                            CustLog.log.error(s"His favorite style is KUNG_FU as he used it ${kungfu_style_cntr.value} times and the weapon he used on the killing blow was the $wep")
                          } else if (drunken_style_cntr.value > wushu_style_cntr.value && drunken_style_cntr.value > kungfu_style_cntr.value) {
                            CustLog.log.error(s"His favorite style is DRUNKEN_BOXING as he used it ${drunken_style_cntr.value} times and the weapon he used on the killing blow was the $wep")
                          }
                          
                          opponent_strength.value_=(30D)                          
                          wushu_style_cntr.value_=(0)
                          kungfu_style_cntr.value_=(0)
                          drunken_style_cntr.value_=(0)
                       }
            }
            case _ => {
              println(s"$act is ignored")
              CustLog.log.error(s"$act is ignored")
            }
          }
          
        }
        
      }
      
    })
    
    ssc.start()
    Thread.sleep(1000)
    ssc.awaitTermination()
  }
}
