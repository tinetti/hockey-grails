package ss.hockey;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

  private static DateFormat DATE_FORMAT = new SimpleDateFormat("m:ss");

  public static Integer stringToSeconds(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }

    String digits = getDigits(value);
    if (digits.length() == 0) {
      return null;
    }

    int iValue = Integer.valueOf(digits).intValue();
    int minutes = iValue / 100;
    int seconds = iValue % 100;
    return minutes * 60 + seconds;
  }

  public static String secondsToString(BigDecimal value) {
    return secondsToString(value == null ? 0 : value.intValue());
  }

  public static String secondsToString(Integer value) {
    if (value != null && value > 0) {
      return DATE_FORMAT.format(new Date(value * 1000));
    }

    return null;
  }

  public static String getDigits(String value) {
    StringBuffer buffer = new StringBuffer();
    for (char c : value.toCharArray()) {
      if (Character.isDigit(c)) {
        buffer.append(c);
      }
    }
    return buffer.toString();
  }

}
