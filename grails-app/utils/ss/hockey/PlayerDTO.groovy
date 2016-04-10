/*
 * Copyright SwerveSoft, Inc. 2010
 */

package ss.hockey

import java.text.NumberFormat

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 *
 * @author tinetti
 */
class PlayerDTO {
  Player player

  List<Long> gameIds = []
  List<Long> gamesWonIds = []
  List<Long> gamesLostIds = []
  List<Long> streaks = []
  Long goalsFor = 0
  Long goalsAgainst = 0
  Long goalsWithShotsFor = 0
  Long goalsWithShotsAgainst = 0
  Long shotsFor = 0
  Long shotsForGames = 0
  Long shotsAgainst = 0
  Long shotsAgainstGames = 0
  Long hitsFor = 0
  Long hitsForGames = 0
  Long hitsAgainst = 0
  Long hitsAgainstGames = 0
  Long timeOnAttackFor = 0
  Long timeOnAttackForGames = 0
  Long timeOnAttackAgainst = 0
  Long timeOnAttackAgainstGames = 0
  Long passingPercentageFor = 0
  Long passingPercentageForGames = 0
  Long passingPercentageAgainst = 0
  Long passingPercentageAgainstGames = 0
  Double ratingFor = 0
  Double ratedGamesFor = 0
  Double ratingAgainst = 0
  Double ratedGamesAgainst = 0
  Double drinksFor = 0
  Double drinksGamesFor = 0
  Double drinksAgainst = 0
  Double drinksGamesAgainst = 0

  Long getId() {
    return player.id
  }

  String getName() {
    return player.name
  }

  Double getWinPercentage() {
    return (gameIds && gamesWonIds ? gamesWonIds.size() / gameIds.size() : 0) as Double
  }

  String getStreakDescription() {
    if (streaks.isEmpty() || (streaks[-1] == 0)) {
      return ''
    }
    Long streak = streaks[-1]

    return streak > 0 ? "W ${streak}" : "L ${streak * -1}"
  }

  Double getGoalsForPerGame() {
    return (gameIds ? goalsFor / gameIds.size() : 0) as Double
  }

  Double getGoalsAgainstPerGame() {
    return (gameIds ? goalsAgainst / gameIds.size() : 0) as Double
  }

  Double getShotsForPerGame() {
    return shotsForGames == 0 ? null : shotsFor / shotsForGames
  }

  Double getShotsAgainstPerGame() {
    return shotsAgainstGames == 0 ? null : shotsAgainst / shotsAgainstGames
  }

  Double getShotPercentageFor() {
    return shotsFor == 0 ? null : goalsWithShotsFor / shotsFor
  }

  Double getShotPercentageAgainst() {
    return shotsAgainst == 0 ? null : goalsWithShotsAgainst / shotsAgainst
  }

  Double getHitsForPerGame() {
    return hitsForGames == 0 ? null : hitsFor / hitsForGames
  }

  Double getHitsAgainstPerGame() {
    return hitsAgainstGames == 0 ? null : hitsAgainst / hitsAgainstGames
  }

  Double getTimeOnAttackForPerGame() {
    return timeOnAttackForGames == 0 ? null : timeOnAttackFor / timeOnAttackForGames
  }

  Double getTimeOnAttackAgainstPerGame() {
    return timeOnAttackAgainstGames == 0 ? null : timeOnAttackAgainst / timeOnAttackAgainstGames
  }

  String getTimeOnAttackForPerGameString() {
    def toaForPerGame = getTimeOnAttackForPerGame()
    return toaForPerGame ? DateUtils.secondsToString(toaForPerGame) : null
  }

  String getTimeOnAttackAgainstPerGameString() {
    def toaAgainstPerGame = getTimeOnAttackAgainstPerGame()
    return toaAgainstPerGame ? DateUtils.secondsToString(toaAgainstPerGame) : null
  }

  Double getPassingPercentageForAverage() {
    return passingPercentageForGames == 0 ? null : (passingPercentageFor / passingPercentageForGames / 100)
  }

  Double getPassingPercentageAgainstAverage() {
    return passingPercentageAgainstGames == 0 ? null : (passingPercentageAgainst / passingPercentageAgainstGames / 100)
  }

  Double getRatingForPerGame() {
    return ratedGamesFor == 0 ? null : ratingFor / ratedGamesFor
  }

  Double getRatingAgainstPerGame() {
    return ratedGamesAgainst == 0 ? null : ratingAgainst / ratedGamesAgainst
  }

  Double getDrinksForPerGame() {
    return drinksGamesFor == 0 ? null : drinksFor / drinksGamesFor
  }

  Double getDrinksAgainstPerGame() {
    return drinksGamesAgainst == 0 ? null : drinksAgainst / drinksGamesAgainst
  }

