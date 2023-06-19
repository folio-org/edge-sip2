package org.folio.edge.sip2.repositories.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatronPasswordVerificationRecords {
  private final ExtendedUser extendedUser;
  private final Boolean passwordVerified;
  private final List<String> errorMessages;

  private PatronPasswordVerificationRecords(Builder builder) {
    this.extendedUser = builder.extendedUser;
    this.passwordVerified = builder.passwordVerified;
    this.errorMessages = builder.errorMessages == null ? null :
      Collections.unmodifiableList(new ArrayList<>(builder.errorMessages));
  }

  /**
   * Get the User object.
   * @return the User object
   */
  public User getUser() {
    if (extendedUser != null) {
      return extendedUser.getUser();
    } else {
      return null;
    }
  }

  /**
   * Get the ExtendedUser object.
   * @return the ExtendedUser object
   */
  public ExtendedUser getExtendedUser() {
    return extendedUser;
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
    private ExtendedUser extendedUser;
    private Boolean passwordVerified;
    private List<String> errorMessages;

    public Builder extendedUser(ExtendedUser extendedUser) {
      this.extendedUser = extendedUser;
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
