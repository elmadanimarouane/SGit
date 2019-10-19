package api

import java.util.Calendar

object TimeApi {

  def getDate: String =
    {
      // We get the current date and time
      val calendar = Calendar.getInstance()
      calendar.getTime.toString
    }

}
