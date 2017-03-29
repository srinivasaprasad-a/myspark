import org.apache.spark.sql.SparkSession
import java.io.File

object ReadfrmHive {
  def main(args: Array[String]): Unit = {
    val warehouseLocation = new File("spark-warehouse").getAbsolutePath
    val spark = SparkSession.builder().appName("ReadfrmHive")
                .config("spark.sql.warehouse.dir", warehouseLocation)
                .enableHiveSupport()
                .getOrCreate()
    import spark.implicits._
    
    val TablesList = spark.sql("SHOW TABLES")
    TablesList.show()
  }
}