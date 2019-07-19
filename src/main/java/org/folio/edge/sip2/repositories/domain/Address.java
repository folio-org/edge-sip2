package org.folio.edge.sip2.repositories.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Address.Builder.class)
public class Address {
  private final String addressLine1;
  private final String addressLine2;
  private final String city;
  private final String region;
  private final String postalCode;
  private final String countryId;
  private final Boolean primaryAddress;

  private Address(Builder builder) {
    addressLine1 = builder.addressLine1;
    addressLine2 = builder.addressLine2;
    city = builder.city;
    region = builder.region;
    postalCode = builder.postalCode;
    countryId = builder.countryId;
    primaryAddress = builder.primaryAddress;
  }

  public String getAddressLine1() {
    return addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public String getCity() {
    return city;
  }

  public String getRegion() {
    return region;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getCountryId() {
    return countryId;
  }

  public Boolean getPrimaryAddress() {
    return primaryAddress;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPOJOBuilder
  public static class Builder {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String region;
    private String postalCode;
    private String countryId;
    private Boolean primaryAddress;

    @JsonProperty
    public Builder addressLine1(String addressLine1) {
      this.addressLine1 = addressLine1;
      return this;
    }

    @JsonProperty
    public Builder addressLine2(String addressLine2) {
      this.addressLine2 = addressLine2;
      return this;
    }

    @JsonProperty
    public Builder city(String city) {
      this.city = city;
      return this;
    }

    @JsonProperty
    public Builder region(String region) {
      this.region = region;
      return this;
    }

    @JsonProperty
    public Builder postalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    @JsonProperty
    public Builder countryId(String countryId) {
      this.countryId = countryId;
      return this;
    }

    @JsonProperty
    public Builder primaryAddress(Boolean primaryAddress) {
      this.primaryAddress = primaryAddress;
      return this;
    }

    public Address build() {
      return new Address(this);
    }
  }
}
