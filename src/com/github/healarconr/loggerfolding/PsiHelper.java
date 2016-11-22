package com.github.healarconr.loggerfolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Helper class to determine if a PsiElement represents a logger method call and to obtain the text range of a PsiElement from its
 * start offset to the immediate following semicolon.
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
final class PsiHelper {

  private PsiHelper() {

    super();
  }

  /**
   * Determines if a PsiElement represents a logger method call by evaluating the qualifier type
   *
   * @param element the element
   * @return true if the qualifier type is one of the logger classes defined in {@link LoggerClasses}
   */
  static boolean isALoggerMethodCall(@NotNull PsiElement element) {

    if (element instanceof PsiMethodCallExpression) {
      PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element;
      PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
      PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
      if (qualifierExpression != null) {

        List<String> canonicalTexts = new LinkedList<>();

        PsiType type = qualifierExpression.getType();
        if (type != null) {
          canonicalTexts.add(type.getCanonicalText());
        }

        if (qualifierExpression instanceof PsiReferenceExpression) {
          PsiReferenceExpression referenceExpression = (PsiReferenceExpression) qualifierExpression;
          if (referenceExpression.isQualified()) {
            canonicalTexts.add(referenceExpression.getCanonicalText());
          } else {
            PsiJavaFile javaFile = (PsiJavaFile) element.getContainingFile();
            PsiImportList importList = javaFile.getImportList();
            if (importList != null) {
              PsiImportStatementBase importStatement = importList.findSingleImportStatement(referenceExpression.getReferenceName());
              if (importStatement != null) {
                PsiJavaCodeReferenceElement importReference = importStatement.getImportReference();
                if (importReference != null) {
                  canonicalTexts.add(importReference.getCanonicalText());
                }
              }
            }
          }
        }

        for (String canonicalText : canonicalTexts) {
          if (LoggerClasses.contains(canonicalText)) {
            return true;
          }
        }

        return false;
      }
    }
    return false;
  }

  /**
   * Returns the text range that starts at the start offset of the provided element and ends at the end offset of the semicolon
   * next to it
   *
   * @param element the element
   * @return the text range
   */
  @NotNull
  static TextRange getTextRange(@NotNull PsiElement element) {

    TextRange textRange = element.getTextRange();
    PsiElement semicolon = findSemicolonNextTo(element);
    if (semicolon == null) {
      return textRange;
    } else {
      return textRange.union(semicolon.getTextRange());
    }
  }

  /**
   * Iterates through the next siblings of the provided element until a semicolon is found
   *
   * @param element the element
   * @return the first sibling of the provided element that represents a semicolon or null if none is found
   */
  @Nullable
  @SuppressWarnings("ConstantConditions")
  private static PsiElement findSemicolonNextTo(@NotNull PsiElement element) {

    while ((element = element.getNextSibling()) != null) {
      if (element instanceof PsiJavaToken) {
        PsiJavaToken javaToken = (PsiJavaToken) element;
        if (";".equals(javaToken.getText())) {
          return element;
        }
      }
    }
    return null;
  }

}
