package priv.robin

import org.apache.spark.sql.SparkSession

import scala.reflect.internal.util.TableDef.Column

object MovieRank {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("Movie Ranking Statistic")
      .getOrCreate()
    val path = "F:\\data\\ml-20m\\ratings.csv"

    val ds = spark.read.format("csv")
      .option("header","true")
      .load(path)
    //    ds.printSchema()
    //使用dataframe的操作比较麻烦，不灵活
    val rank = ds.select("movieId","rating").groupBy("movieId")

    //注册成临时表，方便地使用sql直接进行数据分析
    ds.createOrReplaceTempView("temp")

//    spark.sql("select * from temp").show(10)
    spark.sql("select movieId, max(rating)  from temp group by movieId").show()
    spark.stop()
  }

}
