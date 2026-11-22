package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VersionChecker {
  private int resource = 3742;
  private CMI plugin;
  public static String s1 = "835824";
  public static String s2 = "3742";
  public static String s3 = "358302464";

  public VersionChecker(CMI cmi) {
    this.plugin = cmi;
  }

  public Integer convertVersion(String string) {
    string = string.replaceAll("[^\\d.]", "");
    Integer integer = 0;
    if (string.contains(".")) {
      String versionDigits = "";
      String[] strings;
      int value = (strings = string.split("\\.")).length;

      for (int index = 0; index < value; index++) {
        String part = strings[index];
        String paddedPart = part;
        if (paddedPart.length() == 1) {
          paddedPart = "0" + paddedPart;
        }

        versionDigits = versionDigits + paddedPart;
      }

      try {
        integer = Integer.parseInt(versionDigits);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    } else {
      try {
        integer = Integer.parseInt(string);
      } catch (Exception exception2) {
        exception2.printStackTrace();
      }
    }

    return integer;
  }

  public String deconvertVersion(Integer integer) {
    StringBuilder stringBuilder = new StringBuilder();

    for (String string = String.valueOf(integer);
        string.length() > 0;
        string = string.substring(0, string.length() - 2)) {
      int value = 0;

      try {
        if (string.length() > 2) {
          value = Integer.parseInt(string.substring(string.length() - 2));
          stringBuilder.insert(0, "." + value);
        } else {
          value = Integer.parseInt(string);
          stringBuilder.insert(0, (int) value);
        }
      } catch (Throwable throwable) {
      }

      if (string.length() <= 2) {
        break;
      }
    }

    return stringBuilder.toString();
  }

  public void VersionCheck(Player player) {
    CMIScheduler.runTaskAsynchronously(
        this.plugin,
        () -> {
          String string = this.plugin.getDescription().getVersion();
          String officialVersion = this.getOfficialVersion(this.resource);
          if (officialVersion != null
              && this.convertVersion(officialVersion) > this.convertVersion(string)) {
            RawMessage rawMessage = new RawMessage();
            rawMessage.addText(
                CMIChatColor.GRAY
                    + "_________________/ "
                    + this.plugin.getDescription().getName()
                    + " \\_________________\n");
            rawMessage
                .addText(
                    CMIChatColor.GRAY
                        + "| "
                        + officialVersion
                        + " is now available! Your version: "
                        + string
                        + "\n")
                .addHover(this.plugin.getDescription().getWebsite())
                .addUrl(this.plugin.getDescription().getWebsite());
            rawMessage.addText(CMIChatColor.GRAY + "----------------------------------------");
            rawMessage.show(player);
          }
        });
  }

  public String getOfficialVersion() {
    return this.getOfficialVersion(this.resource);
  }

  public String getOfficialVersion(int value) {
    BufferedReader bufferedReader = null;

    try {
      URLConnection urlConnection =
          new URL("https://api.spigotmc.org/legacy/update.php?resource=" + value).openConnection();
      bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      String string = bufferedReader.readLine();
      if (string.length() <= 11) {
        return string;
      }
    } catch (Exception exception) {
      this.plugin.consoleMessage(
          ChatColor.RED
              + "Failed to check for "
              + this.plugin.getDescription().getName()
              + " update on spigot web page.");
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      }
    }

    return null;
  }
}
