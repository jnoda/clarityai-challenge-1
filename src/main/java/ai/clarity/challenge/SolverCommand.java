package ai.clarity.challenge;

import ai.clarity.challenge.solver.OperatorImpl;
import ai.clarity.challenge.solver.Solver;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "run.sh",
  mixinStandardHelpOptions = true,
  sortOptions = false,
  usageHelpWidth = 180,
  description = "Variable solver"
)
public class SolverCommand extends SolverParameters implements Callable<Integer> {

  private static Logger LOGGER = LogManager.getLogger(SolverCommand.class);

  @Override
  public Integer call() {
    LOGGER.info("\n\nClarity Code Challenge\n"
        + "\tfile name: {}\n"
        + "\ttarget variable: {}\n"
      , getFileName()
      , getTargetVariable()
    );

    Solver solver = new Solver(this::getFileInputStream, OperatorImpl::apply, getTargetVariable());
    Solution solution = solver.solve();

    printSolution(solution);

    return 0;
  }

  private FileInputStream getFileInputStream() {
    try {
      return new FileInputStream(getFileName());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private void printSolution(Solution solution) {
    LOGGER.info("SOLUTION: ");
    LOGGER.info("{} = {}", solution.getVariable(), solution.getValue().map(String::valueOf).orElse("?"));
  }

}
