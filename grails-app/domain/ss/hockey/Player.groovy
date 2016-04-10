package ss.hockey

class Player {

  static final Long OTHER_PLAYER_ID = 999

  static constraints = {
    name(empty: false, unique: true)
  }

  Long id
  Long version

  String name

  String toString() {
    return name
  }
}
