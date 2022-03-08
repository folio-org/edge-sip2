package org.folio.edge.sip2.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.vertx.ext.web.client.WebClient;

/**
 * Module for creating a {@code FolioResourceProvider} via Dependency injection.
 *
 * @author mreno-EBSCO
 *
 */
public class FolioResourceProviderModule extends AbstractModule {
  private final String okapiUrl;
  private final WebClient webClient;

  /**
   * Build a module for dependency injection.
   * @param okapiUrl the okapi url
   * @param webClient the instance of WebClient
   */
  public FolioResourceProviderModule(String okapiUrl, WebClient webClient) {
    this.okapiUrl = okapiUrl;
    this.webClient = webClient;
  }

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("okapiUrl")).toInstance(okapiUrl);
    bind(WebClient.class).annotatedWith(Names.named("webClient")).toInstance(webClient);
  }
}
