package org.folio.edge.sip2.repositories.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonDeserialize(builder = Personal.Builder.class)
public class Personal {
  private final String firstName;
  private final String middleName;
  private final String lastName;
  private final List<Address> addresses;
  private final String email;
  private final String phone;

  private Personal(Builder builder) {
    firstName = builder.firstName;
    middleName = builder.middleName;
    lastName = builder.lastName;
    addresses = builder.addresses == null ? null :
      Collections.unmodifiableList(new ArrayList<>(builder.addresses));
    email = builder.email;
    phone = builder.phone;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPOJOBuilder
  public static class Builder {
    private String firstName;
    private String middleName;
    private String lastName;
    private List<Address> addresses;
    private String email;
    private String phone;

    @JsonProperty
    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    @JsonProperty
    public Builder middleName(String middleName) {
      this.middleName = middleName;
      return this;
    }

    @JsonProperty
    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    @JsonProperty
    public Builder addresses(List<Address> addresses) {
      this.addresses = addresses;
      return this;
    }

    @JsonProperty
    public Builder email(String email) {
      this.email = email;
      return this;
    }

    @JsonProperty
    public Builder phone(String phone) {
      this.phone = phone;
      return this;
    }

    public Personal build() {
      return new Personal(this);
    }
  }
}
