import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.FloatType
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DateType


object ReadaCSV {
	def main(args: Array[String]): Unit = {
	  val sparksess = SparkSession.builder().appName("ReadaCSV").getOrCreate()
	  import sparksess.implicits._
	  
	  //Read first ROW as column header
	  val csvDF = sparksess.read.option("header", true).csv(args(0))
	  //Print infered Schema
	  csvDF.printSchema()
	  csvDF.rdd.foreach(println)
	  csvDF.createOrReplaceTempView("tempclickstream")
    sparksess.sql("SELECT * from tempclickstream").show(5)
	  
	  /*val conf = new SparkConf().setAppName("ReadaCSV")
			val sc = new SparkContext(conf)
			val sqlContext = new SQLContext(sc)
			
			val CustomSchema = new StructType(Array(
			    StructField("IP Address", StringType, true),
			    StructField("Website", StringType, true),
			    StructField("Timestamp", StringType, true)
			    ))

			val df = sqlContext.read
			    .format("com.databricks.spark.csv")
					.option("header",true)
					//.option("inferScheme", true)
					.schema(CustomSchema)
					.load(args(0))
					
			//df.rdd.cache()
			df.rdd.foreach(println)
			println("Schema -------> " + df.schema)
			df.createOrReplaceTempView("tempTable")
			
			sqlContext.sql("select * from tempTable").collect().foreach(println)*/
	  

	}
}