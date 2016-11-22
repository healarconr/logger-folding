package com.github.healarconr.loggerfolding;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a collection of Logger classes which method calls should be folded. Contains classes for JUL, slf4j, Apache Commons
 * Logging and log4j.
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
final class LoggerClasses {

  private static final Set<String> LOGGER_CLASSES;

  static {
    Set<String> loggerClasses = new HashSet<>();
    loggerClasses.add("java.util.logging.Logger");
    loggerClasses.add("org.slf4j.Logger");
    loggerClasses.add("org.apache.commons.logging.Log");
    loggerClasses.add("org.apache.log4j.Logger");
    loggerClasses.add("org.apache.logging.log4j.Logger");
    loggerClasses.add("android.util.Log");
    loggerClasses.add("timber.log.Timber");
    LOGGER_CLASSES = Collections.unmodifiableSet(loggerClasses);
  }

  private LoggerClasses() {

    super();
  }

  /**
   * Determines if a given class name corresponds to the name of one of the registered logger classes
   *
   * @param className the class name
   * @return true if the class name is one of the registered logger classes
   */
  static boolean contains(@Nullable String className) {

    return LOGGER_CLASSES.contains(className);
  }

}
