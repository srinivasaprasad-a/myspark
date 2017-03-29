import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.client.HBaseAdmin


object HBaseSample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("HBaseSample").getOrCreate()
    val sc = spark.sparkContext
    val hbaseConfig = new HBaseConfiguration()
    hbaseConfig.set("hbase.master", "localhost:60001")
    val hbaseAdmin = new HBaseAdmin(hbaseConfig)
    if (hbaseAdmin.tableExists("sampletable"))
      println("Table Exists!")
    else
      println("No Table!")
  }
}