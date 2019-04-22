package org.folio.edge.sip2.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.vertx.core.Vertx;

/**
 * Module for creating a {@code FolioResourceProvider} via Dependency injection.
 *
 * @author mreno-EBSCO
 *
 */
public class FolioResourceProviderModule extends AbstractModule {
  private final String okapiUrl;
  private final Vertx vertx;

  /**
   * Build a module for dependency injection.
   * @param okapiUrl the okapi url
   * @param vertx the instance of vertx
   */
  public FolioResourceProviderModule(String okapiUrl, Vertx vertx) {
    this.okapiUrl = okapiUrl;
    this.vertx = vertx;
  }

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("okapiUrl")).toInstance(okapiUrl);
    bind(Vertx.class).annotatedWith(Names.named("vertx")).toInstance(vertx);
  }
}
