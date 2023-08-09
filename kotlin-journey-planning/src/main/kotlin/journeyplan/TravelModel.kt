package journeyplan

class Station(val station: String) {
  var closed = false
  fun close() {
    closed = true
  }

  fun open() {
    closed = false
  }

  override fun toString(): String = station
}

class Line(val line: String) {
  var suspended = false
  fun suspend() {
    suspended = true
  }

  fun resume() {
    suspended = false
  }

  override fun toString(): String = "$line Line"
}

class Segment(val start: Station, val end: Station, val line: Line, val avg: Int)
