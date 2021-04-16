package challenge.http;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class FileCacheHttpClient implements HttpClient {
    private static final int DEFAULT_TTL_MINUTES = 30;

    private final HttpClient delegate;
    private final File cacheDir;
    private final int ttl;
    private final TemporalUnit ttlUnit;

    public FileCacheHttpClient(final HttpClient delegate) {
        this(delegate, defaultCacheDirectory(), DEFAULT_TTL_MINUTES, ChronoUnit.MINUTES);
    }

    public FileCacheHttpClient(final HttpClient delegate, final File cacheDir, final int ttl, final TemporalUnit ttlUnit) {
        this.delegate = delegate;
        this.cacheDir = cacheDir;
        this.ttl = ttl;
        this.ttlUnit = ttlUnit;
        cacheDir.mkdirs();
    }

    private static File defaultCacheDirectory() {
        return new File(System.getProperty("user.home"), ".nasa-cache");
    }

    @Override
    public String get(final String uri) throws IOException, InterruptedException {
        File cached = cacheFile(uri);
        if (isCacheFileValid(cached)) {
            return readFile(cached);
        } else {
            return invokeDelegateAndCache(uri, cached);
        }
    }

    private File cacheFile(final String uri) {
        return new File(cacheDir, URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    private boolean isCacheFileValid(final File file) {
        return file.exists() && file.lastModified() >= earliestValidTimeAsEpochMillis();
    }

    private long earliestValidTimeAsEpochMillis() {
        return ZonedDateTime.now(ZoneOffset.UTC)
                .minus(ttl, ttlUnit)
                .toInstant().toEpochMilli();
    }

    private String readFile(final File file) throws IOException {
        return Files.readString(file.toPath());
    }

    private String invokeDelegateAndCache(final String uri, final File cached) throws IOException, InterruptedException {
        final String response = delegate.get(uri);
        writeFile(cached, response);
        return response;
    }

    private void writeFile(final File file, final String contents) throws IOException {
        Files.writeString(file.toPath(), contents);
    }
}
