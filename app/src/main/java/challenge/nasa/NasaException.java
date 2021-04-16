package challenge.nasa;

public class NasaException extends RuntimeException {
    public NasaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
