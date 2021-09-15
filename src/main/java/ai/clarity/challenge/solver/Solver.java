package ai.clarity.challenge.solver;

import ai.clarity.challenge.Operator;
import ai.clarity.challenge.Solution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ai.clarity.challenge.Solution.newFoundSolution;
import static ai.clarity.challenge.Solution.newUnknownSolution;

public class Solver {
    private static final Logger LOGGER = LogManager.getLogger(Solver.class);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final Pattern LINE_PATTERN = Pattern.compile("(\\w+) = (\\w+(?: # \\w+)*)");

    private final Supplier<InputStream> input;
    private final String variable;
    private final Operator operator;

    public Solver(Supplier<InputStream> input, Operator operator, String variable) {
        this.input = Objects.requireNonNull(input, "input must not be null");
        this.operator = Objects.requireNonNull(operator, "operator must not be null");
        this.variable = Objects.requireNonNull(variable, "variable must not be null");

        if (this.variable.isBlank()) {
            throw new IllegalArgumentException("variable must not be blank");
        }
    }

    /**
     * Tries to parse the given number as a long.
     *
     * @param number the number to be parsed
     * @return the parsed number, if it can be parsed; otherwise <code>null</code>.
     */
    static Long tryParseLong(String number) {
        return (NUMBER_PATTERN.matcher(number).matches()) ? Long.parseLong(number) : null;
    }

    /**
     * Parses the input stream into a map containing the variable names as keys, and a list of operand in the expression as values.
     *
     * <p>Is only important to keep the list of operands, because the operator is said to be commutative and associative.</p>
     *
     * @return the map with the unsolved  variables
     * @throws ParseException if there is a problem parsing the input stream
     */
    private Map<String, List<String>> parseInput() {
        Map<String, List<String>> unsolvedVariables = new HashMap<>();
        try (InputStream inputStream = input.get()) {
            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().forEach(line -> {
                Matcher matcher = LINE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String varName = matcher.group(1);
                    if (unsolvedVariables.containsKey(varName)) {
                        throw new ParseException("Variable already defined: \"" + varName + "\"");
                    } else {
                        unsolvedVariables.put(varName, List.of(matcher.group(2).split(" # ")));
                    }
                } else {
                    throw new ParseException("Invalid format in line: " + line);
                }
            });
            return unsolvedVariables;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Solve variable
     *
     * @return The solution for the target variable.
     */
    public Solution solve() {
        // These maps are declared as local variables to avoid assumptions about the context in which Solver#solve
        // will be called, because Supplier#get might supply different results in each call.
        // Otherwise, making them fields in the class will be both more efficient and clearer.
        Map<String, List<String>> unsolvedVariables = parseInput();
        Map<String, Long> solvedVariables = new HashMap<>();

        if (!unsolvedVariables.containsKey(variable)) {
            throw new SolveException("unexpected variable \"" + variable + "\"");
        }

        try {
            return newFoundSolution(variable, solveOperand(variable, unsolvedVariables, solvedVariables));
        } catch (SolveException exception) {
            LOGGER.debug(exception.getMessage());
            return newUnknownSolution(variable);
        }
    }

    /**
     * Attempts to solve the provided operand, that can be either a variable or a number. If it is a variable, tries to solve recursively each operand in the expression.
     *
     * @param operand           the operand to solve
     * @param unsolvedVariables the map with the unsolved variables
     * @param solvedVariables   the map with solved variables
     * @return the solved operand
     * @throws SolveException if there is a problem trying to solve the operand
     */
    private Long solveOperand(String operand, Map<String, List<String>> unsolvedVariables, Map<String, Long> solvedVariables) {
        // try to parse the operand as a number
        Long result = tryParseLong(operand);

        // if the operand was not a number, then is a variable
        if (result == null) {
            // check if it was already solved
            if (solvedVariables.containsKey(operand)) {
                result = solvedVariables.get(operand);
                if (result == null) {
                    // this would mean that we started to solve the variable somewhere before in the current recursion
                    // and didn't finish, so we are in a loop and cannot solve the equation
                    throw new SolveException("loop found while trying to solve variable \"" + operand + "\\");
                }
            } else {
                // the variable was not solved before, let's do it now
                if (unsolvedVariables.containsKey(operand)) {
                    // first add it to the solveVariables map, to signal we are attempting to solve it
                    solvedVariables.put(operand, null);

                    // recursively solve each operand in the expression and perform a reduction using the operator
                    result = unsolvedVariables.get(operand).stream().
                            map(op -> solveOperand(op, unsolvedVariables, solvedVariables))
                            .reduce(this.operator::operate)
                            // this should never happen since we will have at least one operand, preferred to use .get()
                            .orElseThrow(() -> new SolveException("This shouldn't happen"));

                    // put in the solved value in the solvedVariables map, for future use
                    solvedVariables.put(operand, result);
                } else {
                    // this means that the variable is not even defined
                    throw new SolveException("variable not defined \"" + operand + "\"");
                }
            }
        }

        return result;
    }
}
