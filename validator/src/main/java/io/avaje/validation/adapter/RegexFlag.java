package io.avaje.validation.adapter;

/** Enumeration of regex flags corresponding to java.util.regex.Pattern flags. */
public enum RegexFlag {

  /**
   * Enables Unix lines mode.
   *
   * @see java.util.regex.Pattern#UNIX_LINES
   */
  UNIX_LINES(java.util.regex.Pattern.UNIX_LINES),

  /**
   * Enables case-insensitive matching.
   *
   * @see java.util.regex.Pattern#CASE_INSENSITIVE
   */
  CASE_INSENSITIVE(java.util.regex.Pattern.CASE_INSENSITIVE),

  /**
   * Permits whitespace and comments in pattern.
   *
   * @see java.util.regex.Pattern#COMMENTS
   */
  COMMENTS(java.util.regex.Pattern.COMMENTS),

  /**
   * Enables multiline mode.
   *
   * @see java.util.regex.Pattern#MULTILINE
   */
  MULTILINE(java.util.regex.Pattern.MULTILINE),

  /**
   * Enables dotall mode.
   *
   * @see java.util.regex.Pattern#DOTALL
   */
  DOTALL(java.util.regex.Pattern.DOTALL),

  /**
   * Enables Unicode-aware case folding.
   *
   * @see java.util.regex.Pattern#UNICODE_CASE
   */
  UNICODE_CASE(java.util.regex.Pattern.UNICODE_CASE),

  /**
   * Enables canonical equivalence.
   *
   * @see java.util.regex.Pattern#CANON_EQ
   */
  CANON_EQ(java.util.regex.Pattern.CANON_EQ);

  // JDK flag value
  private final int value;

  RegexFlag(int value) {
    this.value = value;
  }

  /** @return flag value as defined in {@link java.util.regex.Pattern} */
  public int getValue() {
    return value;
  }
}
