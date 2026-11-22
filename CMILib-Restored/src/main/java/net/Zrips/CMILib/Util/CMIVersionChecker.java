package net.Zrips.CMILib.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class CMIVersionChecker {
  public static String s1 = "%%__USER__%%";
  public static String s2 = "%%__RESOURCE__%%";
  public static String s3 = "%%__NONCE__%%";

  public static Integer convertVersion(String v) {
    v = v.replaceAll("[^\\d.]", "");
    Integer version = 0;
    if (v.contains(".")) {
      StringBuilder lVersion = new StringBuilder();

      for (String one : v.split("\\.")) {
        String s = one;
        if (s.length() == 1) {
          s = "0" + s;
        }

        lVersion.append(s);
      }

      try {
        version = Integer.parseInt(lVersion.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        version = Integer.parseInt(v);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return version;
  }

  public static String deconvertVersion(Integer v) {
    StringBuilder version = new StringBuilder();

    for (String vs = String.valueOf(v); vs.length() > 0; vs = vs.substring(0, vs.length() - 2)) {
      int subv = 0;

      try {
        if (vs.length() > 2) {
          subv = Integer.parseInt(vs.substring(vs.length() - 2));
          version.insert(0, "." + subv);
        } else {
          subv = Integer.parseInt(vs);
          version.insert(0, (int) subv);
        }
      } catch (Throwable var5) {
      }

      if (vs.length() <= 2) {
        break;
      }
    }

    return version.toString();
  }

  public static void VersionCheck(Player player, int resource, PluginDescriptionFile dec) {
    CMIScheduler.get()
        .runTaskAsynchronously(
            () -> {
              String currentVersion = dec.getVersion();
              String newVersion = getOfficialVersion(resource, dec.getName());
              if (newVersion != null
                  && convertVersion(newVersion) > convertVersion(currentVersion)) {
                RawMessage rm = new RawMessage();
                rm.addText(
                    CMIChatColor.GRAY
                        + "_________________/ "
                        + dec.getName()
                        + " \\_________________\n");
                rm.addText(
                        CMIChatColor.GRAY
                            + "| "
                            + newVersion
                            + " is now available! Your version: "
                            + currentVersion
                            + "\n")
                    .addHover(dec.getWebsite())
                    .addUrl(dec.getWebsite());
                rm.addText(CMIChatColor.GRAY + "----------------------------------------");
                rm.show((CommandSender) (player == null ? Bukkit.getConsoleSender() : player));
              }
            });
  }

  public static String getOfficialVersion(int resource, String pluginName) {
    String version = null;

    try {
      URLConnection con =
          new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resource)
              .openConnection();
      InputStream stream = con.getInputStream();
      InputStreamReader reader = new InputStreamReader(stream);
      BufferedReader buffer = new BufferedReader(reader);
      version = buffer.readLine();
      if (version.length() > 12) {
        version = null;
      }

      buffer.close();
    } catch (Throwable ex) {
      CMIMessages.consoleMessage(
          CMIChatColor.RED + "Failed to check for " + pluginName + " update on spigot web page.");
    }

    return version;
  }
}
