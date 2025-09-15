package org.folio.edge.sip2.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Sip2TestConfig {

  /**
   * The name of the file with the configuration.
   *
   * @return the file path relative to the resources folder
   */
  String value();
}
