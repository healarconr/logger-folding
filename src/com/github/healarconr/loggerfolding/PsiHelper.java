package com.github.healarconr.loggerfolding;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to determine if a PsiElement represents a logger method call and to obtain the text range of a PsiElement from its
 * start offset to the immediate following semicolon.
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public final class PsiHelper {

	private PsiHelper() {

		super();
	}

	/**
	 * Determines if a PsiElement represents a logger method call by evaluating the qualifier type
	 *
	 * @param element the element
	 * @return true if the qualifier type is one of the logger classes defined in {@link LoggerClasses}
	 */
	public static boolean isALoggerMethodCall(@NotNull PsiElement element) {

		if (element instanceof PsiMethodCallExpression) {
			PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) element;
			PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
			PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
			if (qualifierExpression != null) {
				PsiType type = qualifierExpression.getType();
				return type != null && LoggerClasses.contains(type.getCanonicalText());
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
	public static TextRange getTextRange(@NotNull PsiElement element) {

		TextRange textRange = element.getTextRange();
		PsiElement semicolon = findSemicolonNextTo(element);
		if (semicolon == null) {
			return textRange;
		}
		else {
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
