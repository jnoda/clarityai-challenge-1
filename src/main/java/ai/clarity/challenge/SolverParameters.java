package ai.clarity.challenge;

import picocli.CommandLine.Option;

public class SolverParameters {

  @Option(order = 1, names = {"-f", "--file"}, description = "The name of the file.", required = true)
  private String fileName;

  @Option(order = 2, names = {"-v", "--target-variable"}, description = "The variable to solve.", required = true)
  private String targetVariable;

  public String getFileName() {
    return fileName;
  }

  public String getTargetVariable() {
    return targetVariable;
  }

}
