package challenge.images;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import challenge.nasa.Image;
import challenge.nasa.NasaClient;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

public class DailyImagesComponent {
    private final NasaClient nasa;

    public DailyImagesComponent(final NasaClient nasa) {
        this.nasa = nasa;
    }

    public Map<LocalDate, List<String>> getImages(final LocalDate start,
                                                  final int dayCount,
                                                  final int imagesPerDay) {
        List<LocalDate> dates = dates(start, dayCount);
        var images = dates.stream()
                .map(nasa::getImages)
                .flatMap(List::stream)
                .collect(groupingBy(Image::getDate, mapping(Image::getSource,
                        collectingAndThen(toList(), list -> limit(list, imagesPerDay)))));
        putEmptyListForMissingDates(dates, images);
        return images;
    }

    private static List<LocalDate> dates(final LocalDate mostRecentDate, final int dayCount) {
        return Stream.iterate(mostRecentDate, date -> date.minusDays(1))
                .limit(dayCount)
                .collect(toList());
    }

    private static <E> List<E> limit(final List<E> input, final int count) {
        return input.size() <= count ? input : input.subList(0, count);
    }

    private static void putEmptyListForMissingDates(final List<LocalDate> dates, final Map<LocalDate, List<String>> images) {
        dates.forEach(date -> images.putIfAbsent(date, emptyList()));
    }
}
