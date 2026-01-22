package org.folio.edge.sip2.support.wiremock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Language;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WiremockStubs {

  /**
   * Wiremock stub values.
   *
   * @return Wiremock stub file paths
   */
  @Language(value = "file-reference")
  String[] value() default {};
}
