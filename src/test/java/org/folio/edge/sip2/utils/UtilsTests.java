package org.folio.edge.sip2.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UtilsTests {

  @Test
  public void testParseQueryString() {

    Map<String, String> queryStringParams = new LinkedHashMap();
    queryStringParams.put("key1", "value1");
    queryStringParams.put("key2", "");
    queryStringParams.put("key3", "value3");

    final String delimeter = " AND ";
    final String equalDelimeter = "==";

    String expectedString = "key1==value1 AND key3==value3";

    String output = Utils.parseQueryString(queryStringParams, delimeter, equalDelimeter);
    assertEquals(expectedString, output);
  }

  @Test
  public void testParseQueryStringWithEmptyFirstKeyAndAmpercentSeparator() {

    Map<String, String> queryStringParams = new LinkedHashMap();
    queryStringParams.put("key1", "");
    queryStringParams.put("key2", "");
    queryStringParams.put("key3", "value3");
    queryStringParams.put("key4", "");
    queryStringParams.put("key5", "value5");
    queryStringParams.put("key6", "");

    final String delimeter = "&";
    final String equalDelimeter = "=";

    String expectedString = "key3=value3&key5=value5";

    String output = Utils.parseQueryString(queryStringParams, delimeter, equalDelimeter);
    assertEquals(expectedString, output);
  }

  @Test
  public void testIsStringNullOrEmpty() {
    assertTrue(Utils.isStringNullOrEmpty(null));
    assertTrue(Utils.isStringNullOrEmpty(""));
  }
}
