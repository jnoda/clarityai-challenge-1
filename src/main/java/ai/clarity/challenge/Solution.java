package ai.clarity.challenge;

import java.util.Objects;
import java.util.Optional;

public class Solution {

  private final String variable;
  private final Long value;

  private Solution(String variable, Long value) {
    Objects.requireNonNull(variable, "Variable must not be null");
    this.variable = variable;
    this.value = value;
  }

  public static Solution newFoundSolution(String variable, long value) {
    return new Solution(variable, value);
  }

  public static Solution newUnknownSolution(String variable) {
    return new Solution(variable, null);
  }

  public String getVariable() {
    return variable;
  }

  public Optional<Long> getValue() {
    return Optional.ofNullable(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Solution solution = (Solution) o;
    return variable.equals(solution.variable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variable);
  }

  @Override
  public String toString() {
    return "Solution{" +
      "variable='" + variable + '\'' +
      ", value=" + value +
      '}';
  }

}
