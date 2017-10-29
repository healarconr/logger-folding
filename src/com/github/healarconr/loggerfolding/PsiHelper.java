package com.github.healarconr.loggerfolding;

import java.util.List;
import java.util.Set;

/**
 * Helper class to determine if a canonical text is contained in a canonical names set
 */
final class PsiHelper {

  private PsiHelper() {

    super();
  }

  /**
   * Determines if any of the canonical texts is contained in the canonical names set
   *
   * @param canonicalTexts    the canonical texts
   * @param canonicalNamesSet the canonical names set
   * @return true if any of the canonical texts is contained in the canonical names set, false otherwise
   */
  static boolean isAnyCanonicalTextContainedInTheCanonicalNames(List<String> canonicalTexts, Set<String> canonicalNamesSet) {
    for (String canonicalText : canonicalTexts) {
      if (canonicalNamesSet.contains(canonicalText)) {
        return true;
      }
    }
    return false;
  }

}
