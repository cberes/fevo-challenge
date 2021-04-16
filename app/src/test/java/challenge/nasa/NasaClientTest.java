package challenge.nasa;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import challenge.Jackson;
import challenge.http.HttpClient;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NasaClientTest {
    static class FakeHttpClient implements HttpClient {
        private final Pattern pattern = Pattern.compile("earth_date=(\\d{4}-\\d{2}-\\d{2})");
        private final String apiKey;

        FakeHttpClient(final String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public String get(final String uri) {
            assertThat(uri, containsString(apiKey));
            Matcher matcher = pattern.matcher(uri);
            if (matcher.find()) {
                return readFile(matcher.group(1));
            } else {
                return null;
            }
        }

        private String readFile(final String date) {
            InputStream stream = getClass().getResourceAsStream("/responses/" + date + ".json");
            try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
                return scanner.useDelimiter("\\A").next();
            }
        }
    }

    @Test void getImages() {
        final String apiKey = "TEST_KEY";
        final LocalDate date = LocalDate.of(2016, 4, 2);
        HttpClient http = new FakeHttpClient(apiKey);
        NasaClient nasa = new NasaClient(http, Jackson.jackson(), apiKey);
        List<Image> images = nasa.getImages(date);

        assertEquals(8, images.size());
        images.forEach(img -> assertEquals(date, img.getDate()));
        assertEquals("http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01300/opgs/edr/ncam/NLB_512914365EDR_F0532980NCAM00320M_.JPG",
                images.get(0).getSource());
        assertEquals("http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01300/opgs/edr/ncam/NRB_512908271EDR_F0532980NCAM00223M_.JPG",
                images.get(images.size() - 1).getSource());
    }

    @Test void getImagesWhenEmptyList() {
        final String apiKey = "TEST_KEY";
        HttpClient http = new FakeHttpClient(apiKey);
        NasaClient nasa = new NasaClient(http, Jackson.jackson(), apiKey);
        List<Image> images = nasa.getImages(LocalDate.of(2016, 3, 24));

        assertEquals(0, images.size());
    }
}
