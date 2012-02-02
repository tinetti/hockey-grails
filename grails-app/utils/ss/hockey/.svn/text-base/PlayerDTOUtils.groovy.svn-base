/*
 * Copyright SwerveSoft, Inc. 2010
 */

package ss.hockey

/**
 *
 * @author tinetti
 */
class PlayerDTOUtils {

  static PlayerRecordsDTO getPlayerStatsRecordsHigh(List<PlayerDTO> playerDTOs) {
    PlayerRecordsDTO recordsDTO = new PlayerRecordsDTO(gamesLost: 99999, ratingAgainstPerGame: 99999, drinksForPerGame: 99999, goalsAgainst: 99999, goalsAgainstPerGame: 99999, shotsAgainstPerGame: 99999, shotPercentageAgainst: 99999, hitsAgainstPerGame: 99999, timeOnAttackAgainstPerGame: 99999)

    if (playerDTOs?.size() > 0) {
      playerDTOs.each { playerDTO ->
        recordsDTO.gamesWon = Math.max(recordsDTO.gamesWon, playerDTO.gamesWonIds.size())
        recordsDTO.gamesLost = Math.min(recordsDTO.gamesLost, playerDTO.gamesLostIds.size())
        recordsDTO.winPercentage = Math.max(recordsDTO.winPercentage, playerDTO.winPercentage)
        recordsDTO.streak = Math.max(recordsDTO.streak, playerDTO.bestStreak)
        recordsDTO.ratingForPerGame = Math.max(recordsDTO.ratingForPerGame, playerDTO.ratingForPerGame ?: 0)
        recordsDTO.ratingAgainstPerGame = Math.min(recordsDTO.ratingAgainstPerGame, playerDTO.ratingAgainstPerGame ?: 99999D)
        recordsDTO.drinksForPerGame = Math.min(recordsDTO.drinksForPerGame, playerDTO.drinksForPerGame ?: 99999D)
        recordsDTO.drinksAgainstPerGame = Math.max(recordsDTO.drinksAgainstPerGame, playerDTO.drinksAgainstPerGame ?: 0)
        recordsDTO.goalsFor = Math.max(recordsDTO.goalsFor, playerDTO.goalsFor)
        recordsDTO.goalsAgainst = Math.min(recordsDTO.goalsAgainst, playerDTO.goalsAgainst)
        recordsDTO.goalsForPerGame = Math.max(recordsDTO.goalsForPerGame, playerDTO.goalsForPerGame)
        recordsDTO.goalsAgainstPerGame = Math.min(recordsDTO.goalsAgainstPerGame, playerDTO.goalsAgainstPerGame)
        recordsDTO.shotsForPerGame = Math.max(recordsDTO.shotsForPerGame, playerDTO.shotsForPerGame ?: 0)
        recordsDTO.shotsAgainstPerGame = Math.min(recordsDTO.shotsAgainstPerGame, playerDTO.shotsAgainstPerGame ?: 99999)
        recordsDTO.shotPercentageFor = Math.max(recordsDTO.shotPercentageFor, playerDTO.shotPercentageFor ?: 0)
        recordsDTO.shotPercentageAgainst = Math.min(recordsDTO.shotPercentageAgainst, playerDTO.shotPercentageAgainst ?: 99999)
        recordsDTO.hitsForPerGame = Math.max(recordsDTO.hitsForPerGame, playerDTO.hitsForPerGame ?: 0)
        recordsDTO.hitsAgainstPerGame = Math.min(recordsDTO.hitsAgainstPerGame, playerDTO.hitsAgainstPerGame ?: 99999)
        recordsDTO.timeOnAttackForPerGame = Math.max(recordsDTO.timeOnAttackForPerGame, playerDTO.timeOnAttackForPerGame ?: 0)
        recordsDTO.timeOnAttackAgainstPerGame = Math.min(recordsDTO.timeOnAttackAgainstPerGame, playerDTO.timeOnAttackAgainstPerGame ?: 99999)
      }
    }

    return recordsDTO
  }

