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
import static com.github.healarconr.loggerfolding.PsiHelper.isALoggerMethodCall;
import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

/**
 * Action to fold logger method calls
 *
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
          if (isALoggerMethodCall(element)) {
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element;
            TextRange textRange = getTextRange(methodCallExpression);
            String placeholderText = getPlaceholderText(methodCallExpression);
            fold(editor, textRange, placeholderText);
          }
        }
      });
    }
  }

  /**
   * Returns the placeholder text used in the fold region for a logger method call
   *
   * @param methodCallExpression the method call expression
   * @return the method expression text followed by "(...);"
   */
  @NotNull
  private String getPlaceholderText(@NotNull PsiMethodCallExpression methodCallExpression) {

    return methodCallExpression.getMethodExpression().getText() + "(...);";
  }

  /**
   * Runs a batch folding operation that folds the given text range
   *
   * @param editor          the editor to get the folding model
   * @param textRange       the text range to fold
   * @param placeholderText the fold region placeholder text
   */
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
