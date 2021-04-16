package challenge.nasa;

import java.time.LocalDate;

public class Image {
    private String source;
    private LocalDate date;

    public Image(final String source, final LocalDate date) {
        this.source = source;
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }
}
