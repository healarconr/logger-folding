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

import static com.github.healarconr.loggerfolding.ActionHelper.isAvailable;
import static com.github.healarconr.loggerfolding.PsiHelper.getTextRange;
import static com.github.healarconr.loggerfolding.PsiHelper.isAMethodCallOnALogger;
import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

/**
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public class FoldLoggerMethodCallsAction extends AnAction {

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

					if (isAMethodCallOnALogger(element)) {
						PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element;
						TextRange textRange = getTextRange(methodCallExpression);
						String placeholderText = getPlaceholderText(methodCallExpression);
						fold(editor, textRange, placeholderText);
					}
				}
			});
		}
	}

	@NotNull
	private String getPlaceholderText(@NotNull PsiMethodCallExpression methodCallExpression) {

		return methodCallExpression.getMethodExpression().getText() + "(...);";
	}

	private void fold(@NotNull final Editor editor, @NotNull final TextRange textRange, @NotNull final String placeholderText) {

		editor.getFoldingModel().runBatchFoldingOperation(new Runnable() {

			@Override
			public void run() {

				FoldRegion foldRegion = editor.getFoldingModel()
											  .addFoldRegion(textRange.getStartOffset(), textRange.getEndOffset(),
													  placeholderText);
				if (foldRegion != null) {
					foldRegion.setExpanded(false);
				}
			}
		});
	}

}
