package org.folio.edge.sip2.cache;

import org.folio.okapi.common.refreshtoken.tokencache.TenantUserCache;

public class TokenCacheFactory {

  private TokenCacheFactory() { }

  static TenantUserCache instance;

  public static void initialize(int capacity) {
    instance = new TenantUserCache(capacity);
  }

  public static TenantUserCache get() {
    return instance;
  }
}
