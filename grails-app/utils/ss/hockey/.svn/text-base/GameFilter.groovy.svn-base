/*
 * Copyright SwerveSoft, Inc. 2010
 */

package ss.hockey

/**
 *
 * @author tinetti
 */
class GameFilter {

  Integer offset
  Integer max

  GameVersion gameVersion

  List<Long> gameIds

  Date fromDate
  Date toDate

  PlayerIdFilter gamePlayerIdFilter
  PlayerIdFilter team1PlayerIdFilter
  PlayerIdFilter team2PlayerIdFilter
  PlayerIdFilter excludePlayerIdFilter
  
  Boolean home

  Long maxGamesPerPlayer

  String toString() {
    StringBuffer buf = new StringBuffer()
    buf.append("GameFilter[gameVersion:${gameVersion}")

    if (gameIds) {
      buf.append(",gameIds:${gameIds}")
    }
    if (fromDate) {
      buf.append(",fromDate:${fromDate}")
    }
    if (toDate) {
      buf.append(",toDate:${toDate}")
    }
    if (gamePlayerIdFilter) {
      buf.append(",gamePlayerIdFilter:${gamePlayerIdFilter}")
    }
    if (team1PlayerIdFilter) {
        buf.append(",team1PlayerIdFilter:${team1PlayerIdFilter}")
    }
    if (team2PlayerIdFilter) {
        buf.append(",team2PlayerIdFilter:${team2PlayerIdFilter}")
    }
    if (excludePlayerIdFilter) {
        buf.append(",excludePlayerIdFilter:${excludePlayerIdFilter}")
    }
    if (maxGamesPerPlayer) {
      buf.append(",maxGamesPerPlayer:${maxGamesPerPlayer}")
    }
    if (home != null) {
      buf.append(",home:${home}")
    }
    if (offset) {
      buf.append(",offset:${offset}")
    }
    if (max) {
      buf.append(",max:${max}")
    }

    buf.append "]"
    return buf.toString()
  }
}
