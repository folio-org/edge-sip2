package org.folio.edge.sip2.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UtilsTests {

  @Test
  public void testParseQueryString() {

    Map<String, String> queryStringParams = new LinkedHashMap<>();
    queryStringParams.put("key1", "value1");
    queryStringParams.put("key2", "");
    queryStringParams.put("key3", "value3");

    final String delimeter = " AND ";
    final String equalDelimeter = "==";

    String expectedString = "key1==\"value1\" AND key3==\"value3\"";

    String output = Utils.buildQueryString(queryStringParams, delimeter, equalDelimeter);
    assertEquals(expectedString, output);
  }

  @Test
  public void testParseQueryStringWithEmptyFirstKeyAndAmpercentSeparator() {

    Map<String, String> queryStringParams = new LinkedHashMap<>();
    queryStringParams.put("key1", "");
    queryStringParams.put("key2", "");
    queryStringParams.put("key3", "value3");
    queryStringParams.put("key4", "");
    queryStringParams.put("key5", "value5?");
    queryStringParams.put("key6", "");

    final String delimeter = "&";
    final String equalDelimeter = "=";

    String expectedString = "key3=\"value3\"&key5=\"value5\\?\"";

    String output = Utils.buildQueryString(queryStringParams, delimeter, equalDelimeter);
    assertEquals(expectedString, output);
  }

  @Test
  public void testIsStringNullOrEmpty() {
    assertTrue(Utils.isStringNullOrEmpty(""));
  }

  @CsvSource({
    ",,/api?query=cql.allRecords=1",
    "1,10,/api?query=cql.allRecords=1&offset=0&limit=10",
    "5,15,/api?query=cql.allRecords=1&offset=4&limit=11",
    ",10,/api?query=cql.allRecords=1&limit=10",
    "5,,/api?query=cql.allRecords=1&offset=4",
  })
  @ParameterizedTest
  void appendQueryLimits_parameterized(Integer start, Integer end, String expected) {
    var sb = new StringBuilder("/api?query=cql.allRecords=1");
    var result = Utils.appendQueryLimits(sb, start, end);
    assertEquals(expected, result.toString());
  }
}
