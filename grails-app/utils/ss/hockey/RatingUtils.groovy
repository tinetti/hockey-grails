package ss.hockey

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tinetti
 */
class RatingUtils {

  static Double getOffensiveRating(TeamStats stats, TeamStats vsStats) {
    return 0
  }

  static Double getScoreRating(TeamStats stats, TeamStats vsStats, boolean shootout) {
    Double score = stats.score
    Double vsScore = vsStats.score
    if (shootout) {
      if (score > vsScore) {
        score = vsScore + 0.2
      } else {
        vsScore = score + 0.2
      }
    }

    Double offset = (score * 1.5) - vsScore

    return offset * 20
  }

  static Double getHitsRating(TeamStats stats, TeamStats vsStats) {
    if (stats.hits && vsStats.hits) {
      Double offset = (stats.hits * 1.5) - (vsStats.hits)
      return offset / 3
    } else {
      return null
    }
  }

  static Double getTimeOnAttackRating(TeamStats stats, TeamStats vsStats) {
    if (stats.timeOnAttack && vsStats.timeOnAttack) {
      Double offset = (stats.timeOnAttack * 1.5) - vsStats.timeOnAttack
      return offset / 10
    } else {
      return null
    }
  }

  static Double getRating(TeamStats stats, TeamStats vsStats, GameLength length) {
    if (stats.hits && stats.timeOnAttack && stats.shots && vsStats.hits && vsStats.timeOnAttack && vsStats.shots) {
      Double scoreRating = getScoreRating(stats, vsStats, length == GameLength.Shootout)
      Double hitsRating = getHitsRating(stats, vsStats)
      Double timeOnAttackRating = getTimeOnAttackRating(stats, vsStats)

      return scoreRating + hitsRating + timeOnAttackRating
    } else {
      return null
    }
  }

  static Double getDrinks(TeamStats stats, TeamStats vsStats, String notes) {
    if (stats.hits && stats.timeOnAttack && stats.shots && vsStats.hits && vsStats.timeOnAttack && vsStats.shots) {
      // goals
      Double drinks = vsStats.score * 5

      // shot percentage
      Double shotPct = (stats.score as Double) / (stats.shots as Double)
      Double vsShotPct = (vsStats.score as Double) / (vsStats.shots as Double)
      if (shotPct < vsShotPct) {
        if (vsStats.score > 4) {
          drinks += 6
        } else if (vsStats.score > 2) {
          drinks += 4
        } else {
          drinks += 2
        }
      }

      // hits
      if (vsStats.hits > stats.hits) {
        drinks += (vsStats.hits - stats.hits) * 2
      }

      // TOA
      if (vsStats.timeOnAttack > stats.timeOnAttack) {
        Double diff = (vsStats.timeOnAttack as Double) / (stats.timeOnAttack as Double)
        if (diff >= 4) {
          drinks += 25
        } else if (diff >= 2) {
          drinks += 15
        } else {
          drinks += 9
        }
      }

      // let's say 1/4 of shots that didn't score were held by goalie...
      drinks += (vsStats.shots - vsStats.score) / 4D

      // estimate the number of faceoffs
      Double faceoffs = 3 + stats.score + vsStats.score + (stats.shots / 3) + (vsStats.shots / 3)

      drinks += faceoffs * 1.6
      
      // convert from drinks to ounces
      drinks = drinks * 12 / 30

      return drinks
    } else {
      return null
    }
  }
}

