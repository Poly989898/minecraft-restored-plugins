package com.Zrips.CMI.events;

import com.Zrips.CMI.Containers.CMIUser;

public class CMIPlayerNickNameChangeEvent extends CMIUserCancellableEvent {
  private String nickname;

  public CMIPlayerNickNameChangeEvent(CMIUser cmiUser, String string) {
    super(cmiUser);
    this.nickname = string;
  }

  public String getNickName() {
    return this.nickname;
  }

  public void setNickName(String string) {
    this.nickname = string;
  }
}
