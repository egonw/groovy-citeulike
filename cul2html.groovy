import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import static groovyx.net.http.ContentType.JSON

culUrl = "http://www.citeulike.org/";

papers = [
  "citespaper-cdkii":"egonwart:1073448",
  "citespaper-cdki":"egonwart:423382"
]

http = new HTTPBuilder(culUrl)

map = [
  "cito-usesmethodin":"cito:usesMethodIn",
  "cito-cites":"cito:cites"
]

println "@prefix cito: <http://purl.org/spar/cito/> ."
println "@prefix tag: <http://www.holygoat.co.uk/owl/redwood/tag/> ."
println "@prefix egonwtag: <http://www.citeulike.org/user/egonw/tag/> ."
println "@prefix egonwart: <http://www.citeulike.org/user/egonw/article/> ."
println "@prefix fabio: <http://purl.org/spar/fabio/> ."
println ""

papers.keySet().each { paper ->
  println "# Processing $paper..."
  http.request(Method.valueOf("GET"), JSON) {
    uri.path = "/json/user/egonw/tag/$paper"

    response.success = { resp,json ->
      json.each { article ->
        tripleCount = 0;
        article.tags.each { artTag ->
          if (map.get(artTag) != null) {
            println "egonwart:" + article.article_id + " " + map.get(artTag) +
                    " " + papers.get(paper) + " ."
            tripleCount++
          } else if (!artTag.startsWith("citespaper")) {
            println "egonwart:" + article.article_id + " tag:taggedWithTag egonwtag:$artTag ." 
          }
        }
        if (tripleCount > 0) {
          println "egonwart:" + article.article_id + " fabio:title " + "\"$article.title\" ."
          // println "egonwart:" + article.article_id + "> cito:cites " +
          //         " " + papers.get(paper) + ""
        }
      }
    }
  }
}
