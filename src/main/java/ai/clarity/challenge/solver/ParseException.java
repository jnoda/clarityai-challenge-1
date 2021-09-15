package ai.clarity.challenge.solver;

/**
 * An exception that can be thrown while parsing the input stream.
 */
public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }
}
