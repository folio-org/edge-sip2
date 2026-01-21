package org.folio.edge.sip2.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
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
}
