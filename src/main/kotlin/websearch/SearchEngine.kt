package websearch

class SearchEngine (val corpus: Map<URL,WebPage>) {
  var index = mutableMapOf<String, List<SearchResult>>()

  fun compileIndex() {
    val pairs = mutableListOf<Pair<String, URL>>()
    corpus.forEach { entry -> pairs += allwords(entry.key, entry.value.extractWords()) }
    val indexMap = pairs.groupBy({ it.first }, { it.second })
    for (entries in indexMap) {
      index.put(entries.key, rank(entries.value))
    }
  }

  private fun allwords(url: URL, text: List<String>): MutableList<Pair<String, URL>> {
    var pair = mutableListOf<Pair<String, URL>>()
    for (word in text) {
      pair += Pair(word, url)
    }
    return pair
  }

  fun rank(list: List<URL>): List<SearchResult> {
    val results = mutableListOf<SearchResult>()
    for (n in list) {
      results.add(SearchResult(n, list.count { it == n }))
    }
    return results.sortedByDescending { it.numRefs }
  }

  fun searchFor(string: String): SearchResultsSummary {
    return SearchResultsSummary(string, index[string]!!)
  }
}

class SearchResult(val url: URL, val numRefs: Int) {
}

class SearchResultsSummary(val query: String, val results: List<SearchResult>) {
  override fun toString(): String {
    val s = StringBuilder()
    s.append("Results for \"$query\"")
    for (res in results) {
      s.append("${res.url} - ${res.numRefs} references")
    }
    return s.toString()
  }
}
