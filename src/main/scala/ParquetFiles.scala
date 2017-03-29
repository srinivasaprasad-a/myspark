import org.apache.spark.sql.SparkSession


object ParquetFiles {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("ParquetFiles").getOrCreate()
    // Encoders for most common types are automatically provided by importing spark.implicits._
    import spark.implicits._
    
    val peopleDF = spark.read.json("examples/src/main/resources/people.json")
    
    // DataFrames can be saved as Parquet files, maintaining the schema information
    peopleDF.write.parquet("people.parquet")
    
    // Read in the parquet file created above
    // Parquet files are self-describing so the schema is preserved
    // The result of loading a Parquet file is also a DataFrame
    val parquetFileDF = spark.read.parquet("people.parquet")
    
    // Parquet files can also be used to create a temporary view and then used in SQL statements
    parquetFileDF.createOrReplaceTempView("parquetFile")
    val namesDF = spark.sql("SELECT name FROM parquetFile WHERE age BETWEEN 13 AND 19")
    namesDF.map(attributes => "Name: " + attributes(0)).show()
    // +------------+
    // |       value|
    // +------------+
    // |Name: Justin|
    // +------------+
  }
}