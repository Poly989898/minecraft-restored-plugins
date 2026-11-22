package com.Zrips.CMI.Modules.ChatFilter;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageLog {
  private HashMap<Long, String> messages = new HashMap<>();
  private int range = 5;

  public MessageLog(int value) {
    this.range = value;
  }

  public void addMessage(String string) {
    this.messages.put(System.currentTimeMillis(), string);
  }

  private void removeOld() {
    ArrayList<Long> arrayList = new ArrayList();

    for (var entry : this.messages.entrySet()) {
      if ((Long) entry.getKey() + this.range * 1000 < System.currentTimeMillis()) {
        arrayList.add((Long) entry.getKey());
      }
    }

    for (Long messageTime : arrayList) {
      this.messages.remove(messageTime);
    }
  }

  public HashMap<Long, String> getMessages() {
    this.removeOld();
    return this.messages;
  }
}
