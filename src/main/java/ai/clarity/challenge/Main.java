package ai.clarity.challenge;

import picocli.CommandLine;

public class Main {

  public static void main(String[] args) {

    new CommandLine(new SolverCommand()).execute(args);
  }
}
