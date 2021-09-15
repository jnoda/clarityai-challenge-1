package ai.clarity.challenge.solver;

import ai.clarity.challenge.Solution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {

    private static Supplier<InputStream> stringAsInput(String string) {
        return () -> new ByteArrayInputStream(string.getBytes());
    }

    @Nested
    @DisplayName("tests provided")
    class ProvidedTests {
        @Test
        public void solve1plus2() {
            // given
            String string =
                    "a = 1 # 2";

            Solver solver = new Solver(stringAsInput(string), Long::sum, "a");

            // when
            Solution solution = solver.solve();

            // then
            assertEquals(Optional.of(3L), solution.getValue());
        }

        @Test
        public void solveForward() {
            // given
            String string =
                    "myvar = 1 # 2\n" +
                            "forwardResult = myvar # 2";

            Solver solver = new Solver(stringAsInput(string), Long::sum, "forwardResult");

            // when
            Solution solution = solver.solve();

            // then
            assertEquals(Optional.of(5L), solution.getValue());
        }

        @Test
        public void solveBackward() {
            // given
            String string =
                    "fun = last # 2\n" +
                            "backwardResult = fun # last # 3\n" +
                            "last = 1\n";

            Solver solver = new Solver(stringAsInput(string), Long::sum, "backwardResult");

            // when
            Solution solution = solver.solve();

            // then
            assertEquals(Optional.of(7L), solution.getValue());
        }

        @Test
        public void solveNoSolution() {
            // given
            String string =
                    "x = y # 2\n" +
                            "z = x # y # 3";

            Solver solver = new Solver(stringAsInput(string), Long::sum, "z");

            // when
            Solution solution = solver.solve();

            // then
            assertTrue(solution.getValue().isEmpty());
        }

        @Test
        public void solveLoop() {
            // given
            String string =
                    "look1 = 2 # look2\n" +
                            "look2 = look3 # 99 # 12\n" +
                            "look3 = 1 # look1";

            Solver solver = new Solver(stringAsInput(string), Long::sum, "look1");

            // when
            Solution solution = solver.solve();

            // then
            assertTrue(solution.getValue().isEmpty());
        }
    }

    @Nested
    @DisplayName("#constructor")
    class ConstructorTests {
        @Test
        @DisplayName("a null input throws exception")
        void nullInput() {
            NullPointerException ex = assertThrows(NullPointerException.class, () -> {
                new Solver(null, Long::sum, "a");
            });
            assertTrue(ex.getMessage().contains("input"));
        }

        @Test
        @DisplayName("a null operator throws exception")
        void nullOperator() {
            NullPointerException ex = assertThrows(NullPointerException.class, () -> {
                String string = "a = 1 # 2";
                new Solver(stringAsInput(string), null, "a");
            });
            assertTrue(ex.getMessage().contains("operator"));
        }

        @Test
        @DisplayName("a null variable throws exception")
        void nullVariable() {
            NullPointerException ex = assertThrows(NullPointerException.class, () -> {
                String string = "a = 1 # 2";
                new Solver(stringAsInput(string), Long::sum, null);
            });
            assertTrue(ex.getMessage().contains("variable"));
        }

        @Test
        @DisplayName("a blank variable throws exception")
        void blankVariable() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                String string = "a = 1 # 2";
                new Solver(stringAsInput(string), Long::sum, "  ");
            });
            assertTrue(ex.getMessage().contains("variable"));
        }
    }

    @Nested
    @DisplayName("#tryParseLong")
    class TryParseLongTests {
        @Test
        void parseLong() {
            assertEquals(123L, Solver.tryParseLong("123"));
        }

        @Test
        void parseNumberNotLong() {
            assertNull(Solver.tryParseLong("123.1"));
        }

        @Test
        void parseVariable() {
            assertNull(Solver.tryParseLong("var1"));
        }
    }

    @Nested
    @DisplayName("#parseInput")
    class ParseInputTests {
        @Test
        @DisplayName("throws an exception if a variable is defined more than once")
        void variableAlreadyDefined() {
            String string =
                    "repeatedVar = 1 # 2\n" +
                            "b = 1\n" +
                            "repeatedVar = 1 # 3";

            Solver solver = new Solver(stringAsInput(string), Long::sum, "notRelevant");

            ParseException ex = assertThrows(ParseException.class, solver::solve);
            assertTrue(ex.getMessage().contains("repeatedVar"));
        }

        @ParameterizedTest
        @DisplayName("throws an exception if mal-formatted line")
        @ValueSource(strings = {
                "a = ",
                "a 1 # 2",
                "a = 1 2",
                " a = 1 # 2",
                "a = 1 # 2 ",
                "a = 1 #  2"
                // there are infinite cases here, so we are just putting some "common" ones
        })
        void malformedLine(String string) {
            Solver solver = new Solver(stringAsInput(string), Long::sum, "notRelevant");

            ParseException ex = assertThrows(ParseException.class, solver::solve);
            assertTrue(ex.getMessage().contains(string));
        }
    }

    @Nested
    @DisplayName("#solve")
    class SolveTests {
        @Test
        void solveUnknownVariable() {
            String string = "a = 1 # 2";
            Solver solver = new Solver(stringAsInput(string), Long::sum, "notAVariable");

            SolveException ex = assertThrows(SolveException.class, solver::solve);
            assertTrue(ex.getMessage().contains("notAVariable"));
        }
    }
}
