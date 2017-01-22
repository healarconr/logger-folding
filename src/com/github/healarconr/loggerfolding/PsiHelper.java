package com.github.healarconr.loggerfolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
   * Determines if a PsiElement represents a logger method call
   *
   * @param element the element
   * @param state   the state of the logger folding settings
   * @return true if the element represents a method call on a class defined in {@link LoggerFoldingSettings.State#getCanonicalNamesSet()}
   */
  static boolean isALoggerMethodCall(@NotNull PsiElement element, LoggerFoldingSettings.State state) {

    if (element instanceof PsiMethodCallExpression) {
      PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element;
      PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
      PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
      if (qualifierExpression != null) {
        List<String> canonicalTexts = new LinkedList<>();
        addCanonicalTextFromType(qualifierExpression, canonicalTexts);
        addCanonicalTextFromReferenceExpression(qualifierExpression, canonicalTexts);
        return isAnyCanonicalTextContainedInTheCanonicalNames(canonicalTexts, state.getCanonicalNamesSet());
      }
    }
    return false;
  }

  /**
   * Adds the canonical text of the type of the qualifier expression to the canonical texts list
   *
   * @param qualifierExpression the qualifier expression
   * @param canonicalTexts      the canonical texts
   */
  private static void addCanonicalTextFromType(PsiExpression qualifierExpression, List<String> canonicalTexts) {
    PsiType type = qualifierExpression.getType();
    if (type != null) {
      canonicalTexts.add(type.getCanonicalText());
    }
  }

  /**
   * Adds the canonical text of a reference expression to the canonical texts list. If the reference expression is qualified
   * the canonical text if obtained directly from it, but if it is not qualified the canonical text is obtained from the import statement
   *
   * @param qualifierExpression the qualifier expression
   * @param canonicalTexts      the canonical texts
   */
  private static void addCanonicalTextFromReferenceExpression(PsiExpression qualifierExpression, List<String> canonicalTexts) {
    if (qualifierExpression instanceof PsiReferenceExpression) {
      PsiReferenceExpression referenceExpression = (PsiReferenceExpression) qualifierExpression;
      if (referenceExpression.isQualified()) {
        canonicalTexts.add(referenceExpression.getCanonicalText());
      } else {
        addCanonicalTextFromImport(referenceExpression, canonicalTexts);
      }
    }
  }

  /**
   * Adds the canonical text of an import reference to the canonical texts list.
   *
   * @param referenceExpression the reference expression
   * @param canonicalTexts      the canonical texts
   */
  private static void addCanonicalTextFromImport(PsiReferenceExpression referenceExpression, List<String> canonicalTexts) {
    PsiJavaFile javaFile = (PsiJavaFile) referenceExpression.getContainingFile();
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

  /**
   * Determines if any of the canonical texts is contained in the canonical names set
   *
   * @param canonicalTexts    the canonical texts
   * @param canonicalNamesSet the canonical names set
   * @return true if any of the canonical texts is contained in the canonical names set, false otherwise
   */
  private static boolean isAnyCanonicalTextContainedInTheCanonicalNames(List<String> canonicalTexts, Set<String> canonicalNamesSet) {
    for (String canonicalText : canonicalTexts) {
      if (canonicalNamesSet.contains(canonicalText)) {
        return true;
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
