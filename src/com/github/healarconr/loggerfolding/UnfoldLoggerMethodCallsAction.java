package com.github.healarconr.loggerfolding;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.healarconr.loggerfolding.ActionHelper.isAvailable;
import static com.github.healarconr.loggerfolding.PsiHelper.getTextRange;
import static com.github.healarconr.loggerfolding.PsiHelper.isALoggerMethodCall;
import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

/**
 * Action to unfold logger method calls
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public class UnfoldLoggerMethodCallsAction extends AnAction {

	@Override
	public void update(AnActionEvent actionEvent) {

		actionEvent.getPresentation().setVisible(isAvailable(actionEvent));
	}

	@Override
	public void actionPerformed(AnActionEvent actionEvent) {

		if (isAvailable(actionEvent)) {
			final Editor editor = actionEvent.getRequiredData(EDITOR);
			PsiJavaFile psiJavaFile = (PsiJavaFile) actionEvent.getRequiredData(PSI_FILE);

			psiJavaFile.accept(new PsiRecursiveElementWalkingVisitor() {

				@Override
				public void visitElement(PsiElement element) {

					super.visitElement(element);

					if (isALoggerMethodCall(element)) {
						PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element;
						TextRange textRange = getTextRange(methodCallExpression);
						FoldRegion foldRegion = getFoldRegion(editor, textRange);
						if (foldRegion != null) {
							removeFoldRegion(editor, foldRegion);
						}
					}
				}
			});
		}

	}

	/**
	 * Returns the fold region that matches the provided text range
	 *
	 * @param editor    the editor
	 * @param textRange the text range
	 * @return the fold region
	 */
	@Nullable
	private FoldRegion getFoldRegion(@NotNull Editor editor, @NotNull TextRange textRange) {

		return editor.getFoldingModel().getFoldRegion(textRange.getStartOffset(), textRange.getEndOffset());
	}

	/**
	 * Runs a batch folding operation that removes the given fold region from the editor folding model
	 *
	 * @param editor     the editor
	 * @param foldRegion the fold region to remove
	 */
	private void removeFoldRegion(@NotNull final Editor editor, @NotNull final FoldRegion foldRegion) {

		editor.getFoldingModel().runBatchFoldingOperation(new Runnable() {
			@Override
			public void run() {

				editor.getFoldingModel().removeFoldRegion(foldRegion);
			}
		});
	}

}
