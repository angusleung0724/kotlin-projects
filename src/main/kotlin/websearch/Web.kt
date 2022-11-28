package websearch
import org.jsoup.nodes.Document

class URL (val url: String) {
  override fun toString() = url
}

class WebPage (val document: Document) {
  fun extractWords(): List<String> {
    val text = document.text().lowercase()
    return text.split(' ', '.',',')
  }
}