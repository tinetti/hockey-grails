package ss.hockey

/**
 * Created by IntelliJ IDEA.
 * User: tinetti
 * Date: Sep 4, 2010
 * Time: 1:05:40 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class MatchupTypeDTO {

  String description
  GameFilter gameFilter

  abstract Long getWeight()

  abstract boolean matches(MatchupStatsDTO matchupStats)

  String toString() {
    return "MatchupType[${description}]"
  }
}


class AllMatchupTypeDTO extends MatchupTypeDTO {

  boolean matches(MatchupStatsDTO matchupStats) {
    return true
  }

  Long getWeight() {
    return 0
  }
}

class TeamSizeMatchupTypeDTO extends MatchupTypeDTO {
  int forTeamSize
  int againstTeamSize

  boolean matches(MatchupStatsDTO matchupStats) {
    return matchupStats.forPlayers.size() == forTeamSize && matchupStats.againstPlayers.size() == againstTeamSize
  }

  String getDescription() {
    return "${forTeamSize}-on-${againstTeamSize}"
  }

  Long getWeight() {
    Double count1 = forTeamSize + againstTeamSize
    Double count2 = 0
    if (gameFilter?.gamePlayerIdFilter?.ids) {
      count2 += gameFilter.team1PlayerIdFilter.ids.size()
    }
    if (gameFilter?.team1PlayerIdFilter?.ids) {
      count2 += gameFilter.team1PlayerIdFilter.ids.size()
    }
    if (gameFilter?.team2PlayerIdFilter?.ids) {
      count2 += gameFilter.team2PlayerIdFilter.ids.size()
    }
    if (count2 == 0) {
      count2 = count1
    }

    Double weight
    if (count1 == count2) {
      weight = 1
    } else if (count1 < count2) {
      weight = count1 / count2
    } else {
      weight = count2 / count1
    }

    weight *= 100

    return (Long)(weight * weight)
  }
  
  String toString() {
    return "TeamSizeMatchupTypeDTO[forTeamSize:${forTeamSize},againstTeamSize:${againstTeamSize}"
  }
}

class HomeAwayMatchupTypeDTO extends MatchupTypeDTO {
  boolean home
  Set<Long> playerIds
  
  String getDescription() {
    return home ? "Home" : "Away"
  }
  
  boolean matches(MatchupStatsDTO matchupStats) {
    Set<Long> gamePlayerIds = home ? matchupStats.game.homePlayerIds : matchupStats.game.awayPlayerIds
    return gamePlayerIds.containsAll(playerIds)
  }
  
  Long getWeight() {
    return 1
  }
}