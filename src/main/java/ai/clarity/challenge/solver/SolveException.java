package ai.clarity.challenge.solver;

/**
 * An exception that can be thrown while trying to solve the equations.
 */
public class SolveException extends RuntimeException {
    public SolveException(String message) {
        super(message);
    }
}
