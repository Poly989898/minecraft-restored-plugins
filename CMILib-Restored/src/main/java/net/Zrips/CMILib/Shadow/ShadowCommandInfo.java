package net.Zrips.CMILib.Shadow;

public class ShadowCommandInfo {
  private String cmd = "";
  private ShadowCommandType type = ShadowCommandType.Console;

  public ShadowCommandInfo(ShadowCommandType type, String cmd) {
    this.type = type;
    this.cmd = cmd;
  }

  public ShadowCommandType getType() {
    return this.type;
  }

  public void setType(ShadowCommandType type) {
    this.type = type;
  }

  public String getCmd() {
    return this.cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }
}
