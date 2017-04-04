package srny.hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Get


object ReadData {
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
      //Read only row1
      val _get = new Get(Bytes.toBytes("001"))
      //To get all the columns in the column-family
      _get.addFamily(Bytes.toBytes("samplecolfamily"))
      //To get specific columns use below
      /*_get.addColumn(Bytes.toBytes("samplecolfamily"), Bytes.toBytes("col1"))
       *_get.addColumn(Bytes.toBytes("samplecolfamily"), Bytes.toBytes("col2"))*/
      val res = myTable.get(_get)
      val value1 = res.getValue(Bytes.toBytes("samplecolfamily"), Bytes.toBytes("col1"))
      val value2 = res.getValue(Bytes.toBytes("samplecolfamily"), Bytes.toBytes("col2"))
      println("col1:" + Bytes.toString(value1) + ", col2:" + Bytes.toString(value2))
    }
    else {
      println(s"$tableName - Table doesn't Exists!")
    }
  }
}