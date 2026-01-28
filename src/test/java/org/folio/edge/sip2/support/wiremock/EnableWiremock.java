package org.folio.edge.sip2.support.wiremock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Annotation to enable WireMock for a test class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({ WiremockContainerExtension.class, WiremockStubExtension.class})
public @interface EnableWiremock {
  /**
   * Indicates whether WireMock should be started in HTTPS mode.
   *
   * @return true if the test should run WireMock in HTTPS mode
   */
  boolean https() default false;
}
