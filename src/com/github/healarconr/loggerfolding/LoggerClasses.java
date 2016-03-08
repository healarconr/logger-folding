package com.github.healarconr.loggerfolding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public final class LoggerClasses {

	private static final Set<String> LOGGER_CLASSES;

	static {
		Set<String> loggerClasses = new HashSet<>();
		loggerClasses.add("java.util.logging.Logger");
		loggerClasses.add("org.slf4j.Logger");
		loggerClasses.add("org.apache.commons.logging.Log");
		loggerClasses.add("org.apache.log4j.Logger");
		LOGGER_CLASSES = Collections.unmodifiableSet(loggerClasses);
	}

	private LoggerClasses() {

		super();
	}

	@NotNull
	public static boolean contains(@Nullable String className) {

		return LOGGER_CLASSES.contains(className);
	}

}
