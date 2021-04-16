package challenge;

import java.io.IOException;
import java.time.LocalDate;

import challenge.http.DefaultJavaHttpClient;
import challenge.http.FileCacheHttpClient;
import challenge.http.HttpClient;
import challenge.images.DailyImagesComponent;
import challenge.nasa.NasaClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    public static void main(String[] args) throws IOException {
        final ObjectMapper jackson = Jackson.jackson();
        final HttpClient http = new FileCacheHttpClient(new DefaultJavaHttpClient());
        final NasaClient nasa = new NasaClient(http, jackson, apiKey());
        final DailyImagesComponent output = new DailyImagesComponent(nasa);
        final var data = output.getImages(LocalDate.now(), 10, 3);
        jackson.writeValue(System.out, data);
    }

    private static String apiKey() {
        final String envKey = System.getenv("NASA_API_KEY");
        return envKey == null ? "DEMO_KEY" : envKey;
    }
}
