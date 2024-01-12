package com.github.healarconr.loggerfolding;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

        actionEvent.getPresentation().setVisible(ActionHelper.isAvailable(actionEvent));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {

        if (!ActionHelper.isAvailable(actionEvent)) {
            return;
        }

        Editor editor = actionEvent.getRequiredData(EDITOR);
        PsiFile psiFile = actionEvent.getRequiredData(PSI_FILE);

        LoggerFoldingProjectSettings.State state = LoggerFoldingProjectSettings.getInstance(Objects.requireNonNull(actionEvent.getProject())).getState();

        if (psiFile instanceof PsiJavaFile) {
            psiFile.accept(new PsiRecursiveElementWalkingVisitor() {

                @Override
                public void visitElement(@NotNull PsiElement element) {

                    super.visitElement(element);
                    if (JavaPsiHelper.isALoggerMethodCall(element, state)) {
                        TextRange textRange = JavaPsiHelper.getTextRange(element);
                        String placeholderText = JavaPsiHelper.getPlaceholderText(element);
                        fold(editor, textRange, placeholderText);
                    }
                }
            });
        } else {
            psiFile.accept(new PsiRecursiveElementWalkingVisitor() {

                @Override
                public void visitElement(@NotNull PsiElement element) {

                    super.visitElement(element);
                    if (KotlinPsiHelper.isALoggerMethodCall(element, state)) {
                        TextRange textRange = KotlinPsiHelper.getTextRange(element);
                        String placeholderText = KotlinPsiHelper.getPlaceholderText(element);
                        fold(editor, textRange, placeholderText);
                    }
                }
            });
        }

    }

    /**
     * Runs a batch folding operation that folds the given text range
     *
     * @param editor          the editor to get the folding model
     * @param textRange       the text range to fold
     * @param placeholderText the fold region placeholder text
     */
    private void fold(@NotNull final Editor editor, @NotNull final TextRange textRange, @NotNull final String placeholderText) {

        editor.getFoldingModel().runBatchFoldingOperation(() -> {

            FoldRegion foldRegion = editor.getFoldingModel()
                    .getFoldRegion(textRange.getStartOffset(), textRange.getEndOffset());
            if (foldRegion == null) {
                foldRegion = editor.getFoldingModel()
                        .addFoldRegion(textRange.getStartOffset(), textRange.getEndOffset(),
                                placeholderText);
            }
            if (foldRegion != null) {
                foldRegion.setExpanded(false);
            }
        });
    }

}
