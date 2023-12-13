package org.folio.edge.sip2.domain.messages;

import java.time.OffsetDateTime;

public class PatronAccountInfo {
  private String id;
  private String feeFineType;
  private Double feeFineAmount;
  private Double feeFineRemaining;
  private Double feeFinePaid;
  private String itemBarcode;
  private OffsetDateTime feeCreationDate;
  private String feeFineId;
  private String feeDescription;
  private String itemTitle;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getItemTitle() {
    return itemTitle;
  }

  public void setItemTitle(String itemTitle) {
    this.itemTitle = itemTitle;
  }

  public String getFeeFineId() {
    return feeFineId;
  }

  public void setFeeFineId(String feeFineId) {
    this.feeFineId = feeFineId;
  }

  public String getFeeFineType() {
    return feeFineType;
  }

  public void setFeeFineType(String feeFineType) {
    this.feeFineType = feeFineType;
  }

  public Double getFeeFineAmount() {
    return feeFineAmount;
  }

  public Double getFeeFineRemaining() {
    return feeFineRemaining;
  }

  public void setFeeFineAmount(Double feeFineAmount) {
    this.feeFineAmount = feeFineAmount;
  }

  public void setFeeFineRemaining(Double feeFineRemaining) {
    this.feeFineRemaining = feeFineRemaining;
  }

  public String getItemBarcode() {
    return itemBarcode;
  }

  public void setItemBarcode(String itemBarcode) {
    this.itemBarcode = itemBarcode;
  }

  public OffsetDateTime getFeeCreationDate() {
    return feeCreationDate;
  }

  public void setFeeCreationDate(OffsetDateTime feeCreationDate) {
    this.feeCreationDate = feeCreationDate;
  }

  public String getFeeDescription() {
    return feeDescription;
  }

  public void setFeeDescription(String feeDescription) {
    this.feeDescription = feeDescription;
  }

  public Double getFeeFinePaid() {
    return feeFinePaid;
  }

  public void setFeeFinePaid(Double feeFinePaid) {
    this.feeFinePaid = feeFinePaid;
  }
}
