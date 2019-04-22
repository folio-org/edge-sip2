package org.folio.edge.sip2.modules;

import static org.folio.edge.sip2.parser.Command.CHECKIN_RESPONSE;
import static org.folio.edge.sip2.parser.Command.LOGIN_RESPONSE;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import freemarker.template.Template;
import java.time.Clock;
import javax.inject.Named;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.repositories.FolioResourceProvider;
import org.folio.edge.sip2.repositories.IRequestData;
import org.folio.edge.sip2.repositories.IResourceProvider;
import org.folio.edge.sip2.repositories.LoginRepository;

/**
 * Module to bind dependencies for {@code CirculationRespository}.
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
    bind(LoginRepository.class);
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
}
