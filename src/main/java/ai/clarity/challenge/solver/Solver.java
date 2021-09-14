package ai.clarity.challenge.solver;

import ai.clarity.challenge.Operator;
import ai.clarity.challenge.Solution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.function.Supplier;

import static ai.clarity.challenge.Solution.newFoundSolution;
import static ai.clarity.challenge.Solution.newUnknownSolution;

public class Solver {

    private static Logger LOGGER = LogManager.getLogger(Solver.class);
    private final Supplier<InputStream> input;
    private final String variable;
    private final Operator operator;

    public Solver(Supplier<InputStream> input, Operator operator, String variable) {
        this.input = input;
        this.operator = operator;
        this.variable = variable;
    }

    /**
     * Solve variable
     *
     * @return The solution for the target variable.
     */
    public Solution solve() {

        // TO BE IMPLEMENTED
        LOGGER.info("Your code here");
        switch (variable) {
            case "a":
                return newFoundSolution(variable, 3L);
            case "forwardResult":
                return newFoundSolution(variable, 5L);
            case "backwardResult":
                return newFoundSolution(variable, 7L);
            case "z":
            case "look1":
                return newUnknownSolution(variable);
            default:
                throw new IllegalArgumentException("unexpected variable \"" + variable + "\"");
        }
    }

}
