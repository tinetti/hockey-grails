package ss.hockey

/**
 * SwerveSoft
 * User: tinetti
 * Date: Oct 3, 2010
 * Time: 4:52:59 PM
 */
abstract class RecordGameListDTO {

  String description
  Integer gamesEvaluated = 0
  List<RecordGameDTO> recordGames = []
  def valueStringConverter

  abstract void evaluate(GameDTO game)

  String toString() {
    return "${description}: ${recordGames.size()}"
  }

  void addRecordGame(RecordGameDTO recordGame) {
    gamesEvaluated++

    recordGame.valueStringConverter = valueStringConverter
    recordGames << recordGame
  }

  void sortAndTrim(Integer size) {

    recordGames.sort()

    while (size > 0 && recordGames.size() > size) {
      recordGames.remove(0)
    }

    recordGames = recordGames.reverse()
  }
}


class PropertyDiffRecordGameListDTO extends RecordGameListDTO {
  String property

  void evaluate(GameDTO game) {
    Double v1 = game.homeStats[property]
    Double v2 = game.awayStats[property]
    if (v1 != null && v2 != null) {
      Double diff = Math.abs(v1 - v2)
      Boolean homeTeam = v1 > v2
      addRecordGame(new RecordGameDTO(value: diff, game: game, homeTeam: homeTeam))
    }
  }
}




class PropertyMaxRecordGameListDTO extends RecordGameListDTO {
  String property

  void evaluate(GameDTO game) {
    evaluateValue(game, game.homeStats[property], true)
    evaluateValue(game, game.awayStats[property], false)
  }

  void evaluateValue(game, value, homeTeam) {
    if (value != null) {
      addRecordGame(new RecordGameDTO(value: value, game: game, homeTeam: homeTeam))
    }
  }
}




class PropertyMinRecordGameListDTO extends RecordGameListDTO {
  String property

  void evaluate(GameDTO game) {
    evaluateValue(game, game.homeStats[property], true)
    evaluateValue(game, game.awayStats[property], false)
  }

  void evaluateValue(game, value, homeTeam) {
    if (value != null) {
      addRecordGame(new RecordGameDTO(value: 0D - value, game: game, homeTeam: homeTeam))
    }
  }
}




class PropertyMaxSumRecordGameListDTO extends RecordGameListDTO {
  String property

  void evaluate(GameDTO game) {
    def value1 = game.homeStats[property]
    def value2 = game.awayStats[property]
    if (value1 && value2) {
      addRecordGame(new RecordGameDTO(value:(value1+value2), game:game, homeTeam:true))
    }
  }
}




class RatingMaxRecordGameListDTO extends RecordGameListDTO {
  void evaluate(GameDTO game) {
    Double v1 = RatingUtils.getRating(game.game.homeStats, game.game.awayStats, game.game.gameLength)
    if (v1 != null && v1 > 0) {
      addRecordGame(new RecordGameDTO(value: v1, game: game, homeTeam: true))
    }
    Double v2 = RatingUtils.getRating(game.game.awayStats, game.game.homeStats, game.game.gameLength)
    if (v2 != null && v2 > 0) {
      addRecordGame(new RecordGameDTO(value: v2, game: game, homeTeam: false))
    }
  }
}


class RatingMinRecordGameListDTO extends RecordGameListDTO {
  void evaluate(GameDTO game) {
    Double v1 = RatingUtils.getRating(game.game.homeStats, game.game.awayStats, game.game.gameLength)
    if (v1 != null && v1 < 0) {
      addRecordGame(new RecordGameDTO(value: -v1, game: game, homeTeam: true))
    }
    Double v2 = RatingUtils.getRating(game.game.awayStats, game.game.homeStats, game.game.gameLength)
    if (v2 != null && v2 < 0) {
      addRecordGame(new RecordGameDTO(value: -v2, game: game, homeTeam: false))
    }
  }
}


class RatingDiffRecordGameListDTO extends RecordGameListDTO {
  void evaluate(GameDTO game) {
    Double v1 = RatingUtils.getRating(game.game.homeStats, game.game.awayStats, game.game.gameLength)
    Double v2 = RatingUtils.getRating(game.game.awayStats, game.game.homeStats, game.game.gameLength)
    if (v1 != null && v2 != null) {
      Double diff = Math.abs(v1 - v2)
      Boolean homeTeam = v1 > v2
      addRecordGame(new RecordGameDTO(value: diff, game: game, homeTeam: homeTeam))
    }
  }
}



class DrinksMaxRecordGameListDTO extends RecordGameListDTO {
  void evaluate(GameDTO game) {
    Double v1 = RatingUtils.getDrinks(game.game.homeStats, game.game.awayStats, game.game.notes)
    if (v1 != null) {
      addRecordGame(new RecordGameDTO(value: v1, game: game, homeTeam: true))
    }
    Double v2 = RatingUtils.getDrinks(game.game.awayStats, game.game.homeStats, game.game.notes)
    if (v2 != null) {
      addRecordGame(new RecordGameDTO(value: v2, game: game, homeTeam: false))
    }
  }
}


class DrinksMinRecordGameListDTO extends RecordGameListDTO {
  void evaluate(GameDTO game) {
    Double v1 = RatingUtils.getDrinks(game.game.homeStats, game.game.awayStats, game.game.notes)
    if (v1 != null) {
      addRecordGame(new RecordGameDTO(value: -v1, game: game, homeTeam: true))
    }
    Double v2 = RatingUtils.getDrinks(game.game.awayStats, game.game.homeStats, game.game.notes)
    if (v2 != null) {
      addRecordGame(new RecordGameDTO(value: -v2, game: game, homeTeam: false))
    }
  }
}


class DrinksDiffRecordGameListDTO extends RecordGameListDTO {
  void evaluate(GameDTO game) {
    Double v1 = RatingUtils.getDrinks(game.game.homeStats, game.game.awayStats, game.game.notes)
    Double v2 = RatingUtils.getDrinks(game.game.awayStats, game.game.homeStats, game.game.notes)
    if (v1 != null && v2 != null) {
      Double diff = Math.abs(v1 - v2)
      Boolean homeTeam = v1 > v2
      addRecordGame(new RecordGameDTO(value: diff, game: game, homeTeam: homeTeam))
    }
  }
}


class DrinksMaxSumRecordGameListDTO extends RecordGameListDTO {
  void evaluate(GameDTO game) {
    Double v1 = RatingUtils.getDrinks(game.game.homeStats, game.game.awayStats, game.game.notes)
    Double v2 = RatingUtils.getDrinks(game.game.awayStats, game.game.homeStats, game.game.notes)
    if (v1 != null && v2 != null) {
      Double sum = v1 + v2
      Boolean homeTeam = v1 > v2
      addRecordGame(new RecordGameDTO(value: sum, game: game, homeTeam: homeTeam))
    }
  }
}