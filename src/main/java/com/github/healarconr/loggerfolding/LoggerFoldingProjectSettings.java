package com.github.healarconr.loggerfolding;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Persistent logger folding project settings
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
@Service(Service.Level.PROJECT)
@State(name = "LoggerFolding")
public final class LoggerFoldingProjectSettings implements PersistentStateComponent<LoggerFoldingProjectSettings.State> {

    private State state = new State();

    /**
     * Helper method to get an instance of the settings
     *
     * @param project the project
     * @return an instance of the settings
     */
    static LoggerFoldingProjectSettings getInstance(Project project) {
        return project.getService(LoggerFoldingProjectSettings.class);
    }

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
     * State class with the canonical names to persist
     *
     * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
     */
    public static class State {

        private LinkedHashSet<String> canonicalNames = getDefaultCanonicalNames();


        /**
         * Returns a linked hash set of the default canonical names of the logger classes
         *
         * @return a linked hash set of the default canonical names of the logger classes
         */
        private static LinkedHashSet<String> getDefaultCanonicalNames() {
            LinkedHashSet<String> defaultCanonicalNames = new LinkedHashSet<>();
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

        public LinkedHashSet<String> getCanonicalNames() {
            return canonicalNames;
        }

        public void setCanonicalNames(LinkedHashSet<String> canonicalNames) {
            this.canonicalNames = new LinkedHashSet<>(canonicalNames);
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
    }

}
