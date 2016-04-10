package ss.hockey

class GamePlayer {

  static constraints = {
    home(nullable: false)
    player(nullable: false)
  }

  static belongsTo = [game:Game]

  Long id
  Long version

  Player player
  Boolean home

  String toString() {
    return "GamePlayer[${player}]"
  }
}
