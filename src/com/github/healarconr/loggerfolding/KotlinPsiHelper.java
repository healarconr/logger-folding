package com.github.healarconr.loggerfolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.*;

import java.util.Collections;

/**
 * Helper class to determine if a PsiElement represents a Kotlin logger method call and to obtain the text range and
 * placeholder text of a PsiElement
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
final class KotlinPsiHelper {

  private KotlinPsiHelper() {

    super();
  }

  /**
   * Determines if a PsiElement represents a Kotlin logger method call
   *
   * @param element the element
   * @param state   the state of the logger folding settings
   * @return true if the element represents a method call on a class defined in {@link LoggerFoldingSettings.State#getCanonicalNamesSet()}
   */
  static boolean isALoggerMethodCall(@NotNull PsiElement element, LoggerFoldingSettings.State state) {

    if (!(element instanceof KtDotQualifiedExpression)) {
      return false;
    }

    KtDotQualifiedExpression dotQualifiedExpression = (KtDotQualifiedExpression) element;

    KtExpression selectorExpression = dotQualifiedExpression.getSelectorExpression();

    if (!(selectorExpression instanceof KtCallExpression)) {
      return false;
    }

    KtExpression receiverExpression = dotQualifiedExpression.getReceiverExpression();

    PsiReference[] references = receiverExpression.getReferences();

    PsiElement resolvedReference = resolveReference(references);

    if (!(resolvedReference instanceof KtProperty)) {
      return false;
    }

    KtProperty property = (KtProperty) resolvedReference;

    KtTypeReference typeReference = property.getTypeReference();

    String canonicalName;

    if (typeReference != null) {
      canonicalName = typeReference.getText();
    } else {
      canonicalName = getCanonicalNameFromPropertyCallExpression(property);
    }

    return PsiHelper.isAnyCanonicalTextContainedInTheCanonicalNames(Collections.singletonList(canonicalName), state
        .getCanonicalNamesSet());
  }

  /**
   * Iterates through the received references resolving them until one of the resolved references is not null
   *
   * @param references the references
   * @return the resolved reference PSI element or null if no reference could be resolved
   */
  private static PsiElement resolveReference(PsiReference[] references) {

    for (PsiReference reference : references) {
      PsiElement resolvedReference = reference.resolve();
      if (resolvedReference != null) {
        return resolvedReference;
      }
    }
    return null;
  }

  /**
   * Gets the class canonical name from the property call expression used to initialize it
   *
   * @param property the property
   * @return the class canonical name or null if it could not be found
   */
  private static String getCanonicalNameFromPropertyCallExpression(KtProperty property) {

    KtCallExpression callExpression = null;

    PsiElement[] children = property.getChildren();

    for (PsiElement child : children) {
      if (child instanceof KtCallExpression) {
        callExpression = (KtCallExpression) child;
        break;
      }
    }

    if (callExpression == null) {
      return null;
    }

    KtExpression calleeExpression = callExpression.getCalleeExpression();

    if (calleeExpression == null) {
      return null;
    }

    PsiReference[] references = calleeExpression.getReferences();

    PsiElement resolvedReference = resolveReference(references);

    if (!(resolvedReference instanceof KtNamedFunction)) {
      return null;
    }

    KtNamedFunction namedFunction = (KtNamedFunction) resolvedReference;

    KtTypeReference typeReference = namedFunction.getTypeReference();

    if (typeReference == null) {
      return null;
    }

    return typeReference.getText();
  }


  /**
   * Returns the text range of the received element
   * next to it
   *
   * @param element the element
   * @return the text range
   */
  @NotNull
  static TextRange getTextRange(@NotNull PsiElement element) {

    return element.getTextRange();
  }

  /**
   * Returns the placeholder text used in the fold region for a Kotlin logger method call
   *
   * @param element the method call expression
   * @return the element text replacing the value argument list with "(…)" and the lambda argument with "{…}"
   */
  @NotNull
  static String getPlaceholderText(@NotNull PsiElement element) {

    StringBuilder placeholderText = new StringBuilder();

    KtDotQualifiedExpression dotQualifiedExpression = (KtDotQualifiedExpression) element;

    placeholderText.append(dotQualifiedExpression.getReceiverExpression().getText());

    KtExpression selectorExpression = dotQualifiedExpression.getSelectorExpression();

    if (selectorExpression != null) {

      placeholderText.append(".");

      PsiElement[] children = selectorExpression.getChildren();

      for (PsiElement child : children) {
        if (child instanceof KtValueArgumentList) {
          placeholderText.append("(…)");
        } else if (child instanceof KtLambdaArgument) {
          placeholderText.append("{…}");
        } else {
          placeholderText.append(child.getText());
        }
      }
    }

    return placeholderText.toString();
  }

}
