/*
 * Copyright SwerveSoft, Inc. 2010
 */

package ss.hockey

/**
 *
 * @author tinetti
 */
class HockeyUtils {

  static List<Long> stringToListOfLongs(String value) {
    if (value.startsWith('[')) {
      value = value.substring(1)
    }
    if (value.endsWith(']')) {
      value = value.substring(0, value.length() - 1)
    }

    List<Long> longs = []
    value.split(",").each {
      longs.add(Long.valueOf(it.trim()))
    }

    return longs
  }


}

