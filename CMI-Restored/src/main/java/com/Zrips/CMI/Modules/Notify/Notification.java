package com.Zrips.CMI.Modules.Notify;

import java.util.ArrayList;
import java.util.List;

public class Notification {
  private String notifyier;
  private List<String> mesages = new ArrayList<>();

  public Notification(String string) {
    this.notifyier = string;
  }

  public String getNotifyier() {
    return this.notifyier;
  }

  public void setNotifyier(String string) {
    this.notifyier = string;
  }

  public List<String> getMesages() {
    return this.mesages;
  }

  public void setMesages(List<String> list) {
    this.mesages = list;
  }

  public void addMesage(String string) {
    this.mesages.add(string);
  }
}
