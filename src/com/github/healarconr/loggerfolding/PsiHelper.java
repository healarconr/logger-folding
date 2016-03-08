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
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public final class PsiHelper {

	private PsiHelper() {

		super();
	}

	@NotNull
	public static boolean isAMethodCallOnALogger(@NotNull PsiElement element) {

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
