package net.Zrips.CMILib.Chat;

import java.util.HashMap;
import java.util.Map.Entry;

public class ShadowCommand {
  HashMap<String, ShadowCommandInfo> map = new HashMap<>();

  public String add(String id, String consoleCommand, ShadowCommandType type) {
    ShadowCommandInfo cmd = new ShadowCommandInfo(type, consoleCommand);
    if (!id.contains("!")) {
      for (Entry<String, ShadowCommandInfo> one : this.map.entrySet()) {
        if (one.getValue().getCmd().equals(consoleCommand)) {
          id = one.getKey();
        }
      }
    }

    this.map.put(id, cmd);
    return id;
  }

  public ShadowCommandInfo get(String id) {
    ShadowCommandInfo cmd = this.map.get(id);
    return cmd != null && !id.endsWith("i") ? this.map.remove(id) : cmd;
  }
}
