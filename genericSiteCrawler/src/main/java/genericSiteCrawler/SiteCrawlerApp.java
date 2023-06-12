package genericSiteCrawler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class SiteCrawlerApp {
    public static void main(String[] args) {
        String startUrl = "http://top-channel.tv";
        SiteCrawler crawler = new SiteCrawler(startUrl);
        crawler.crawl();

        // Save pages as static files
        savePagesAsFiles(crawler.getVisitedUrls());
    }

    private static void savePagesAsFiles(Set<String> visitedUrls) {
        String rootDirectory = "site-pages";
        try {
            Files.createDirectories(Paths.get(rootDirectory));
        } catch (IOException e) {
            // Handle exception
        }

        for (String url : visitedUrls) {
            try {
                String fileName = generateFileName(url);
                String filePath = rootDirectory + "/" + fileName;
                Path path = Paths.get(filePath);
                String pageContent = fetchPageContent(url);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
                    writer.write(pageContent);
                }
            } catch (IOException e) {
                // Handle exception
            }
        }
    }

    private static String generateFileName(String url) {
        String fileName = url.replaceFirst("^[a-zA-Z]+://", "")
                .replaceAll("[/?#:]+", "_")
                .replaceAll("[\\s.]+$", "")
                .concat(".html");
        return fileName;
    }

    private static String fetchPageContent(String url) throws IOException {
        // Use appropriate HTTP client to fetch page content
        // Here, we'll assume you have an HttpClient utility class
        // that handles HTTP requests and returns the page content as a string.
        HttpClient httpClient = new HttpClient();
        return httpClient.fetchPageContent(url);
    }
}
