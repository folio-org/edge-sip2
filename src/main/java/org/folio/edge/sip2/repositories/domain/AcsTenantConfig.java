package org.folio.edge.sip2.repositories.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcsTenantConfig {

  private Boolean statusUpdateOk;
  private Boolean offlineOk;
  private Boolean alwaysCheckPatronPassword;
  private Boolean usePinForPatronVerification;
  private Boolean patronPasswordVerificationRequired;
  private String invalidCheckinStatuses;
  private List<SupportedMessage> supportedMessages;

  /**
   * Returns a set of supported messages based on the configured supported messages list.
   * Filters messages where {@code isSupported} is "Y" and maps them to set of {@link Messages}.
   *
   * @return a {@link Set} of {@link Messages} that are supported
   */
  public Set<Messages> getSupportedMessagesSet() {
    if (supportedMessages == null) {
      return Collections.emptySet();
    }

    return supportedMessages.stream()
        .filter(el -> StringUtils.equalsIgnoreCase(el.getIsSupported(), "Y"))
        .map(el -> Messages.fromStringSafe(el.getMessageName()))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  /**
   * Returns a list of invalid checkin statuses by splitting the string on commas.
   *
   * @return a {@link List} of {@link String} representing invalid checkin statuses
   */
  public List<String> getInvalidCheckinStatusesList() {
    return StringUtils.isBlank(invalidCheckinStatuses)
        ? Collections.emptyList()
        : Arrays.asList(invalidCheckinStatuses.split(","));
  }

  /**
   * Sets statusUpdateOk for {@link AcsTenantConfig} and returns {@link AcsTenantConfig}.
   *
   * @return this {@link AcsTenantConfig} with new statusUpdateOk value
   */
  public AcsTenantConfig statusUpdateOk(Boolean statusUpdateOk) {
    this.statusUpdateOk = statusUpdateOk;
    return this;
  }

  /**
   * Sets offlineOk for {@link AcsTenantConfig} and returns {@link AcsTenantConfig}.
   *
   * @return this {@link AcsTenantConfig} with new offlineOk value
   */
  public AcsTenantConfig offlineOk(Boolean offlineOk) {
    this.offlineOk = offlineOk;
    return this;
  }

  /**
   * Sets alwaysCheckPatronPassword for {@link AcsTenantConfig} and returns it.
   *
   * @return this {@link AcsTenantConfig} with new alwaysCheckPatronPassword value
   */
  public AcsTenantConfig alwaysCheckPatronPassword(Boolean alwaysCheckPatronPassword) {
    this.alwaysCheckPatronPassword = alwaysCheckPatronPassword;
    return this;
  }

  /**
   * Sets usePinForPatronVerification for {@link AcsTenantConfig} and returns it.
   *
   * @return this {@link AcsTenantConfig} with new usePinForPatronVerification value
   */
  public AcsTenantConfig usePinForPatronVerification(Boolean usePinForPatronVerification) {
    this.usePinForPatronVerification = usePinForPatronVerification;
    return this;
  }

  /**
   * Sets patronPasswordVerificationRequired for {@link AcsTenantConfig} and returns it.
   *
   * @return this {@link AcsTenantConfig} with new patronPasswordVerificationRequired value
   */
  public AcsTenantConfig patronPasswordVerificationRequired(
      Boolean patronPasswordVerificationRequired) {
    this.patronPasswordVerificationRequired = patronPasswordVerificationRequired;
    return this;
  }

  /**
   * Sets invalidCheckinStatuses for {@link AcsTenantConfig} and returns {@link AcsTenantConfig}.
   *
   * @return this {@link AcsTenantConfig} with new invalidCheckinStatuses value
   */
  public AcsTenantConfig invalidCheckinStatuses(String invalidCheckinStatuses) {
    this.invalidCheckinStatuses = invalidCheckinStatuses;
    return this;
  }

  /**
   * Sets supportedMessages for {@link AcsTenantConfig} and returns {@link AcsTenantConfig}.
   *
   * @return this {@link AcsTenantConfig} with new supportedMessages value
   */
  public AcsTenantConfig supportedMessages(
      List<SupportedMessage> supportedMessages) {
    this.supportedMessages = supportedMessages;
    return this;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor(staticName = "of")
  public static class SupportedMessage {
    private String messageName;
    private String isSupported;

    /**
     * Sets messageName for {@link SupportedMessage} and returns {@link SupportedMessage}.
     *
     * @return this {@link SupportedMessage} with new messageName value
     */
    public SupportedMessage messageName(String messageName) {
      this.messageName = messageName;
      return this;
    }

    /**
     * Sets isSupported for {@link SupportedMessage} and returns {@link SupportedMessage}.
     *
     * @return this {@link SupportedMessage} with new isSupported value
     */
    public SupportedMessage isSupported(String isSupported) {
      this.isSupported = isSupported;
      return this;
    }
  }
}
