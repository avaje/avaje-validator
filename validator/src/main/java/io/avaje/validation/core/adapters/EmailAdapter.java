package io.avaje.validation.core.adapters;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.avaje.validation.adapter.RegexFlag;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext.Message;
import io.avaje.validation.adapter.ValidationRequest;

/* most of this was written by
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Guillaume Smet
 */
final class EmailAdapter implements ValidationAdapter<CharSequence> {

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

  private final Message message;
  private final Predicate<String> pattern;

  @SuppressWarnings("unchecked")
  public EmailAdapter(Message message, Map<String, Object> attributes) {
    this.message = message;
    int flags = 0;
    var regex = (String) attributes.get("regexp");
    if (regex == null) {
      regex = ".*";
    }

    List<RegexFlag> flags1 = (List<RegexFlag>) attributes.get("flags");
    if (flags1 != null) {
      for (final var flag : flags1) {
        flags |= flag.getValue();
      }
    }
    this.pattern = Pattern.compile(regex, flags).asMatchPredicate().negate();
  }

  @Override
  public boolean validate(CharSequence value, ValidationRequest req, String propertyName) {
    if (value == null || value.length() == 0) {
      return true;
    }

    // cannot split email string at @ as it can be a part of quoted local part of email.
    // so we need to split at a position of last @ present in the string:
    final String stringValue = value.toString();
    final int splitPosition = stringValue.lastIndexOf('@');

    // need to check if
    if (splitPosition < 0) {
      req.addViolation(message, propertyName);
      return false;
    }

    final String localPart = stringValue.substring(0, splitPosition);
    final String domainPart = stringValue.substring(splitPosition + 1);

    if (!isValidEmailLocalPart(localPart)
        || !DomainNameUtil.isValidEmailDomainAddress(domainPart)
        || pattern.test(value.toString())) {

      req.addViolation(message, propertyName);
      return false;
    }

    return true;
  }

  private boolean isValidEmailLocalPart(String localPart) {
    if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
      return false;
    }
    return LOCAL_PART_PATTERN.matcher(localPart).matches();
  }
}
