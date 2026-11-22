package net.Zrips.CMILib.Logs;

import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;

public class CMILogManager {
  boolean enabledDebug = false;

  public CMILogManager() {
    this.enabledDebug = Version.isTestServer();
  }

  public void print(CMIDebug log) {
    CMIMessages.consoleMessage(log.getMsg());
  }
}
