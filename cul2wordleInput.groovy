import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import static groovyx.net.http.ContentType.JSON

culUrl = "http://www.citeulike.org/";

citotags = [
  "cito--cites",
  "cito--usesMethodIn",
  "cito--discusses",
  "cito--extends" // there are more, but these are all I use right now
]

papers = [
  "1073448",
  "423382"
]

http = new HTTPBuilder(culUrl)

papers.each { paper ->
  println "# Processing $paper..."
  citotags.each { tag ->
    sleep(2000)
    citation = "$tag--$paper".toLowerCase()
    http.request(Method.valueOf("GET"), JSON) {
      uri.path = "/json/user/egonw/tag/$citation"

      response.success = { resp,json ->
        json.each { article ->
          tripleCount = 0;
          article.tags.each { artTag ->
            if (artTag.startsWith(tag)) tripleCount++
          }
          if (tripleCount > 0) {
            title = article.title
            title = title.replaceAll("\\{","").replaceAll("\\}","")
            println "$title"
          }
        }
      }
    }
  }
  sleep(5000)
}
