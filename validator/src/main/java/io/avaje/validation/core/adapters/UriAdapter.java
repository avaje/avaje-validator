package io.avaje.validation.core.adapters;

import io.avaje.validation.adapter.AbstractConstraintAdapter;
import io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest;
import io.avaje.validation.core.adapters.BasicAdapters.PatternAdapter;

import java.net.URI;

final class UriAdapter extends AbstractConstraintAdapter {

  private final String scheme;
  private final String host;
  private final int port;

  private final PatternAdapter patternAdapter;

  UriAdapter(AdapterCreateRequest request) {
    super(request);
    this.scheme = (String) request.attribute("scheme");
    this.host = (String) request.attribute("host");
    this.port = (int) request.attribute("port");

    String regexp = (String) request.attribute("regexp");
    if (!regexp.isEmpty()) {
      patternAdapter = new PatternAdapter(request);
    } else {
      patternAdapter = null;
    }
  }

  @Override
  protected boolean isValid(Object value) {
    if (value == null) {
      return true;
    }
    try {
      final var stringValue = String.valueOf(value);
      final var uri = URI.create(stringValue);
      if (!scheme.isEmpty() && !scheme.equals(uri.getScheme())) {
        return false;
      }
      if (!host.isEmpty() && !host.equals(uri.getHost())) {
        return false;
      }
      if (port > -1 && port != uri.getPort()) {
        return false;
      }
      return patternAdapter == null || patternAdapter.isValid(stringValue);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
