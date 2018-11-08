package priv.robin

import java.util.UUID


object Test {
  def main(args: Array[String]): Unit = {

    print(UUID.randomUUID().toString.replace("-",""))
  }

}
