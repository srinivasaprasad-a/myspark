import org.apache.spark._

class WordCount {
  def CountMethod(ip: String, op: String): Unit = {
      val conf = new SparkConf().setAppName("wordCount")
			val sc = new SparkContext(conf)
			// Load our input data.
			val input = sc.textFile(ip)
			// Split it up into words.
			val words = input.flatMap(line => line.split(" "))
			// Transform into pairs and count.
			val counts = words.map(word => (word, 1)).reduceByKey{case (x, y) => x + y}
	    // Save the word count back out to a text file, causing evaluation.
	    counts.saveAsTextFile(op)
  }
}

object WordCount {
  def main(args: Array[String]): Unit = {
     val WC = new WordCount()
     WC.CountMethod(args(0), args(1))
  }
}