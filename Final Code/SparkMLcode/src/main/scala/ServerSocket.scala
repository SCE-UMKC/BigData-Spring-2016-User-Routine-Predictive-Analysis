import java.io.PrintStream
import java.net.{ServerSocket, InetAddress}

import scala.io.BufferedSource

/**
  * Created by Rakesh on 4/30/2016.
  */
object ServerSocket {
  def main(args: Array[String]) {
    println(InetAddress.getLocalHost.getHostAddress)
    val server = new ServerSocket(9999)
    while (true) {
      val s = server.accept()
      val in = new BufferedSource(s.getInputStream()).getLines()
      val out = new PrintStream(s.getOutputStream())
      val data=NaiveBayesClassfy.classifyAcc(in.next())
      println(data)
      println(data.toString)
      var x="Your current Activity is Sitting"
      if(data.toString.equals("1.0")){
      x=" Your Current Activity is Walking"
      }
      else if(data.toString.equals("2.0")){
        x=" Your Current Activity is Sitting"
      }
      else if(data.toString.equals("3.0")||data.toString.equals("6.0")){
        x=" Your Current Activity is Sitting"
      }
      else if(data.toString.equals("4.0")){
        x=" Your Current Activity is Stairs Climbing down"
      }
      else if(data.toString.equals("5.0")){
        x=" Your Current Activity is Stairs Climbing up"
      }
    x=  x+"\n No Recommendation is Available for this "


      x=x+"\n Your next activity is        "
      out.println(x)
      out.flush()
      s.close()
    }
  }
}
