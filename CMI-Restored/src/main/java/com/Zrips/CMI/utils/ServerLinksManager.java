package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ServerLinks.ServerLink;

public class ServerLinksManager {
  public static void save() {
    if (!Version.isCurrentLower(Version.v1_21_R1)) {
      ConfigReader configReader = null;

      try {
        configReader =
            new ConfigReader(CMI.getInstance(), "Saves" + File.separator + "serverLinks.yml");
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      if (configReader != null) {
        ArrayList<String> arrayList = new ArrayList();

        for (ServerLink serverLink : Bukkit.getServerLinks().getLinks()) {
          arrayList.add(
              CMIChatColor.deColorize(serverLink.getDisplayName())
                  + ";"
                  + serverLink.getUrl().toString());
        }

        configReader.set("Links", arrayList);
        configReader.save();
      }
    }
  }

  public static void load() {
    if (!Version.isCurrentLower(Version.v1_21_R1) && CMIModule.serverLinks.isEnabled()) {
      File file =
          new File(CMI.getInstance().getDataFolder(), "Saves" + File.separator + "serverLinks.yml");
      if (file.exists()) {
        Long startTime = System.currentTimeMillis();
        ConfigReader configReader = null;

        try {
          configReader = new ConfigReader(file);
        } catch (Exception exception) {
          exception.printStackTrace();
        }

        if (configReader != null) {
          if (configReader.getC().isList("Links")) {
            Bukkit.getServerLinks()
                .getLinks()
                .forEach(serverLink -> Bukkit.getServerLinks().removeLink(serverLink));

            for (String string : configReader.getC().getStringList("Links")) {
              if (string.contains(";")) {
                String[] strings = string.split(";", 2);
                if (strings.length >= 2) {
                  String linkUrl = strings[1];
                  if (!linkUrl.startsWith("http://") && !linkUrl.startsWith("https://")) {
                    linkUrl = "http://" + linkUrl;
                  }

                  try {
                    Bukkit.getServerLinks()
                        .addLink(CMIChatColor.translate(strings[0]), new URI(linkUrl));
                  } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                  }
                }
              }
            }

            Bukkit.getOnlinePlayers().forEach(value -> value.sendLinks(Bukkit.getServerLinks()));
            CMI.getInstance()
                .loadMessage(
                    Bukkit.getServerLinks().getLinks().size(),
                    "Server links",
                    System.currentTimeMillis() - startTime);
          }
        }
      }
    }
  }
}
