package com.github.healarconr.loggerfolding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Folding builder for Java logger method calls
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public class JavaFoldingBuilder extends FoldingBuilderEx {

    /**
     * Builds fold regions for a PSI element and its children
     *
     * @param psiElement         the PSI element
     * @param foldingDescriptors the folding descriptors
     * @param state              the Logger Folding project settings state
     */
    private static void buildFoldRegions(@NotNull PsiElement psiElement, List<FoldingDescriptor> foldingDescriptors, LoggerFoldingProjectSettings.State state) {

        if (JavaPsiHelper.isALoggerMethodCall(psiElement, state)) {
            TextRange textRange = JavaPsiHelper.getTextRange(psiElement);
            foldingDescriptors.add(new FoldingDescriptor(psiElement, textRange));
        }

        for (PsiElement child : psiElement.getChildren()) {
            buildFoldRegions(child, foldingDescriptors, state);
        }
    }

    @NotNull
    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean quick) {

        if (!(psiElement instanceof PsiJavaFile) || quick || !LoggerFoldingApplicationSettings.getInstance().getState()
                .getCollapseByDefault()) {
            return new FoldingDescriptor[0];
        }

        List<FoldingDescriptor> foldingDescriptors = new LinkedList<>();

        Project project = psiElement.getProject();
        LoggerFoldingProjectSettings.State state = LoggerFoldingProjectSettings.getInstance(project).getState();

        buildFoldRegions(psiElement, foldingDescriptors, state);

        return foldingDescriptors.toArray(new FoldingDescriptor[0]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {

        PsiElement psiElement = astNode.getPsi();
        return JavaPsiHelper.getPlaceholderText(psiElement);
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {

        return true;
    }

}
