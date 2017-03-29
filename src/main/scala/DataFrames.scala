import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.IntegerType

object DataFrames {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("DataFrames").getOrCreate()
    import spark.implicits._

    //Simple DataFrame creation steps
    /*val sche = StructType(Array(StructField("Key", IntegerType, true), StructField("Value", StringType, true)))
    val sc = spark.sparkContext
    val inputRowRDD = sc.parallelize(1 to 100).map(i => Row(i, s"val_$i"))
    spark.createDataFrame(inputRowRDD, sche).show()*/
   
    //Create sequence of rows using case class
    //case class Employee(ID: Int, Name: String)
    //val data = Seq(Employee(100,"One Hundred"),Employee(200,"Two Hundred"),Employee(300,"Three Hundred"))

    // Create an RDD of Person objects from a CSV file, convert it to a Dataframe
    val schema = StructType(Array(StructField("Name", StringType, true), 
                                  StructField("Age", IntegerType, true), 
                                  StructField("Gender", StringType, true), 
                                  StructField("DeptId", IntegerType, true)))
    val peopleDF = spark.read.schema(schema).csv(args(0))
    // Register the DataFrame as a temporary view
    peopleDF.createOrReplaceTempView("people")
    
     // Create an RDD of Department objects from a CSV file, convert it to a Dataframe
    val Deptschema = StructType(Array(StructField("DeptName", StringType, true), 
                                      StructField("DeptId", IntegerType, true)))
    val deptDF = spark.read.schema(Deptschema).csv(args(0))
    // Register the DataFrame as a temporary view
    deptDF.createOrReplaceTempView("department")
    
    //DataFrame Operations
    peopleDF.select("Name", "Gender").show(5)
    peopleDF.select($"Name", $"Age" + 1).show(5)
    peopleDF.filter($"Age" > 20).show(5)
    peopleDF.groupBy("Age").count().show(5)
    peopleDF.filter($"Gender" === "Male").show(5)
    
    //Multiple Operations to generate RelationalGroupedDataSet
    /*val FinalDF = peopleDF.filter($"Age" > 30)
      .join(deptDF, peopleDF("DeptId") === deptDF("DeptId"))
      .groupBy(deptDF("DeptId"), peopleDF("gender"))*/
    
    //OR
    val FinalDF = spark.sql("SELECT p.Name, p.Age, p.Gender, d.DeptName FROM people p, department d WHERE p.DeptId = d.DeptId GROUP BY p.Name, p.Age, p.Gender, d.DeptName")
    FinalDF.show(5)
  }
}