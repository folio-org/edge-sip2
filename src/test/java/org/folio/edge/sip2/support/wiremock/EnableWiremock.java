package org.folio.edge.sip2.support.wiremock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({ WiremockContainerExtension.class, WiremockStubExtension.class})
public @interface EnableWiremock {
  boolean https() default false;
}
