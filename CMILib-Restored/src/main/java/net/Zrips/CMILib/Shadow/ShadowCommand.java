package net.Zrips.CMILib.Shadow;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.entity.Player;

public class ShadowCommand {
  public static String prefix = CommandsHandler.getLabel();
  private static HashMap<UUID, ShadowCommand> shadowCommand = new HashMap<>();
  HashMap<String, ShadowCommandInfo> map = new HashMap<>();

  public static String addShadowCmd(
      Player player, String cmd, Boolean infinite, ShadowCommandType type) {
    String id = UUID.randomUUID() + (infinite ? "i" : "");
    ShadowCommand shadow = shadowCommand.getOrDefault(player.getUniqueId(), new ShadowCommand());
    id = shadow.add(id, cmd, type);
    shadowCommand.put(player.getUniqueId(), shadow);
    return id;
  }

  public static void remove(UUID uuid) {
    shadowCommand.remove(uuid);
  }

  public static ShadowCommandInfo getShadowCommand(Player player, String id) {
    ShadowCommand shadow = shadowCommand.get(player.getUniqueId());
    return shadow == null ? null : shadow.get(id);
  }

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
