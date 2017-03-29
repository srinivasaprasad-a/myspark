import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.DateType
import org.apache.spark.sql.types.IntegerType


object NetworkUsage {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("NetworkUsage").getOrCreate()
    val schema = StructType(Array(StructField("Date",DateType,true),
        StructField("Local_IP",IntegerType,true),
        StructField("Remote_ASN",IntegerType,true),
        StructField("Count_Connc",IntegerType,true)
        ))
    val inputDF = spark.read.option("header", true).schema(schema).csv(args(0))
    inputDF.createOrReplaceTempView("NU_inputDF")
    val tempDF = spark.sql("SELECT MONTH(Date) AS Month, DAY(Date) AS Day, CONCAT(Local_IP, ' ', Remote_ASN) AS LIP_RASN, COUNT(*) from NU_inputDF where Count_Connc > 50 GROUP BY MONTH(Date), DAY(Date), CONCAT(Local_IP, ' ', Remote_ASN)")
     tempDF.count()
     tempDF.show()
  }
}