package com.github.healarconr.loggerfolding;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

/**
 * Persistent logger folding application settings
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
@State(name = "LoggerFolding", storages = @Storage("editor.codeinsight.xml"))
public class LoggerFoldingApplicationSettings implements PersistentStateComponent<LoggerFoldingApplicationSettings.State> {

  /**
   * State class with the collapse by default flag
   *
   * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
   */
  public static class State {

    private Boolean collapseByDefault = false;

    public Boolean getCollapseByDefault() {
      return collapseByDefault;
    }

    public void setCollapseByDefault(Boolean collapseByDefault) {
      this.collapseByDefault = collapseByDefault;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      State state = (State) o;

      return collapseByDefault.equals(state.collapseByDefault);
    }

    @Override
    public int hashCode() {
      return collapseByDefault.hashCode();
    }
  }

  private State state = new State();

  @NotNull
  @Override
  public State getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull State state) {
    this.state = state;
  }

  /**
   * Helper method to get an instance of the settings
   *
   * @return an instance of the settings
   */
  static LoggerFoldingApplicationSettings getInstance() {
    return ApplicationManager.getApplication().getService(LoggerFoldingApplicationSettings.class);
  }

}
