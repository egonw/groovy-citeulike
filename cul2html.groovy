import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.xml.MarkupBuilder
import static groovyx.net.http.ContentType.JSON
import net.sf.json.JSONSerializer

doMisc = false;

culUrl = "http://www.citeulike.org/";
culAccount = "egonw";

http = new HTTPBuilder(culUrl)

def outputAuthors = { xml, authors ->
  xml.span{
    authors.each { author ->
      span(author)
    }
  }
}

def getYear = { published ->
  list = JSONSerializer.toJava(published)
  return (list != null ? list.get(0) : "");
}

def writer = new StringWriter()
def xml = new MarkupBuilder(writer)

xml.'html'(
  'xmlns':'http://www.w3.org/1999/xhtml',
  'xmlns:bibo':'http://purl.org/ontology/bibo/',
  'xmlns:dcterms':'http://purl.org/dc/terms/'
) {
  http.request(Method.valueOf("GET"), JSON) {
    uri.path = "/json/user/$culAccount/publications"

    response.success = { resp,json ->
      body() { ol() {
        json.each { article ->
          // this is where I should use CSL
          title = article.title
          title = title.replaceAll("\\{","").replaceAll("\\}","")
          if (article.type.equals("BOOK")) {
            li('id':'#'+article.article_id, typeof:'bibo:Article') {
              outputAuthors(xml, article.authors)
              span(property:'bibo:title', article.title)
              span(property:'dcterms:date', getYear(article.published))
            }
          } else if (article.type.equals("JOUR")) {
            li('id':'#'+article.article_id, typeof:'bibo:Book') {
              outputAuthors(xml, article.authors)
              span(property:'bibo:title', article.title)
              span(property:'dcterms:date', getYear(article.published))
            }
          } else if (doMisc) { // MISC
            li('id':'#'+article.article_id, typeof:'bibo:Document') {
              outputAuthors(xml, article.authors)
              span(property:'bibo:title', article.title) + ', '
              span(property:'dcterms:date', article.year)
            }
          }
        }
      }}
    }
  }
}

println writer.toString()