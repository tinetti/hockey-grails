package ss.hockey

class PlayerIdFilter {

  Set<Long> ids = new HashSet<Long>()
  MatchingStrategy matchingStrategy = MatchingStrategy.ANY

  Boolean isEmpty() {
    return ids == null || ids.isEmpty()
  }

  Boolean contains(Long id) {
    return ids && ids.contains(id)
  }

  Boolean matches(Collection<Long> full) {
    if (isEmpty()) {
      return true
    }

    switch (matchingStrategy) {
      case MatchingStrategy.ANY:
        return full.intersect(ids).size() > 0

      case MatchingStrategy.ALL:
        return full.containsAll(ids)

      case MatchingStrategy.ONLY:
        return ids.containsAll(full)

      case MatchingStrategy.EXACT:
        return ids.containsAll(full) && full.containsAll(ids)

      default:
        throw new RuntimeException("unknown matching strategy: ${matchingStrategy}")
    }
  }

  String toString() {
    return "PlayerIdFilter[ids=${ids}, strategy=${matchingStrategy}]"
  }
}
