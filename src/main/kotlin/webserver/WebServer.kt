package webserver

// write your web framework code here:

fun scheme(url: String): String = url.substringBefore(":")

fun host(url: String): String = url.split("/") [2]

fun path(url: String): String {
  val res = url.substringAfter(host(url))
  return res.substringBefore("?")
}

fun queryParams(url: String): List<Pair<String, String>> {
  val params = (url.substringAfter("?"))
  if (params == url) {
    return listOf()
  } else {
    val pairs = params.split("&").map { s -> s.split("=") }
    return pairs.map { list -> Pair(list[0], list[1]) }
  }
}
// http handlers for a particular website...

fun homePageHandler(request: Request): Response {
  val p = path(request.url)
  if (p == "/") {
    return Response(Status.OK, "This is Imperial.")
  } else if (p == "/computing") {
    return Response(Status.OK, "This is DoC.")
  } else {
    return Response(Status.NOT_FOUND)
  }
}

fun helloHandler (request: Request):Response {
  var name = "World" // default value for name is world
  var upper = false
  for (i in (queryParams(request.url))) {
    if (i.first == "name") {
      name = i.second
    } else {
      upper = i.first == "style" && i.second == "shouting"
    }
  }
  var text = "Hello, $name!"
  return if (upper) {
    Response(Status.OK, text.uppercase())
  } else {
    Response(Status.OK, text)
  }
}

fun restrictedPageHandler (request:Request): Response = Response(Status.OK,"This is very secret.")

fun route (request: Request): Response {
  return if (path(request.url) == "/say-hello") {
    helloHandler(request)
  } else {
    homePageHandler(request)
  }
}


typealias HttpHandler = (Request) -> Response

val funcs = listOf(
  "/" to ::homePageHandler,
  "/computing" to ::homePageHandler,
  "/say-hello" to ::helloHandler,
  "/exam-marks" to requireToken("password1",::restrictedPageHandler))

fun configureRoutes (list: List<Pair<String,HttpHandler>>):HttpHandler{
  return fun(request:Request):Response{
    val path = path(request.url)
    val pair = list.filter {it.first == path}
    return if (pair.isEmpty()) {
      Response(Status.NOT_FOUND)
    } else {
      pair[0].second(request)
    }
  }
}

fun requireToken (token: String, wrapped: HttpHandler): HttpHandler {
  return fun(request:Request):Response {
    val auth = request.authToken
    return if (auth == token) {
      wrapped(request)
    } else {
      Response(Status.FORBIDDEN)
    }
  }
}
