package ss.hockey;

/**
 * @author tinetti
 */
public enum NhlTeam {

    DUCKS("Anaheim", "Ducks", NhlLeague.NHL),
    BRUINS("Boston", "Bruins", NhlLeague.NHL),
    SABRES("Buffalo", "Sabres", NhlLeague.NHL),
    FLAMES("Calgary", "Flames", NhlLeague.NHL),
    HURRICANES("Carolina", "Hurricanes", NhlLeague.NHL),
    BLACKHAWKS("Chicago", "Blackhawks", NhlLeague.NHL),
    AVALANCHE("Colorado", "Avalanche", NhlLeague.NHL),
    BLUE_JACKETS("Columbus", "Blue Jackets", NhlLeague.NHL),
    STARS("Dallas", "Stars", NhlLeague.NHL),
    RED_WINGS("Detroit", "Red Wings", NhlLeague.NHL),
    OILERS("Edmonton", "Oilers", NhlLeague.NHL),
    PANTHERS("Florida", "Panthers", NhlLeague.NHL),
    KINGS("Los Angeles", "Kings", NhlLeague.NHL),
    WILD("Minnesota", "Wild", NhlLeague.NHL),
    CANADIENS("Montreal", "Canadiens", NhlLeague.NHL),
    PREDATORS("Nashville", "Predators", NhlLeague.NHL),
    DEVILS("New Jersey", "Devils", NhlLeague.NHL),
    ISLANDERS("New York", "Islanders", NhlLeague.NHL),
    RANGERS("New York", "Rangers", NhlLeague.NHL),
    SENATORS("Ottawa", "Senators", NhlLeague.NHL),
    FLYERS("Philadelphia", "Flyers", NhlLeague.NHL),
    COYOTES("Phoenix", "Coyotes", NhlLeague.NHL),
    PENGUINS("Pittsburgh", "Penguins", NhlLeague.NHL),
    SHARKS("San Jose", "Sharks", NhlLeague.NHL),
    BLUES("St Louis", "Blues", NhlLeague.NHL),
    LIGHTNING("Tampa Bay", "Lightning", NhlLeague.NHL),
    MAPLE_LEAFS("Toronto", "Maple Leafs", NhlLeague.NHL),
    CANUCKS("Vancouver", "Canucks", NhlLeague.NHL),
    CAPITALS("Washington", "Capitals", NhlLeague.NHL),
    JETS("Winnipeg", "Jets", NhlLeague.NHL),
    RED_ALL_STARS("Green All Stars", null, NhlLeague.NHL),
    BLUE_ALL_STARS("Black All Stars", null, NhlLeague.NHL),

    AUSTRIA("Austria", null, NhlLeague.NATIONAL),
    BELARUS("Belarus", null, NhlLeague.NATIONAL),
    CANADA("Canada", null, NhlLeague.NATIONAL),
    CZECH_REPUBLIC("Czech Republic", null, NhlLeague.NATIONAL),
    DENMARK("Denmark", null, NhlLeague.NATIONAL),
    FINLAND("Finland", null, NhlLeague.NATIONAL),
    FRANCE("France", null, NhlLeague.NATIONAL),
    GERMANY("Germany", null, NhlLeague.NATIONAL),
    GREAT_BRITAIN("Great Britain", null, NhlLeague.NATIONAL),
    ITALY("Italy", null, NhlLeague.NATIONAL),
    JAPAN("Japan", null, NhlLeague.NATIONAL),
    KAZAKHSTAN("Kazakhstan", null, NhlLeague.NATIONAL),
    LATVIA("Latvia", null, NhlLeague.NATIONAL),
    NORWAY("Norway", null, NhlLeague.NATIONAL),
    POLAND("Poland", null, NhlLeague.NATIONAL),
    RUSSIA("Russia", null, NhlLeague.NATIONAL),
    SLOVAKIA("Slovakia", null, NhlLeague.NATIONAL),
    SWEDEN("Sweden", null, NhlLeague.NATIONAL),
    SWITZERLAND("Switzerland", null, NhlLeague.NATIONAL),
    UKRAINE("Ukraine", null, NhlLeague.NATIONAL),
    UNITED_STATES("United States", null, NhlLeague.NATIONAL),

    OTHER("Other", null, NhlLeague.OTHER);

    private final String city;
    private final String teamName;
    private final NhlLeague league;

    private NhlTeam(String city, String teamName, NhlLeague league) {
        this.city = city;
        this.teamName = teamName;
        this.league = league;
    }

    public String getCity() {
        return city;
    }

    public String getTeamName() {
        return teamName;
    }

    public NhlLeague getLeague() {
        return league;
    }

    public String getFullName() {
        return teamName == null ? city : city + " " + teamName;
    }
    
    public String getFullNameWithRating() {
	  try {
      	Integer rating = NhlTeamRating.getLatest(this).getTotalRating();
      	return getFullName() + " (" + rating + ")";
	  } catch (Exception e) {
		return getFullName() + " (unknown)";
      }
    }

}
