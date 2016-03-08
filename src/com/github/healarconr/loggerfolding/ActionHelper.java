package com.github.healarconr.loggerfolding;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

/**
 * Utility class to determine if the folding actions should be available
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public final class ActionHelper {

	private ActionHelper() {

		super();
	}

	/**
	 * Determines if the folding actions should be available by evaluating the project, editor and PSI file
	 *
	 * @param actionEvent the action event
	 * @return true if the project, editor and PSI file are not null and the PSI file represents a Java file
	 */
	public static boolean isAvailable(@NotNull AnActionEvent actionEvent) {

		Project project = actionEvent.getData(PROJECT);
		Editor editor = actionEvent.getData(EDITOR);
		PsiFile psiFile = actionEvent.getData(PSI_FILE);
		return project != null && editor != null && psiFile != null && psiFile instanceof PsiJavaFile;
	}

}
