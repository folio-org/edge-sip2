package org.folio.edge.sip2.repositories.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = User.Builder.class)
public class User {
  private final String id;
  private final String barcode;
  private final String username;
  private final Boolean active;
  private final Personal personal;
  private final String externalSystemId;

  private User(Builder builder) {
    id = builder.id;
    barcode = builder.barcode;
    username = builder.username;
    active = builder.active;
    personal = builder.personal;
    externalSystemId = builder.externalSystemId;
  }

  public String toString() {
    return String.format("id: %s, barcode: %s, username: %s, active: %b, externalSystemId: %s",
        id, barcode, username, active, externalSystemId);
  }

  public String getId() {
    return id;
  }

  public String getBarcode() {
    return barcode;
  }

  public String getExtSystemId() {
    return externalSystemId;
  }

  public String getUsername() {
    return username;
  }

  public Boolean getActive() {
    return active;
  }

  public Personal getPersonal() {
    return personal;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPOJOBuilder
  public static class Builder {
    private String id;
    private String barcode;
    private String username;
    private Boolean active;
    private Personal personal;
    private String externalSystemId;

    @JsonProperty
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    @JsonProperty
    public Builder barcode(String barcode) {
      this.barcode = barcode;
      return this;
    }

    @JsonProperty
    public Builder username(String username) {
      this.username = username;
      return this;
    }

    @JsonProperty
    public Builder active(Boolean active) {
      this.active = active;
      return this;
    }

    @JsonProperty
    public Builder personal(Personal personal) {
      this.personal = personal;
      return this;
    }

    @JsonProperty
    public Builder externalSystemId(String externalSystemId) {
      this.externalSystemId = externalSystemId;
      return this;
    }

    public User build() {
      return new User(this);
    }
  }
}
