package org.folio.edge.sip2.modules;

import static org.folio.edge.sip2.parser.Command.CHECKIN_RESPONSE;
import static org.folio.edge.sip2.parser.Command.CHECKOUT_RESPONSE;
import static org.folio.edge.sip2.parser.Command.END_SESSION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.FEE_PAID_RESPONSE;
import static org.folio.edge.sip2.parser.Command.ITEM_INFORMATION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.LOGIN_RESPONSE;
import static org.folio.edge.sip2.parser.Command.PATRON_INFORMATION_RESPONSE;
import static org.folio.edge.sip2.parser.Command.PATRON_STATUS_RESPONSE;
import static org.folio.edge.sip2.parser.Command.RENEW_ALL_RESPONSE;
import static org.folio.edge.sip2.parser.Command.RENEW_RESPONSE;
import static org.folio.edge.sip2.parser.Command.SC_STATUS;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import freemarker.template.Template;
import jakarta.inject.Named;
import java.time.Clock;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.FeeFinesRepository;
import org.folio.edge.sip2.repositories.FolioResourceProvider;
import org.folio.edge.sip2.repositories.IRequestData;
import org.folio.edge.sip2.repositories.IResourceProvider;
import org.folio.edge.sip2.repositories.ItemRepository;
import org.folio.edge.sip2.repositories.LoginRepository;
import org.folio.edge.sip2.repositories.PasswordVerifier;
import org.folio.edge.sip2.repositories.UsersRepository;
import org.folio.edge.sip2.service.config.TenantConfigurationService;
import org.folio.edge.sip2.service.tenant.IpTenantResolver;
import org.folio.edge.sip2.service.tenant.LocationCodeTenantResolver;
import org.folio.edge.sip2.service.tenant.PortTenantResolver;
import org.folio.edge.sip2.service.tenant.Sip2TenantResolver;
import org.folio.edge.sip2.service.tenant.TenantResolver;
import org.folio.edge.sip2.service.tenant.UsernamePrefixTenantResolver;

/**
 * Module to bind dependencies for injection.
 *
 * @author mreno-EBSCO
 *
 */
public class ApplicationModule extends AbstractModule {

  @Override
  protected void configure() {
    var tenantResolverBinder = Multibinder.newSetBinder(binder(), TenantResolver.class);
    tenantResolverBinder.addBinding().to(IpTenantResolver.class);
    tenantResolverBinder.addBinding().to(PortTenantResolver.class);
    tenantResolverBinder.addBinding().to(UsernamePrefixTenantResolver.class);
    tenantResolverBinder.addBinding().to(LocationCodeTenantResolver.class);
    bind(Sip2TenantResolver.class).asEagerSingleton();

    bind(new TypeLiteral<IResourceProvider<IRequestData>>() {})
        .to(FolioResourceProvider.class).asEagerSingleton();

    bind(TenantConfigurationService.class).asEagerSingleton();
    bind(Clock.class).toInstance(Clock.systemUTC());
    bind(ConfigurationRepository.class);
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
  @Named("patronStatusResponse")
  Template providePatronStatusResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(PATRON_STATUS_RESPONSE);
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

  @Provides
  @Named("scStatusResponse")
  Template scStatusResponseTemplate() {
    return FreemarkerRepository.getInstance().getFreemarkerTemplate(SC_STATUS);
  }
}
