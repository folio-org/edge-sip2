package org.folio.edge.sip2.modules;

import static org.folio.edge.sip2.parser.Command.CHECKIN;
import static org.folio.edge.sip2.parser.Command.CHECKOUT;
import static org.folio.edge.sip2.parser.Command.END_PATRON_SESSION;
import static org.folio.edge.sip2.parser.Command.FEE_PAID;
import static org.folio.edge.sip2.parser.Command.ITEM_INFORMATION;
import static org.folio.edge.sip2.parser.Command.LOGIN;
import static org.folio.edge.sip2.parser.Command.PATRON_INFORMATION;
import static org.folio.edge.sip2.parser.Command.PATRON_STATUS_REQUEST;
import static org.folio.edge.sip2.parser.Command.RENEW;
import static org.folio.edge.sip2.parser.Command.RENEW_ALL;
import static org.folio.edge.sip2.parser.Command.REQUEST_ACS_RESEND;
import static org.folio.edge.sip2.parser.Command.REQUEST_SC_RESEND;
import static org.folio.edge.sip2.parser.Command.SC_STATUS;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import org.folio.edge.sip2.handlers.ACSResendHandler;
import org.folio.edge.sip2.handlers.CheckinHandler;
import org.folio.edge.sip2.handlers.CheckoutHandler;
import org.folio.edge.sip2.handlers.EndPatronSessionHandler;
import org.folio.edge.sip2.handlers.FeePaidHandler;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;
import org.folio.edge.sip2.handlers.InvalidMessageHandler;
import org.folio.edge.sip2.handlers.ItemInformationHandler;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.handlers.PatronInformationHandler;
import org.folio.edge.sip2.handlers.PatronStatusHandler;
import org.folio.edge.sip2.handlers.RenewAllHandler;
import org.folio.edge.sip2.handlers.RenewHandler;
import org.folio.edge.sip2.handlers.SCStatusHandler;
import org.folio.edge.sip2.parser.Command;

public class RequestHandlerModule extends AbstractModule {

  @Override
  protected void configure() {
    var mapbinder = MapBinder.newMapBinder(binder(), Command.class, ISip2RequestHandler.class);
    mapbinder.addBinding(CHECKOUT).to(CheckoutHandler.class);
    mapbinder.addBinding(CHECKIN).to(CheckinHandler.class);
    mapbinder.addBinding(SC_STATUS).to(SCStatusHandler.class);
    mapbinder.addBinding(REQUEST_ACS_RESEND).to(ACSResendHandler.class);
    mapbinder.addBinding(LOGIN).to(LoginHandler.class);
    mapbinder.addBinding(PATRON_INFORMATION).to(PatronInformationHandler.class);
    mapbinder.addBinding(PATRON_STATUS_REQUEST).to(PatronStatusHandler.class);
    mapbinder.addBinding(REQUEST_SC_RESEND).to(InvalidMessageHandler.class);
    mapbinder.addBinding(END_PATRON_SESSION).to(EndPatronSessionHandler.class);
    mapbinder.addBinding(FEE_PAID).to(FeePaidHandler.class);
    mapbinder.addBinding(ITEM_INFORMATION).to(ItemInformationHandler.class);
    mapbinder.addBinding(RENEW).to(RenewHandler.class);
    mapbinder.addBinding(RENEW_ALL).to(RenewAllHandler.class);
  }
}
