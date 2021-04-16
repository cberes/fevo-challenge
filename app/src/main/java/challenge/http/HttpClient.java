package challenge.http;

import java.io.IOException;

public interface HttpClient {
    String get(String uri) throws IOException, InterruptedException;
}
