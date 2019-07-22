package org.folio.edge.sip2.repositories.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatronPasswordVerificationRecords {
  private final User user;
  private final Boolean passwordVerified;
  private final List<String> errorMessages;

  private PatronPasswordVerificationRecords(Builder builder) {
    this.user = builder.user;
    this.passwordVerified = builder.passwordVerified;
    this.errorMessages = builder.errorMessages == null ? null :
      Collections.unmodifiableList(new ArrayList<>(builder.errorMessages));
  }

  public User getUser() {
    return user;
  }

  public Boolean getPasswordVerified() {
    return passwordVerified;
  }

  public List<String> getErrorMessages() {
    return errorMessages;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private User user;
    private Boolean passwordVerified;
    private List<String> errorMessages;

    public Builder user(User user) {
      this.user = user;
      return this;
    }

    public Builder passwordVerified(Boolean passwordVerified) {
      this.passwordVerified = passwordVerified;
      return this;
    }

    public Builder errorMessages(List<String> errorMessages) {
      this.errorMessages = errorMessages;
      return this;
    }

    public PatronPasswordVerificationRecords build() {
      return new PatronPasswordVerificationRecords(this);
    }
  }
}
