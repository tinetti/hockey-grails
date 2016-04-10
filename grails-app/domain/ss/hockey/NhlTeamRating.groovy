package ss.hockey

class NhlTeamRating {

  static constraints = {
  }

  static transients = ['totalRating']

  Long id
  Long version

  NhlTeam team
  Date date

  Integer offense
  Integer defense
  Integer goalie

  Integer getTotalRating() {
    return offense + defense + goalie
  }

  String toString() {
    return "NhlTeamRating[${totalRating}:${team.fullName} (${offense}/${defense}/${goalie}) (${date})]"
  }

  static NhlTeamRating getLatest(NhlTeam team) {
    return NhlTeamRating.find("from NhlTeamRating r where r.team=? order by date desc", [team])
  }

}
