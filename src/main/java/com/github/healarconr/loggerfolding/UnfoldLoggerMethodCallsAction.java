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
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
            unfoldJavaFile(editor, psiFile, state);
        } else {
            unfoldKotlinFile(editor, psiFile, state);
        }
    }

    /**
     * Unfolds logger method calls in a Java file
     *
     * @param editor  the editor
     * @param psiFile the PSI file
     * @param state   the state
     */
    private void unfoldJavaFile(Editor editor, PsiFile psiFile, LoggerFoldingProjectSettings.State state) {
        psiFile.accept(new PsiRecursiveElementWalkingVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {

                super.visitElement(element);
                if (JavaPsiHelper.isALoggerMethodCall(element, state)) {
                    TextRange textRange = JavaPsiHelper.getTextRange(element);
                    FoldRegion foldRegion = getFoldRegion(editor, textRange);
                    if (foldRegion != null) {
                        removeFoldRegion(editor, foldRegion);
                    }
                }
            }
        });
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

        editor.getFoldingModel().runBatchFoldingOperation(() -> editor.getFoldingModel().removeFoldRegion(foldRegion));
    }

    /**
     * Unfolds logger method calls in a Kotlin file
     *
     * @param editor  the editor
     * @param psiFile the PSI file
     * @param state   the state
     */
    private void unfoldKotlinFile(Editor editor, PsiFile psiFile, LoggerFoldingProjectSettings.State state) {
        psiFile.accept(new PsiRecursiveElementWalkingVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {

                super.visitElement(element);
                if (KotlinPsiHelper.isALoggerMethodCall(element, state)) {
                    TextRange textRange = KotlinPsiHelper.getTextRange(element);
                    FoldRegion foldRegion = getFoldRegion(editor, textRange);
                    if (foldRegion != null) {
                        removeFoldRegion(editor, foldRegion);
                    }
                }
            }
        });
    }
}
