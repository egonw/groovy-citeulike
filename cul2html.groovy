import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.xml.MarkupBuilder
import static groovyx.net.http.ContentType.JSON

culUrl = "http://www.citeulike.org/";
culAccount = "egonw";

http = new HTTPBuilder(culUrl)

def writer = new StringWriter()
def xml = new MarkupBuilder(writer)
xml.'html'(
  'xmlns':'http://www.w3.org/1999/xhtml',
  'xmlns:bibo':'http://purl.org/ontology/bibo/'
) {
  http.request(Method.valueOf("GET"), JSON) {
    uri.path = "/json/user/$culAccount/publications"

    response.success = { resp,json ->
      xml.body() {
        json.each { article ->
          xml.div('id':'#'+article.article_id, typeof:'bibo:Article') {
            // this is where I should use CSL
            title = article.title
            title = title.replaceAll("\\{","").replaceAll("\\}","")
            if (article.type.equals("BOOK")) {
              xml.span(property:'bibo:title', article.title)
            } else if (article.type.equals("JOUR")) {
              xml.span(property:'bibo:title', article.title)
            } else { // MISC
              xml.span(property:'bibo:title', article.title)
            }
          }
        }
      }
    }
  }
}

println writer.toString()