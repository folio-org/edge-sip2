package org.folio.edge.sip2.modules;

import static org.folio.edge.sip2.parser.Command.CHECKIN_RESPONSE;
import static org.folio.edge.sip2.parser.Command.CHECKOUT_RESPONSE;
import static org.folio.edge.sip2.parser.Command.END_SESSION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.FEE_PAID_RESPONSE;
import static org.folio.edge.sip2.parser.Command.ITEM_INFORMATION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.LOGIN_RESPONSE;
import static org.folio.edge.sip2.parser.Command.PATRON_INFORMATION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.RENEW_ALL_RESPONSE;
import static org.folio.edge.sip2.parser.Command.RENEW_RESPONSE;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import freemarker.template.Template;
import java.time.Clock;
import javax.inject.Named;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.repositories.FeeFinesRepository;
import org.folio.edge.sip2.repositories.FolioResourceProvider;
import org.folio.edge.sip2.repositories.IRequestData;
import org.folio.edge.sip2.repositories.IResourceProvider;
import org.folio.edge.sip2.repositories.ItemRepository;
import org.folio.edge.sip2.repositories.LoginRepository;
import org.folio.edge.sip2.repositories.PasswordVerifier;
import org.folio.edge.sip2.repositories.UsersRepository;

/**
 * Module to bind dependencies for injection.
 *
 * @author mreno-EBSCO
 *
 */
public class ApplicationModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(new TypeLiteral<IResourceProvider<IRequestData>>() {})
        .to(FolioResourceProvider.class).asEagerSingleton();
    bind(Clock.class).toInstance(Clock.systemUTC());
    bind(CirculationRepository.class);
    bind(FeeFinesRepository.class);
    bind(LoginRepository.class);
    bind(UsersRepository.class);
    bind(PasswordVerifier.class);
    bind(ItemRepository.class);
  }

  @Provides
  @Named("checkoutResponse")
  Template provideCheckoutResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(CHECKOUT_RESPONSE);
  }

  @Provides
  @Named("checkinResponse")
  Template provideCheckinResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(CHECKIN_RESPONSE);
  }

  @Provides
  @Named("loginResponse")
  Template provideLoginResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(LOGIN_RESPONSE);
  }

  @Provides
  @Named("patronInformationResponse")
  Template providePatronInformationResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(PATRON_INFORMATION_RESPONSE);
  }

  @Provides
  @Named("endSessionResponse")
  Template provideEndSessionResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(END_SESSION_RESPONSE);
  }

  @Provides
  @Named("itemInformationResponse")
  Template provideItemInformationResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(ITEM_INFORMATION_RESPONSE);
  }

  @Provides
  @Named("renewResponse")
  Template renewResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(RENEW_RESPONSE);
  }

  @Provides
  @Named("renewAllResponse")
  Template renewAllResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(RENEW_ALL_RESPONSE);
  }

  @Provides
  @Named("feePaidResponse")
  Template feePaidResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(FEE_PAID_RESPONSE);
  }



}
