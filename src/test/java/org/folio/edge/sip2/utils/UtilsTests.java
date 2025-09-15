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

  @CsvSource(nullValues = "null", value = { "30,30", "unknown,10" })
  @ParameterizedTest
  void getEnvOrDefault_positive_integerValue(String systemProperty, int expected) {
    var defaultValue = 10;
    var propertyName = "test";

    if (systemProperty != null) {
      System.setProperty(propertyName, systemProperty);
    }

    var result = Utils.getEnvOrDefault(propertyName, "TEST", defaultValue, Integer::parseInt);
    assertEquals(expected, result);
  }

  @Test
  void getEnvOrDefault_positive_propertyNotFound() {
    var result = Utils.getEnvOrDefault("test", "TEST", 10, Integer::parseInt);
    assertEquals(10, result);
  }
}
