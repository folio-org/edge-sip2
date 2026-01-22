package org.folio.edge.sip2.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.Arrays;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@UnitTest
class CqlQueryTest {

  @ParameterizedTest
  @CsvSource({
    "title,simple,'title==\"simple\"'",
    "title,'a b','title==\"a b\"'",
    "tag,'a*b','tag==\"a\\*b\"'",
    "code,'(abc)','code==\"(abc)\"'",
    "note,'space  inside','note==\"space  inside\"'",
    "sym,'^test?','sym==\"\\^test\\?\"'"
  })
  void exactMatch_positive_parameterized(String field, String value, String expected) {
    var result = CqlQuery.exactMatch(field, value).toText();
    assertEquals(expected, URLDecoder.decode(result, UTF_8));
  }

  @Test
  void constructorIsPrivate() throws Exception {
    Constructor<CqlQuery> ctor = CqlQuery.class.getDeclaredConstructor(String.class);
    assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    ctor.setAccessible(true);
    ctor.newInstance("test");
  }

  @Test
  void exactMatchByKey_delegatesToExactMatch() {
    var query = CqlQuery.exactMatchByKey("abc");
    assertEquals("key==\"abc\"", decode(query));
  }

  @Test
  void exactMatchByScope_positive_delegatesToExactMatch() {
    var query = CqlQuery.exactMatchByScope("scope-value");
    assertEquals("scope==\"scope-value\"", decode(query));
  }

  @Test
  void exactMatchAny_positive_handlesMultipleValues() {
    var query = CqlQuery.exactMatchAny("tag", Arrays.asList("first", "second"));
    assertEquals("tag==(\"first\" or \"second\")", decode(query));
  }

  @Test
  void exactMatchAny_positive_handlesNullAndBlankValues() {
    var query = CqlQuery.exactMatchAny("tag", Arrays.asList(null, "", "   "));
    assertEquals("tag==()", decode(query));
  }

  @Test
  void exactMatchAny_positive_nullValue() {
    var query = CqlQuery.exactMatchAny("tag", null);
    assertEquals("tag==()", decode(query));
  }

  @Test
  void and_positive_wrapsQueriesByDefault() {
    var left = CqlQuery.exactMatch("a", "1");
    var right = CqlQuery.exactMatch("b", "2");
    assertEquals("(a==\"1\") and (b==\"2\")", decode(left.and(right)));
  }

  @Test
  void and_positive_supportsSimplifiedJoin() {
    var left = CqlQuery.exactMatch("a", "1");
    var right = CqlQuery.exactMatch("b", "2");
    assertEquals("a==\"1\" and b==\"2\"", decode(left.and(right, true)));
  }

  @Test
  void or_positive_wrapsQueriesByDefault() {
    var left = CqlQuery.exactMatch("a", "1");
    var right = CqlQuery.exactMatch("b", "2");
    assertEquals("(a==\"1\") or (b==\"2\")", decode(left.or(right)));
  }

  @Test
  void or_positive_supportsSimplifiedJoin() {
    var left = CqlQuery.exactMatch("a", "1");
    var right = CqlQuery.exactMatch("b", "2");
    assertEquals("a==\"1\" or b==\"2\"", decode(left.or(right, true)));
  }

  private static String decode(CqlQuery query) {
    return URLDecoder.decode(query.toText(), UTF_8);
  }
}