  Double getBestStreak() {
    Long bestStreak = 0
    streaks.each { streak ->
      bestStreak = Math.max(bestStreak, streak)
    }
    return bestStreak
  }

  String getBestStreakDescription() {
    if (streaks.size() == 0) {
      return "?"
    }

    Double bestStreak = getBestStreak()
    return bestStreak > 0 ? "W ${NumberFormat.getIntegerInstance().format(bestStreak)}" : "?"
  }

  Double getWorstStreak() {
    Double worstStreak = 0;
    streaks.each { streak ->
      worstStreak = Math.min(worstStreak, streak)
    }
    return worstStreak
  }

  Long getCurrentStreak() {
    return streaks.size() == 0 ? 0 : streaks[-1]
  }

  String getWorstStreakDescription() {
    if (streaks.size() == 0) {
      return "?"
    }

    Double worstStreak = getWorstStreak()
    return worstStreak == 0 ? "?" : "L ${NumberFormat.getIntegerInstance().format(worstStreak * -1)}"
  }

  String getAverageStreakDescription() {
    if (streaks.size() == 0) {
      return "?";
    }

    Double sum = 0;
    streaks.each { streak -> sum += streak }
    Double avg = sum / streaks.size()
    return avg > 0 ? "W ${avg}" : "L ${avg * -1}"
  }

  String toString() {
    return "PlayerDTO[${name}: ${gamesWonIds.size()}W-${gamesLostIds.size()}L]"
  }

  void leftShift(GameDTO gameDTO) {
    Boolean home
    TeamStats forStats
    TeamStats againstStats
    if (gameDTO.homePlayerIds.contains(id)) {
      forStats = gameDTO.homeStats
      againstStats = gameDTO.awayStats
      home = true
    } else if (gameDTO.awayPlayerIds.contains(id)) {
      forStats = gameDTO.awayStats
      againstStats = gameDTO.homeStats
      home = false
    } else {
      throw new RuntimeException("got unexpected game for player: ${this} not in ${gameDTO.players}")
    }

    add(gameDTO.game, forStats, againstStats)
  }

  void leftShift(MatchupStatsDTO matchupStats) {
    add(matchupStats.game, matchupStats.forStats, matchupStats.againstStats)
  }

  void add(Game game, TeamStats forStats, TeamStats againstStats) {
    gameIds << game.id

    if (forStats.score > againstStats.score) {
      gamesWonIds << game.id
      if (streaks.size() == 0 || streaks[-1] < 0) {
        streaks << 1
      } else {
        streaks[-1] += 1
      }
    } else {
      gamesLostIds << game.id
      if (streaks.size() == 0 || streaks[-1] > 0) {
        streaks << -1
      } else {
        streaks[-1] -= 1
      }
    }

    goalsFor += forStats.score
    goalsAgainst += againstStats.score

    if (forStats.shots) {
      shotsFor += forStats.shots
      goalsWithShotsFor += forStats.score
      shotsForGames++
    }
    if (forStats.hits) {
      hitsFor += forStats.hits
      hitsForGames++
    }
    if (forStats.timeOnAttack) {
      timeOnAttackFor += forStats.timeOnAttack
      timeOnAttackForGames++
    }
    if (forStats.passingPercentage) {
      passingPercentageFor += forStats.passingPercentage
      passingPercentageForGames++
    }

    if (againstStats.shots) {
      shotsAgainst += againstStats.shots
      goalsWithShotsAgainst += againstStats.score
      shotsAgainstGames++
    }
    if (againstStats.hits) {
      hitsAgainst += againstStats.hits
      hitsAgainstGames++
    }
    if (againstStats.timeOnAttack) {
      timeOnAttackAgainst += againstStats.timeOnAttack
      timeOnAttackAgainstGames++
    }
    if (againstStats.passingPercentage) {
      passingPercentageAgainst += againstStats.passingPercentage
      passingPercentageAgainstGames++
    }

    def forRating = RatingUtils.getRating(forStats, againstStats, game.gameLength)
    if (forRating) {
      ratingFor += forRating
      ratedGamesFor++
    }
    def againstRating = RatingUtils.getRating(againstStats, forStats, game.gameLength)
    if (againstRating) {
      ratingAgainst += againstRating
      ratedGamesAgainst++
    }

    def forDrinks = RatingUtils.getDrinks(forStats, againstStats, game.notes)
    if (forDrinks) {
      drinksFor += forDrinks
      drinksGamesFor++
    }
    def againstDrinks = RatingUtils.getDrinks(againstStats, forStats, game.notes)
    if (againstDrinks) {
      drinksAgainst += againstDrinks
      drinksGamesAgainst++
    }
  }

  @Override
  boolean equals(Object o) {
    if (o == null || !(o instanceof PlayerDTO)) {
      return false
    }
    return this.player == ((PlayerDTO)o).player
  }

  @Override
  public int hashCode() {
    return player.hashCode();
  }
}

