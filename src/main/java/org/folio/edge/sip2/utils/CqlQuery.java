package org.folio.edge.sip2.utils;

import static java.util.stream.Collectors.joining;
import static org.folio.util.StringUtil.cqlEncode;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.util.PercentCodec;
import org.folio.util.StringUtil;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, staticName = "of")
public class CqlQuery {

  private final String query;

  /**
   * Creates a CqlQuery for an exact match on the given parameter and value.
   *
   * @param param - the CQL field to match
   * @param value - the value to match
   * @return a new CqlQuery representing the exact match
   */
  public static CqlQuery exactMatch(String param, String value) {
    return new CqlQuery("%s==%s".formatted(param, cqlEncode(value)));
  }

  /**
   * Creates a CqlQuery for an exact match by key and provided value.
   *
   * @param value - the value to match
   * @return a new CqlQuery representing the exact match
   */
  public static CqlQuery exactMatchByKey(String value) {
    return exactMatch("key", value);
  }

  /**
   * Creates a CqlQuery for an exact match by scope and provided value.
   *
   * @param value - the value to match
   * @return a new CqlQuery representing the exact match
   */
  public static CqlQuery exactMatchByScope(String value) {
    return exactMatch("scope", value);
  }

  /**
   * Creates a CqlQuery for an exact match on the given parameter and list of values.
   *
   * @param param  - the CQL field to match
   * @param values - the values to match
   * @return a new CqlQuery representing the exact match
   */
  public static CqlQuery exactMatchAny(String param, List<String> values) {
    var listValues = values != null ? values : List.<String>of();
    var stringValues = listValues.stream()
        .filter(StringUtils::isNotBlank)
        .map(StringUtil::cqlEncode)
        .collect(joining(" or "));
    return new CqlQuery("%s==(%s)".formatted(param, stringValues));
  }

  /**
   * Combines this CqlQuery with another using an AND operation.
   *
   * @param query the CqlQuery to combine with this one
   * @return a new CqlQuery representing the logical AND of both queries
   */
  public CqlQuery and(CqlQuery query) {
    return and(query, false);
  }

  /**
   * Combines this CqlQuery with another using an AND operation.
   * Optionally uses simplified joining without parentheses.
   *
   * @param query          - the CqlQuery to combine with this one
   * @param simplifiedJoin - if true, omits parentheses around queries
   * @return a new CqlQuery representing the logical AND of both queries
   */
  public CqlQuery and(CqlQuery query, boolean simplifiedJoin) {
    return simplifiedJoin
        ? new CqlQuery("%s and %s".formatted(this.query, query.query))
        : new CqlQuery("(%s) and (%s)".formatted(this.query, query.query));
  }

  /**
   * Combines this CqlQuery with another using an AND operation.
   *
   * @param query the CqlQuery to combine with this one
   * @return a new CqlQuery representing the logical AND of both queries
   */
  public CqlQuery or(CqlQuery query) {
    return or(query, false);
  }

  /**
   * Combines this CqlQuery with another using an AND operation.
   * Optionally uses simplified joining without parentheses.
   *
   * @param query          - the CqlQuery to combine with this one
   * @param simplifiedJoin - if true, omits parentheses around queries
   * @return a new CqlQuery representing the logical AND of both queries
   */
  public CqlQuery or(CqlQuery query, boolean simplifiedJoin) {
    return simplifiedJoin
        ? new CqlQuery("%s or %s".formatted(this.query, query.query))
        : new CqlQuery("(%s) or (%s)".formatted(this.query, query.query));
  }

  /**
   * Returns the encoded CQL query string representation of this query.
   *
   * @return encoded CQL query string
   */
  public String toText() {
    return PercentCodec.encodeAsString(this.query);
  }
}
