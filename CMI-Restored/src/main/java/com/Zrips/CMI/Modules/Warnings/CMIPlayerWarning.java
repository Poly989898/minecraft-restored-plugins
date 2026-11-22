package com.Zrips.CMI.Modules.Warnings;

public class CMIPlayerWarning {
  private String givenBy;
  private long givenAt = 0L;
  private String reason;
  private CMIWarningCategory category;

  public String getGivenBy() {
    return this.givenBy;
  }

  public void setGivenBy(String string) {
    this.givenBy = string;
  }

  public Long getGivenAt() {
    return this.givenAt;
  }

  public void setGivenAt(Long newGivenAt) {
    this.givenAt = newGivenAt == null ? 0L : newGivenAt;
  }

  public String getReason() {
    return this.reason;
  }

  public void setReason(String string) {
    this.reason = string;
  }

  public CMIWarningCategory getCategory() {
    return this.category;
  }

  public void setCategory(CMIWarningCategory cmiWarningCategory) {
    this.category = cmiWarningCategory;
  }
}
