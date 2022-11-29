package websearch

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

data class URL(val url: String) {

  override fun toString() = url
  fun download(): WebPage {
    val downloadedDocument = Jsoup.connect(url).get()
    return WebPage(downloadedDocument)
  }

}

class WebPage(val document: Document) {
  fun extractWords(): List<String> {
    val text = document.text().lowercase()
    return text.replace(",", "").replace(".", "").split(" ")
  }

  fun extractLinks(): List<URL> {
    val result = mutableListOf<URL>()
    val visited = mutableSetOf<URL>()
    val aTags: Elements = document.getElementsByTag("a")
    for (link in aTags) {
      val firstTag = link.attr("href")
      if (firstTag.startsWith("https://") || firstTag.startsWith("http://")) {
        val curr = URL(firstTag)
        if (curr !in visited) {
          result.add(curr)
          visited.add(curr)
        }
      }
    }
    return result
  }
}
