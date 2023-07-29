package io.avaje.validation.core.adapters;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.core.adapters.BasicAdapters.PatternAdapter;

/* most of this was written by
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Guillaume Smet
 */
final class EmailAdapter extends PatternAdapter {

  private static final int MAX_LOCAL_PART_LENGTH = 64;

  private static final String LOCAL_PART_ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~\u0080-\uFFFF-]";
  private static final String LOCAL_PART_INSIDE_QUOTES_ATOM =
      "(?:[a-z0-9!#$%&'*.(),<>\\[\\]:;  @+/=?^_`{|}~\u0080-\uFFFF-]|\\\\\\\\|\\\\\\\")";
  /** Regular expression for the local part of an email address (everything before '@') */
  private static final Pattern LOCAL_PART_PATTERN =
      Pattern.compile(
          "(?:"
              + LOCAL_PART_ATOM
              + "+|\""
              + LOCAL_PART_INSIDE_QUOTES_ATOM
              + "+\")"
              + "(?:\\."
              + "(?:"
              + LOCAL_PART_ATOM
              + "+|\""
              + LOCAL_PART_INSIDE_QUOTES_ATOM
              + "+\")"
              + ")*",
          CASE_INSENSITIVE);

  EmailAdapter(
      ValidationContext.Message message, Set<Class<?>> groups, Map<String, Object> attributes) {
    super(message, groups, attributes, (String) attributes.getOrDefault("regexp", ".*"));
  }

  @Override
  public boolean isValid(CharSequence value) {
    if (value == null || value.length() == 0) {
      return true;
    }

    // cannot split email string at @ as it can be a part of quoted local part of email.
    // so we need to split at a position of last @ present in the string:
    final String stringValue = value.toString();
    final int splitPosition = stringValue.lastIndexOf('@');

    // need to check if
    if (splitPosition < 0) {
      return false;
    }

    final String localPart = stringValue.substring(0, splitPosition);
    final String domainPart = stringValue.substring(splitPosition + 1);

    return isValidEmailLocalPart(localPart)
        && DomainNameUtil.isValidEmailDomainAddress(domainPart)
        && !pattern.test(value.toString());
  }

  private boolean isValidEmailLocalPart(String localPart) {
    if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
      return false;
    }
    return LOCAL_PART_PATTERN.matcher(localPart).matches();
  }
}
