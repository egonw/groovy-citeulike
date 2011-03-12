import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.xml.MarkupBuilder
import static groovyx.net.http.ContentType.JSON

// culUrl = "http://www.citeulike.org/";
culUrl = "file:///home/egonw/";
culAccount = "egonw";

http = new HTTPBuilder(culUrl)

def outputAuthors = { xml, authors ->
  xml.span{
    authors.each { author ->
      xml.span(author) + ', ' 
    }
  }
}

def writer = new StringWriter()
def xml = new MarkupBuilder(writer)

xml.'html'(
  'xmlns':'http://www.w3.org/1999/xhtml',
  'xmlns:bibo':'http://purl.org/ontology/bibo/',
  'xmlns:dcterms':'http://purl.org/dc/terms/'
) {
  http.request(Method.valueOf("GET"), JSON) {
    // uri.path = "/json/user/$culAccount/publications"
    uri.path = "/publications"

    response.success = { resp,json ->
      xml.body() { xml.ol() {
        json.each { article ->
          // this is where I should use CSL
          title = article.title
          title = title.replaceAll("\\{","").replaceAll("\\}","")
          if (article.type.equals("BOOK")) {
            xml.li('id':'#'+article.article_id, typeof:'bibo:Article') {
              outputAuthors(xml, article.authors)
              xml.span(property:'bibo:title', article.title) + ', '
              xml.span(property:'dcterms:created', article.year)
            }
          } else if (article.type.equals("JOUR")) {
            xml.li('id':'#'+article.article_id, typeof:'bibo:Book') {
              outputAuthors(xml, article.authors)
              xml.span(property:'bibo:title', article.title) + ', '
              xml.span(property:'dcterms:created', article.year)
            }
          } else { // MISC
            xml.li('id':'#'+article.article_id, typeof:'bibo:Document') {
              outputAuthors(xml, article.authors)
              xml.span(property:'bibo:title', article.title) + ', '
              xml.span(property:'dcterms:created', article.year)
            }
          }
        }
      }}
    }
  }
}

println writer.toString()