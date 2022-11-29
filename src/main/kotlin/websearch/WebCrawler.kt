package websearch

import org.jsoup.UnsupportedMimeTypeException

class WebCrawler (val start: URL) {
  private val limit = 50
  var counter = 0
  var i = 0
  val index = mutableMapOf<URL, WebPage>()
  val visited = mutableSetOf<URL>()
  val queue = mutableListOf<URL>()

  fun run() {
    val page = start.download()
    index[start] = page
    visited.add(start)
    queue += page.extractLinks()
    while (counter <= limit && i <= queue.size) {
      val curr = queue[i]
      if (curr !in visited) {
        try {
          val currpage = curr.download()
          index[curr] = currpage
          queue += currpage.extractLinks().filter{it !in queue}
          counter += 1
        } catch(e: Exception) {
        }
      }
      visited.add(curr)
      i += 1
    }
  }

  fun dump() = index
}

fun main() {
  val crawler = WebCrawler(URL("https://www.youtube.com"))
  crawler.run()
  val searchEngine = SearchEngine(crawler.dump())
  searchEngine.compileIndex()
  println(searchEngine.searchFor("video"))
}
