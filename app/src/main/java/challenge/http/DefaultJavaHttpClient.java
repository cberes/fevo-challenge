package challenge.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class DefaultJavaHttpClient implements HttpClient {
    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 20;
    private static final int DEFAULT_READ_TIMEOUT_SECONDS = 120;

    private final int readTimeoutSeconds;
    private final java.net.http.HttpClient client;

    public DefaultJavaHttpClient() {
        this(DEFAULT_CONNECT_TIMEOUT_SECONDS, DEFAULT_READ_TIMEOUT_SECONDS);
    }

    public DefaultJavaHttpClient(final int connectTimeoutSeconds, final int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
        client = java.net.http.HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                .build();
    }

    @Override
    public String get(final String uri) throws IOException, InterruptedException {
        final HttpRequest request = makeRequest(uri);
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        errorIfBadStatus(response);
        return response.body();
    }

    private HttpRequest makeRequest(final String uri) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(readTimeoutSeconds))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private static void errorIfBadStatus(final HttpResponse<String> response) throws IOException {
        if (response.statusCode() != 200) {
            throw new IOException("Unexpected status: " + response.statusCode());
        }
    }
}
