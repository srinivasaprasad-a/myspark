package srny.streaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.DoubleType

object structuredstreaming {
  def main(args: Array[String]): Unit = {
     if (args.length < 3) {
      System.err.println("Usage: srny.streaming.structuredstreaming <bootstrap-servers> <subscribe-type> <topics>")
      System.exit(1)
      }
   
     val Array(bootstrapServers, subscribeType, topics) = args

     //action:String,strength:Double,style:String,target:String,timestamp:String,weapon:String
     val schema = StructType(Array(
         StructField("action",StringType,true),
         StructField("strength",DoubleType,true),
         StructField("style",StringType,true),
         StructField("target",StringType,true),
         StructField("timestamp",StringType,true),
         StructField("weapon",StringType,true)
         ))
     
     val spark = SparkSession
      .builder
      .appName("structuredstreaming")
      .getOrCreate()

    import spark.implicits._
    
    val messages = spark
    .readStream
    .format("kafka")
    //.schema(schema)
    .option("kafka.bootstrap.servers", bootstrapServers)
    .option(subscribeType, topics)
    .option("partition.assignment.strategy", "range")
    .load()
    .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    .as[(String, String)]
     
     val ctx = messages.writeStream
              //.outputMode("complete")
              .format("console")
              .start()
     ctx.awaitTermination()
              
    /*messages.show()
    messages.printSchema()
    
    val newtable = messages.select("action", "stregth", "style", "target", "weapon")
    
    val ctx = newtable.writeStream
              .outputMode("complete")
              .format("console")
              .start()
              
    ctx.awaitTermination()*/
  }
}