  static PlayerRecordsDTO getPlayerStatsRecordsLow(List<PlayerDTO> playerDTOs) {
    PlayerRecordsDTO recordsDTO = new PlayerRecordsDTO(gamesWon: 99999, winPercentage: 99999, streak: 99999, ratingForPerGame: 99999, drinksAgainstPerGame: 99999, goalsFor: 99999, goalsForPerGame: 99999, shotsForPerGame: 99999, shotPercentageFor: 99999, hitsForPerGame: 99999, timeOnAttackForPerGame: 99999)

    if (playerDTOs?.size() > 0) {
      playerDTOs.each { playerDTO ->
        recordsDTO.gamesWon = Math.min(recordsDTO.gamesWon, playerDTO.gamesWonIds.size())
        recordsDTO.gamesLost = Math.max(recordsDTO.gamesLost, playerDTO.gamesLostIds.size())
        recordsDTO.winPercentage = Math.min(recordsDTO.winPercentage, playerDTO.winPercentage)
        recordsDTO.streak = Math.min(recordsDTO.streak, playerDTO.worstStreak)
        recordsDTO.ratingForPerGame = Math.min(recordsDTO.ratingForPerGame, playerDTO.ratingForPerGame ?: 99999D)
        recordsDTO.ratingAgainstPerGame = Math.max(recordsDTO.ratingAgainstPerGame, playerDTO.ratingAgainstPerGame ?: 0D)
        recordsDTO.drinksForPerGame = Math.max(recordsDTO.drinksForPerGame, playerDTO.drinksForPerGame ?: 0D)
        recordsDTO.drinksAgainstPerGame = Math.min(recordsDTO.drinksAgainstPerGame, playerDTO.drinksAgainstPerGame ?: 99999D)
        recordsDTO.goalsFor = Math.min(recordsDTO.goalsFor, playerDTO.goalsFor)
        recordsDTO.goalsAgainst = Math.max(recordsDTO.goalsAgainst, playerDTO.goalsAgainst)
        recordsDTO.goalsForPerGame = Math.min(recordsDTO.goalsForPerGame, playerDTO.goalsForPerGame)
        recordsDTO.goalsAgainstPerGame = Math.max(recordsDTO.goalsAgainstPerGame, playerDTO.goalsAgainstPerGame)
        recordsDTO.shotsForPerGame = Math.min(recordsDTO.shotsForPerGame, playerDTO.shotsForPerGame ?: 99999D)
        recordsDTO.shotsAgainstPerGame = Math.max(recordsDTO.shotsAgainstPerGame, playerDTO.shotsAgainstPerGame ?: 0D)
        recordsDTO.shotPercentageFor = Math.min(recordsDTO.shotPercentageFor, playerDTO.shotPercentageFor ?: 99999D)
        recordsDTO.shotPercentageAgainst = Math.max(recordsDTO.shotPercentageAgainst, playerDTO.shotPercentageAgainst ?: 0D)
        recordsDTO.hitsForPerGame = Math.min(recordsDTO.hitsForPerGame, playerDTO.hitsForPerGame ?: 99999D)
        recordsDTO.hitsAgainstPerGame = Math.max(recordsDTO.hitsAgainstPerGame, playerDTO.hitsAgainstPerGame ?: 0D)
        recordsDTO.timeOnAttackForPerGame = Math.min(recordsDTO.timeOnAttackForPerGame, playerDTO.timeOnAttackForPerGame ?: 99999D)
        recordsDTO.timeOnAttackAgainstPerGame = Math.max(recordsDTO.timeOnAttackAgainstPerGame, playerDTO.timeOnAttackAgainstPerGame ?: 0D)
      }
    }

    return recordsDTO
  }

  static void populatePlayerStats(List<PlayerDTO> playerDTOs, List<GameDTO> gameDTOs) {
  }
}
