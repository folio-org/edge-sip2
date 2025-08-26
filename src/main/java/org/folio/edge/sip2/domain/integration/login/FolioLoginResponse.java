package org.folio.edge.sip2.domain.integration.login;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public final class FolioLoginResponse {

  /**
   * The access token issued after successful login.
   */
  private final String accessToken;

  /**
   * The expiration date and time of the access token.
   */
  private final OffsetDateTime accessTokenExpiration;

  /**
   * The expiration date and time of the refresh token.
   */
  private final OffsetDateTime refreshTokenExpiration;
}
