package ss.hockey


class NewGameRecordDTO {

  Object value
  Long position
  Long gamesPlayed
  String playersDescription
  String recordGameListDescription

  Double getRank() {
    if (gamesPlayed == 1) {
      // treat as best of 10
      return 0.1D
    } else {
      return (position as Double) / (gamesPlayed as Double)
    }
  }

  String getDescription() {
    def pct = (position as Double) / (gamesPlayed as Double)
    pct = Math.max(pct + 0.005D, 0.01D)
    pct = (pct * 100) as Integer
    return "Top ${pct}% in ${recordGameListDescription}: ${value} (#${position+1}/${gamesPlayed})"
  }
}
