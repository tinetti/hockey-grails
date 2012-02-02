package ss.hockey

class MatchupListDTO {

  String description
  List<AggregatedMatchupStatsDTO> matchupStats = []

  String toString() {
    return "MatchupListDTO[${description}:${matchupStats}]"
  }
}
