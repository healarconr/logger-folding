package com.github.healarconr.loggerfolding;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Persistent logger folding project settings
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
@State(name = "LoggerFolding")
public class LoggerFoldingProjectSettings implements PersistentStateComponent<LoggerFoldingProjectSettings.State> {

  /**
   * State class with the canonical names to persist
   *
   * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
   */
  public static class State {

    private List<String> canonicalNames = getDefaultCanonicalNames();
    private Set<String> canonicalNamesSet = new HashSet<>(canonicalNames);

    public List<String> getCanonicalNames() {
      return canonicalNames;
    }

    public void setCanonicalNames(List<String> canonicalNames) {
      this.canonicalNames = new ArrayList<>(canonicalNames);
      this.canonicalNamesSet = new HashSet<>(this.canonicalNames);
    }

    /**
     * Returns a set of the canonical names to perform a faster search
     *
     * @return a set of the canonical names
     */
    Set<String> getCanonicalNamesSet() {
      return canonicalNamesSet;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      State state = (State) o;
      return Objects.equals(canonicalNames, state.canonicalNames);
    }

    @Override
    public int hashCode() {
      return Objects.hash(canonicalNames);
    }

    /**
     * Returns a list of the default canonical names of the logger classes
     *
     * @return a list of the default canonical names of the logger classes
     */
    private static List<String> getDefaultCanonicalNames() {
      List<String> defaultCanonicalNames = new LinkedList<>();
      defaultCanonicalNames.add("java.util.logging.Logger");
      defaultCanonicalNames.add("org.slf4j.Logger");
      defaultCanonicalNames.add("org.apache.commons.logging.Log");
      defaultCanonicalNames.add("org.apache.log4j.Logger");
      defaultCanonicalNames.add("org.apache.logging.log4j.Logger");
      defaultCanonicalNames.add("android.util.Log");
      defaultCanonicalNames.add("timber.log.Timber");
      defaultCanonicalNames.add("mu.KLogger");
      return defaultCanonicalNames;
    }
  }

  private State state = new State();

  @NotNull
  @Override
  public State getState() {
    return state;
  }

  @Override
  public void loadState(State state) {
    this.state = state;
  }

  /**
   * Helper method to get an instance of the settings
   *
   * @param project the project
   * @return an instance of the settings
   */
  static LoggerFoldingProjectSettings getInstance(Project project) {
    return ServiceManager.getService(project, LoggerFoldingProjectSettings.class);
  }

}
