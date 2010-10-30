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

println "@prefix cito: <http://purl.org/spar/cito/> ."
println "@prefix tag: <http://www.holygoat.co.uk/owl/redwood/tag/> ."
println "@prefix egonwtag: <http://www.citeulike.org/user/egonw/tag/> ."
println "@prefix egonwart: <http://www.citeulike.org/user/egonw/article/> ."
println "@prefix fabio: <http://purl.org/spar/fabio/> ."
println ""

papers.each { paper ->
  println "# Processing $paper..."
  citotags.each { tag ->
    citation = "$tag--$paper".toLowerCase()
    println "# tag $citation..."
    http.request(Method.valueOf("GET"), JSON) {
      uri.path = "/json/user/egonw/tag/$citation"

      response.success = { resp,json ->
        json.each { article ->
          // println "# article $article"
          tripleCount = 0;
          article.tags.each { artTag ->
            if (artTag.startsWith(tag)) {
              println "egonwart:" + article.article_id + " " + tag.replaceAll("--",":") +
                      " egonwart:$paper ."
              tripleCount++
            } else if (!artTag.startsWith("cito--")) {
              println "egonwart:" + article.article_id + " tag:taggedWithTag egonwtag:$artTag ." 
            }
          }
          if (tripleCount > 0) {
            println "egonwart:" + article.article_id + " fabio:title " + "\"$article.title\" ."
          }
        }
      }
    }
  }
}
