package challenge.nasa;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import challenge.http.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NasaClient {
    private final HttpClient client;
    private final ObjectMapper jackson;
    private final String apiKey;

    public NasaClient(final HttpClient client, final ObjectMapper jackson, final String apiKey) {
        this.client = client;
        this.jackson = jackson;
        this.apiKey = apiKey;
    }

    public List<Image> getImages(final LocalDate date) throws NasaException {
        try {
            return getImagesUnhandled(date);
        } catch (IOException | InterruptedException e) {
            throw new NasaException("Failed to get images for date=" + date, e);
        }
    }

    private List<Image> getImagesUnhandled(final LocalDate date) throws IOException, InterruptedException {
        String uri = String.format("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?earth_date=%s&camera=NAVCAM&api_key=%s", date, apiKey);
        final String json = client.get(uri);
        return parseResponse(json);
    }

    private List<Image> parseResponse(final String json) throws JsonProcessingException {
        final List<Image> images = new LinkedList<>();
        JsonNode root = jackson.readTree(json);
        JsonNode photos = root.get("photos");
        for (JsonNode photo : photos) {
            images.add(parseImage(photo));
        }
        return images;
    }

    private static Image parseImage(final JsonNode photo) {
        final String imageSource = photo.get("img_src").asText();
        final String date = photo.get("earth_date").asText();
        return new Image(imageSource, LocalDate.parse(date));
    }
}
