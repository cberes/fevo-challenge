package challenge.images;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import challenge.images.DailyImagesComponent;
import challenge.nasa.Image;
import challenge.nasa.NasaClient;
import challenge.nasa.NasaException;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DailyImagesComponentTest {
    @Test void getImages() {
        final Map<LocalDate, List<Image>> imageData = fakeImageData();
        NasaClient nasa = new NasaClient(null, null, null) {
            @Override
            public List<Image> getImages(final LocalDate date) throws NasaException {
                return imageData.get(date);
            }
        };
        DailyImagesComponent component = new DailyImagesComponent(nasa);
        var imagesByDate = component.getImages(
                LocalDate.of(2016, 4, 2), 10, 3);

        assertEquals(10, imagesByDate.size());
        assertMapKeys(imagesByDate);
        assertMapValues(imagesByDate);
    }

    private static Map<LocalDate, List<Image>> fakeImageData() {
        final Map<LocalDate, List<Image>> imageData = new HashMap<>();
        fakeImages(imageData, LocalDate.of(2016,4, 2), 5);
        fakeImages(imageData, LocalDate.of(2016,4, 1), 5);
        fakeImages(imageData, LocalDate.of(2016,3, 31), 20);
        fakeImages(imageData, LocalDate.of(2016,3, 30), 0);
        fakeImages(imageData, LocalDate.of(2016,3, 29), 1);
        fakeImages(imageData, LocalDate.of(2016,3, 28), 2);
        fakeImages(imageData, LocalDate.of(2016,3, 27), 3);
        fakeImages(imageData, LocalDate.of(2016,3, 26), 0);
        fakeImages(imageData, LocalDate.of(2016,3, 25), 2);
        fakeImages(imageData, LocalDate.of(2016,3, 24), 4);
        return imageData;
    }

    private static void fakeImages(final Map<LocalDate, List<Image>> imageData,
                                   final LocalDate date,
                                   final int imageCount) {
        final List<Image> images = Stream.iterate(0, x -> x + 1)
                .limit(imageCount)
                .map(i -> new Image(Character.toString('a' + i), date))
                .collect(toList());
        imageData.put(date, images);
    }

    private static void assertMapKeys(final Map<LocalDate, List<String>> imagesByDate) {
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 4, 2)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 4, 1)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 3, 31)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 3, 30)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 3, 29)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 3, 28)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 3, 27)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 3, 26)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 3, 25)));
        assertThat(imagesByDate, hasKey(LocalDate.of(2016, 3, 24)));
    }

    private static void assertMapValues(final Map<LocalDate, List<String>> imagesByDate) {
        assertImages(imagesByDate.get(LocalDate.of(2016, 4, 2)), 3);
        assertImages(imagesByDate.get(LocalDate.of(2016, 4, 1)), 3);
        assertImages(imagesByDate.get(LocalDate.of(2016, 3, 31)), 3);
        assertImages(imagesByDate.get(LocalDate.of(2016, 3, 30)), 0);
        assertImages(imagesByDate.get(LocalDate.of(2016, 3, 29)), 1);
        assertImages(imagesByDate.get(LocalDate.of(2016, 3, 28)), 2);
        assertImages(imagesByDate.get(LocalDate.of(2016, 3, 27)), 3);
        assertImages(imagesByDate.get(LocalDate.of(2016, 3, 26)), 0);
        assertImages(imagesByDate.get(LocalDate.of(2016, 3, 25)), 2);
        assertImages(imagesByDate.get(LocalDate.of(2016, 3, 24)), 3);
    }

    private static void assertImages(final List<String> images, final int expectedCount) {
        assertNotNull(images);
        assertThat(images, hasSize(expectedCount));

        final HashSet<String> set = new HashSet<>(images);
        assertThat(set, hasSize(expectedCount));
    }
}
