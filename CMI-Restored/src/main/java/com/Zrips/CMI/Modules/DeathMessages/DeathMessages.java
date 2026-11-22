package com.Zrips.CMI.Modules.DeathMessages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class DeathMessages {
  private List<String> messages = new ArrayList<>();
  private String path;

  public DeathMessages(String string, List<String> list) {
    this.path = string;
    this.messages = list;
  }

  public DeathMessages addMessage(String string) {
    if (string != null && !string.isEmpty()) {
      this.messages.add(string);
      return this;
    } else {
      return this;
    }
  }

  @Nonnull
  public String getRandom() {
    if (this.messages.isEmpty()) {
      return "";
    }

    Collections.shuffle(this.messages);
    return this.messages.get(0);
  }

  public String getPath() {
    return this.path;
  }

  @Nonnull
  public List<String> getMessages() {
    return this.messages;
  }

  public void setMessages(@Nonnull List<String> list) {
    this.messages = list;
  }
}
