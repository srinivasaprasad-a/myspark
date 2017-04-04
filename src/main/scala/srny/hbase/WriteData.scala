package srny.hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Get


object WriteData {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
                .master("local")
                .appName("HBaseSample")
                .getOrCreate()
    val sc = spark.sparkContext
    val hbaseConfig = new HBaseConfiguration()
    hbaseConfig.set("hbase.master", "localhost:60001")
    val hbaseAdmin = new HBaseAdmin(hbaseConfig)
    
    val tableName = "sampletable"
    
    if (hbaseAdmin.tableExists(tableName)) {
      println(s"$tableName - Table Exists!")

      val myTable = new HTable(hbaseConfig, tableName)
      val _put = new Put(Bytes.toBytes("002"))
      _put.add(Bytes.toBytes("samplecolfamily"), Bytes.toBytes("col1"), Bytes.toBytes("XYZ"))
      _put.add(Bytes.toBytes("samplecolfamily"), Bytes.toBytes("col2"), Bytes.toBytes("890"))
      myTable.put(_put)
      println("Data Inserted")
    }
    else {
      println(s"$tableName - Table doesn't Exists!")
    }
  }
}