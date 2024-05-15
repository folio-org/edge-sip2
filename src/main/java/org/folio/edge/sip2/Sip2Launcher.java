package org.folio.edge.sip2;

import java.security.Security;

import io.vertx.core.Launcher;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

public class Sip2Launcher extends Launcher {
  public static void main(String[] args) {
    Security.addProvider(new BouncyCastleFipsProvider());
    new Sip2Launcher().dispatch(args);
  }
}
