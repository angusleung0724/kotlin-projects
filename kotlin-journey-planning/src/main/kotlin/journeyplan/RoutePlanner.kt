package journeyplan

class SubwayMap(val segments: List<Segment>) {
  fun routesFrom(
    origin: Station,
    destination: Station,
    optimisingFor: (Route) -> Int = { it.duration() }
  ): List<Route> {
    if (!origin.closed && !destination.closed) {
      return helper(
        origin,
        destination,
        mutableSetOf<Station>(origin),
        mutableListOf<Segment>(),
        segments.filter { !it.line.suspended }.toMutableList(),
        mutableListOf<Route>()
      ).sortedBy(optimisingFor)
    } else {
      return emptyList()
    }
  }

  fun helper(
    current: Station,
    end: Station,
    visited: MutableSet<Station>,
    acc: MutableList<Segment>,
    newsegments: MutableList<Segment>,
    result: MutableList<Route>
  ): List<Route> {
    if (current == end) {
      result.add(Route(acc.toList()))
    } else {
      var next = newsegments.filter { it.start == current }
      if (current.closed) {
        next = next.filter { it.line == acc.lastOrNull()?.line }
      }
      for (n in next) {
        if (n.end !in visited) {
          visited.add(n.end)
          acc.add(n)
          helper(n.end, end, visited, acc, newsegments, result)
          visited.remove(n.end)
          acc.remove(n)
        }
      }
    }
    return result
  }
}

class Route(val segments: List<Segment>) {
  fun duration() = segments.sumOf { s -> s.avg }

  fun numChanges(): Int {
    var count = 0
    for ((seg1, seg2) in (segments zip segments.drop(1))) {
      if (seg1.line != seg2.line) {
        count += 1
      }
    }
    return count
  }

  override fun toString(): String {
    val result = StringBuilder()
    result.append("${segments.first().start} to ${segments.last().end} - ${duration()} minutes, ${numChanges()} changes")
    var x = 0
    var curr = segments.first().start
    for (x in 0..segments.size - 1) {
      if (x == segments.size - 1 || segments[x].line != segments[x + 1].line) {
        result.append("\n - $curr to ${segments[x].end} by ${segments[x].line}")
        curr = segments[x].end
      }
    }
    return result.toString()
  }
}
