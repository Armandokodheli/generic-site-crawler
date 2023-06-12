package genericSiteCrawler;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class SiteCrawler {
    private String startUrl;
    private String domain;
    private Set<String> visitedUrls;

    public SiteCrawler(String startUrl) {
        this.startUrl = startUrl;
        this.domain = getDomain(startUrl);
        this.visitedUrls = new HashSet<>();
    }

    public void crawl() {
        crawlPage(startUrl);
    }

    private void crawlPage(String url) {
        if (visitedUrls.contains(url)) {
            return;
        }

        visitedUrls.add(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    return;
                }

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String htmlContent = EntityUtils.toString(entity);
                    Document document = Jsoup.parse(htmlContent);

                    // Execute custom action on the page
                    executeCustomAction(url, document);

                    Elements links = document.select("a[href]");
                    for (Element link : links) {
                        String href = link.attr("abs:href");
                        if (isValidUrl(href)) {
                            crawlPage(href);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            // Handle exception
        }
    }

    private String getDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            // Handle exception
        }
        return null;
    }

    private boolean isValidUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain != null && domain.equals(this.domain);
    }

    private void executeCustomAction(String url, Document document) {
        // Custom action to be executed on each page
        System.out.println("Visited: " + url);
    }

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }
}
