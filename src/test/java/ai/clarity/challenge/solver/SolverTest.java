package ai.clarity.challenge.solver;

import ai.clarity.challenge.Solution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
