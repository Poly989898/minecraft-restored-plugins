package net.Zrips.CMILib.Logs;

import java.util.Arrays;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CMIDebug {
  private Object[] message;
  private static long fixedTime = 0L;

  public static void it() {
    fixedTime = System.nanoTime();
  }

  public static boolean isTestServer() {
    return Version.isTestServer();
  }

  public static double getIT() {
    return (System.nanoTime() - fixedTime) / 100L / 10000.0;
  }

  public static void d(Object... message) {
    if (Version.isTestServer()) {
      Player player = Bukkit.getPlayer("Zrips");
      if (player != null && player.isOnline()) {
        StringBuilder fullMessage = new StringBuilder();
        int i = 1;
        CMIChatColor cl = CMIChatColor.GRAY;
        if (message == null) {
          fullMessage.append("null");
        } else {
          for (Object one : message) {
            if (++i >= 2) {
              i = 0;
              if (cl == CMIChatColor.GRAY) {
                cl = CMIChatColor.WHITE;
              } else {
                cl = CMIChatColor.GRAY;
              }

              fullMessage.append(cl);
            }

            if (one instanceof String[]) {
              fullMessage.append(Arrays.asList((String[]) one).toString() + " ");
            } else {
              fullMessage.append(one + " ");
            }
          }
        }

        player.sendMessage(
            CMIChatColor.DARK_GRAY + "[CMID] " + CMIChatColor.DARK_AQUA + fullMessage.toString());
      }
    }
  }

  public static void cd(Object... message) {
    Player player = Bukkit.getPlayer("Zrips");
    if (player != null && player.isOnline()) {
      StringBuilder fullMessage = new StringBuilder();
      if (message == null) {
        fullMessage.append("null");
      } else {
        for (Object one : message) {
          if (one instanceof String[]) {
            fullMessage.append(Arrays.asList((String[]) one).toString()).append(" ");
          } else {
            fullMessage.append(String.valueOf(one)).append(" ");
          }
        }
      }

      player.sendMessage(
          CMIChatColor.DARK_GRAY
              + "[CMID] "
              + CMIChatColor.DARK_AQUA
              + fullMessage.toString().replace("§", "&"));
    }
  }

  public static void c(Object... message) {
    if (Version.isTestServer()) {
      if (CommandsHandler.enabledDebug) {
        if (Version.isTestServer()) {
          new CMIDebug(message);
        }
      }
    }
  }

  public CMIDebug(String msg) {
    this((Object) msg);
  }

  public CMIDebug(Object... message) {
    if (CommandsHandler.enabledDebug) {
      this.message = message;
      CMILib.getInstance().getLogManager().print(this);
    }
  }

  public String getMsg() {
    StringBuilder fullMessage = new StringBuilder();

    for (Object one : this.message) {
      if (!fullMessage.toString().isEmpty()) {
        fullMessage.append(" ");
      }

      if (one instanceof String[]) {
        fullMessage.append(String.valueOf(Arrays.asList((String[]) one).toString()));
      } else {
        fullMessage.append(String.valueOf(one));
      }
    }

    return fullMessage.toString();
  }

  public void setMsg(Object... message) {
    this.message = message;
  }
}
