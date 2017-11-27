package com.github.healarconr.loggerfolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
   * @return true if the element represents a method call on a class defined in {@link LoggerFoldingProjectSettings.State#getCanonicalNamesSet()}
   */
  static boolean isALoggerMethodCall(@NotNull PsiElement element, LoggerFoldingProjectSettings.State state) {

    if (!(element instanceof KtDotQualifiedExpression)) {
      return false;
    }

    KtDotQualifiedExpression dotQualifiedExpression = (KtDotQualifiedExpression) element;

    KtExpression selectorExpression = dotQualifiedExpression.getSelectorExpression();

    if (!(selectorExpression instanceof KtCallExpression)) {
      return false;
    }

    KtExpression receiverExpression = dotQualifiedExpression.getReceiverExpression();
    KtReferenceExpression referenceExpression = null;

    if (receiverExpression instanceof KtReferenceExpression) {
      referenceExpression = (KtReferenceExpression) receiverExpression;
    } else if (receiverExpression instanceof KtDotQualifiedExpression) {
      PsiElement[] children = receiverExpression.getChildren();
      for (PsiElement child : children) {
        if (child instanceof KtReferenceExpression) {
          referenceExpression = (KtReferenceExpression) child;
          break;
        }
      }
    }

    if (referenceExpression == null) {
      return false;
    }

    PsiReference[] references = referenceExpression.getReferences();

    PsiElement resolvedReference = resolveReference(references);

    List<String> canonicalNames = new LinkedList<>();

    if (resolvedReference instanceof KtProperty) {
      KtProperty property = (KtProperty) resolvedReference;
      canonicalNames.addAll(getCanonicalNamesFromProperty(property));
    } else if (resolvedReference instanceof KtParameter) {
      KtParameter parameter = (KtParameter) resolvedReference;
      canonicalNames.addAll(getCanonicalNamesFromParameter(parameter));
    }

    return PsiHelper.isAnyCanonicalTextContainedInTheCanonicalNames(canonicalNames, state
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
   * Returns a list of canonical names from a property
   *
   * @param property the property
   * @return the list of canonical names
   */
  private static List<String> getCanonicalNamesFromProperty(KtProperty property) {

    List<String> canonicalNames = new LinkedList<>();

    KtTypeReference typeReference = property.getTypeReference();

    if (typeReference != null) {
      canonicalNames.add(typeReference.getText());
    } else {
      canonicalNames.add(getCanonicalNameFromPropertyCallExpression(property));
      canonicalNames.add(getCanonicalNameFromPropertyDotQualifiedExpression(property));
    }

    return canonicalNames;
  }

  /**
   * Returns a list of canonical names from a parameter
   *
   * @param parameter the parameter
   * @return the list of canonical names
   */
  private static List<String> getCanonicalNamesFromParameter(KtParameter parameter) {

    KtTypeReference typeReference = parameter.getTypeReference();

    if (typeReference == null) {
      return Collections.emptyList();
    }

    String text = typeReference.getText();
    if (text.contains(".")) {
      return Collections.singletonList(text);
    }

    KtTypeElement typeElement = typeReference.getTypeElement();

    if (typeElement == null) {
      return Collections.emptyList();
    }

    KtReferenceExpression referenceExpression = null;

    PsiElement[] children = typeElement.getChildren();
    for (PsiElement child : children) {
      if (child instanceof KtReferenceExpression) {
        referenceExpression = (KtReferenceExpression) child;
      }
    }

    if (referenceExpression == null) {
      return Collections.emptyList();
    }

    PsiReference[] references = referenceExpression.getReferences();

    PsiElement resolvedReference = resolveReference(references);

    if (!(resolvedReference instanceof KtClass)) {
      return Collections.emptyList();
    }
    KtClass type = (KtClass) resolvedReference;
    StringBuilder canonicalName = new StringBuilder();
    String packageName = type.getContainingKtFile().getPackageFqName().asString();
    if (!packageName.isEmpty()) {
      canonicalName.append(packageName);
      canonicalName.append(".");
    }
    canonicalName.append(typeReference.getText());
    return Collections.singletonList(canonicalName.toString());
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
   * Gets the class canonical name from the property dot qualified expression used to initialize it
   *
   * @param property the property
   * @return the class canonical name or null if it could not be found
   */
  private static String getCanonicalNameFromPropertyDotQualifiedExpression(KtProperty property) {

    KtDotQualifiedExpression dotQualifiedExpression = null;

    PsiElement[] children = property.getChildren();

    for (PsiElement child : children) {
      if (child instanceof KtDotQualifiedExpression) {
        dotQualifiedExpression = (KtDotQualifiedExpression) child;
        break;
      }
    }

    if (dotQualifiedExpression == null) {
      return null;
    }

    KtExpression selectorExpression = dotQualifiedExpression.getSelectorExpression();

    if (selectorExpression == null) {
      return null;
    }

    KtNameReferenceExpression nameReferenceExpression = null;

    children = selectorExpression.getChildren();

    for (PsiElement child : children) {
      if (child instanceof KtNameReferenceExpression) {
        nameReferenceExpression = (KtNameReferenceExpression) child;
        break;
      }
    }

    if (nameReferenceExpression == null) {
      return null;
    }

    PsiReference[] references = nameReferenceExpression.getReferences();

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
