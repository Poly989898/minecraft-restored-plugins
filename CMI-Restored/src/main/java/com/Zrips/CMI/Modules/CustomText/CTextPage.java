package com.Zrips.CMI.Modules.CustomText;

import java.util.ArrayList;
import java.util.List;

public class CTextPage {
  private String label = null;
  private List<String> lines = new ArrayList<>();

  public CTextPage() {}

  public CTextPage(List<String> list) {
    this.lines = list;
  }

  public String getLabel() {
    return this.label;
  }

  public CTextPage setLabel(String string) {
    this.label = string;
    return this;
  }

  public List<String> getLines() {
    return this.lines;
  }

  public CTextPage setLines(List<String> list) {
    this.lines = list;
    return this;
  }
}
