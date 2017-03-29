import java.sql.Timestamp
import scala.collection.mutable.MutableList
import java.sql.DriverManager
import java.sql.Connection
import java.sql.ResultSet
import org.apache.spark.sql.SparkSession

object RemoteHive {
    /*case class StatsRec (
      first_name: String,
      last_name: String,
      action_dtm: Timestamp,
      size: Long,
      size_p: Long,
      size_d: Long
    )*/
  
  case class Employee (
    eid: String,
    name: String,
    salary: String,
    destination: String
  )
    
    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder().appName("RemoteHive").getOrCreate()
        val sc = spark.sparkContext
        import spark.implicits._
      
        val url: String = "jdbc:hive2://localhost:10000"
        val user: String = ""
        val password: String = ""
        
        val conn: Connection = DriverManager.getConnection(url, user, password)
        val res: ResultSet = conn.createStatement.executeQuery("SELECT * FROM sample_employee")
        
        //val fetchedRes = MutableList[StatsRec]()
        val fetchedRes = MutableList[Employee]()
       
        while(res.next()) {
          /*var rec = StatsRec(
               res.getString("first_name"), 
               res.getString("last_name"), 
               Timestamp.valueOf(res.getString("action_dtm")), 
               res.getLong("size"), 
               res.getLong("size_p"), 
               res.getLong("size_d"))*/
          var rec = Employee(
               res.getString("eid"), 
               res.getString("name"), 
               res.getString("salary"), 
               res.getString("destination"))
          fetchedRes += rec
        }
        conn.close()
        
        val rddStatsDelta = sc.parallelize(fetchedRes)
        rddStatsDelta.cache()
        
        println(rddStatsDelta.count)
        rddStatsDelta.collect.take(10).foreach(println) 
    }
}