package challenge.http;

import java.nio.file.Files;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileCacheHttpClientTest {
    static class FakeHttpClient implements HttpClient {
        private String response;
        private int timesCalled;

        @Override
        public String get(final String uri) {
            ++timesCalled;
            return response;
        }

        void setResponse(final String response) {
            this.response = response;
        }

        int getTimesCalled() {
            return timesCalled;
        }
    }

    @Test void caches() throws Exception {
        FakeHttpClient fake = new FakeHttpClient();
        FileCacheHttpClient cache = new FileCacheHttpClient(
                fake, Files.createTempDirectory("test-http-file-cache").toFile(), 1, ChronoUnit.SECONDS);

        fake.setResponse("first value");
        assertEquals("first value", cache.get("a"));
        assertEquals(1, fake.getTimesCalled());

        fake.setResponse("second value");
        assertEquals("first value", cache.get("a"));
        assertEquals(1, fake.getTimesCalled());

        // TODO if this were a real project I would try to avoid sleeping
        // but it's a coding challenge so we can wait 2 seconds
        TimeUnit.SECONDS.sleep(2);
        assertEquals("second value", cache.get("a"));
        assertEquals(2, fake.getTimesCalled());
    }
}
