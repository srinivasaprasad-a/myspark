package srny.hive

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.SaveMode

object WritetoHive {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("DataFrames").getOrCreate()
    import spark.implicits._
    
    val schema = StructType(Array(StructField("Name", StringType, true), 
                                  StructField("Age", IntegerType, true), 
                                  StructField("Gender", StringType, true), 
                                  StructField("City", StringType, true)))
    val peopleDF = spark.read.schema(schema).csv(args(0))
    //Writing to parquet file
    //peopleDF.filter($"City" === "Bangalore").write.format("parquet").mode(SaveMode.Overwrite).save("BLRpeople.parquet")
    
    //Saving as table in Hive
    peopleDF.filter($"City" === "Bangalore").write.mode(SaveMode.Overwrite).saveAsTable("BLRpeople_Table")
  }
}