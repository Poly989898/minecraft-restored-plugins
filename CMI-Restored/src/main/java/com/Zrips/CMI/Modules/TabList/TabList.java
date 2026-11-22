package com.Zrips.CMI.Modules.TabList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class TabList {
  private HashMap<Integer, List<String>> headerMap = new HashMap<>();
  Integer lastHeader = 1;
  private HashMap<Integer, List<String>> footerMap = new HashMap<>();
  Integer lastFooter = 1;
  private String playerNameFormat = "";

  public String getPlayerNameFormat() {
    return this.playerNameFormat;
  }

  public void setPlayerNameFormat(String string) {
    this.playerNameFormat = string;
  }

  public void prepareNextAnimationForHeader() {
    if (this.headerMap.size() < this.lastHeader + 1) {
      this.lastHeader = 1;
    } else {
      this.lastHeader = this.lastHeader + 1;
    }
  }

  public void prepareNextAnimationForFooter() {
    if (this.footerMap.size() < this.lastFooter + 1) {
      this.lastFooter = 1;
    } else {
      this.lastFooter = this.lastFooter + 1;
    }
  }

  public List<String> getHeader() {
    if (this.headerMap.size() == 1) {
      var iterator = this.headerMap.entrySet().iterator();
      if (iterator.hasNext()) {
        Entry entry = (Entry) iterator.next();
        return (List<String>) entry.getValue();
      } else {
        return new ArrayList<>();
      }
    } else {
      return this.headerMap.get(this.lastHeader);
    }
  }

  @Deprecated
  public void setHeader(List<String> list) {
    this.headerMap.put(1, list);
  }

  public void addHeaderAnimation(List<String> list) {
    this.headerMap.put(this.headerMap.size() + 1, list);
  }

  public void addFooterAnimation(List<String> list) {
    this.footerMap.put(this.footerMap.size() + 1, list);
  }

  public List<String> getFooter() {
    if (this.footerMap.size() == 1) {
      var iterator = this.footerMap.entrySet().iterator();
      if (iterator.hasNext()) {
        Entry entry = (Entry) iterator.next();
        return (List<String>) entry.getValue();
      } else {
        return new ArrayList<>();
      }
    } else {
      return this.footerMap.get(this.lastFooter);
    }
  }

  @Deprecated
  public void setFooter(List<String> list) {
    this.footerMap.put(1, list);
  }

  public HashMap<Integer, List<String>> getHeaderMap() {
    return this.headerMap;
  }

  public void setHeaderMap(HashMap<Integer, List<String>> hashMap) {
    this.headerMap = hashMap;
  }

  public HashMap<Integer, List<String>> getFooterMap() {
    return this.footerMap;
  }

  public void setFooterMap(HashMap<Integer, List<String>> hashMap) {
    this.footerMap = hashMap;
  }
}
