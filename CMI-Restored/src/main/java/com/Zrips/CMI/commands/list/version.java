package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.CMIOnEnable;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashSet;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class version implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("cmiVersion", "{gcp}CMI: {gcs}[version]");
    configReader.get("cmiVersionNew", "{gcp}-> [newVersion]");
    configReader.get("dbType", " {gcn}[db]");
    configReader.get("newServer", "{gcp}Server: {gcs}[version]");
    configReader.get("Economy", "{gcp}CMI economy: {gcs}[state] ");
    configReader.get("Chat", "{gcp}CMI Chat: {gcs}[state] ");
    configReader.get("CMIB", "{gcp}CMIB ");
    configReader.get("newVault", "{gcp}Vault: {gcs}[version] ");
    configReader.get(
        "Modules",
        "{gcp}Modules -> {gcu}[enabled] enabled {gcd}[disabled] disabled{gcp}: {gcd}[list]");
    configReader.get("Network", " {gcp}[type] ");
    configReader.get("CMILib", "{gcp}CMILib: {gcs}[version] ");
    configReader.get("cmilVersionNew", "{gcp}-> [newVersion]");
  }

  @CAnnotation(
      priority = 9999,
      info = "Show plugin version",
      args = "",
      tab = {},
      explanation = {},
      regVar = 0,
      consoleVar = 0,
      others = false,
      alias = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIScheduler.runTaskAsynchronously(
        cmi,
        () -> {
          String string = cmi.getDescription().getVersion();
          String officialVersion = cmi.getVersionCheckManager().getOfficialVersion();
          String latestVersion =
              CMI.getInstance().getVersionCheckManager().getOfficialVersion(CMIOnEnable.cmiLibId);
          String bukkitVersion = Bukkit.getBukkitVersion();
          String[] versionParts = Bukkit.getVersion().split("-");
          String serverBuild = versionParts.length > 1 ? versionParts[1] : versionParts[0];
          String build = null;

          try {
            if (!serverBuild.equalsIgnoreCase("Paper")
                && !serverBuild.equalsIgnoreCase("purpur")
                && !Version.isPaper()) {
              if (Version.isSpigot()) {
                build = Bukkit.getVersion().split("-", 2)[0];
              }
            } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
              build = Bukkit.getVersion().split("-")[1].split(" ")[0];
            } else {
              build = Bukkit.getVersion().split("-")[2].split(" ")[0];
            }

            Integer.parseInt(build);
          } catch (Throwable throwable) {
            build = null;
          }

          String buildSuffix = build == null ? "" : "(" + build + ")";
          String versionInfo =
              cmi.getEconomyManager().isVaultEnabled()
                  ? cmi.getEconomyManager().getVaultManager().getVersion()
                  : null;
          Plugin plugin = cmi.getServer().getPluginManager().getPlugin("CMIEInjector");
          String extra = "";
          if (plugin != null) {
            extra = "(CMIEInjector)";
          }

          versionInfo =
              versionInfo == null
                  ? null
                  : (versionInfo
                          + (cmi.getEconomyManager().getVaultManager().isSupportsCmi()
                              ? "(+)"
                              : extra))
                      .replace("${env.TRAVIS_BUILD_NUMBER}", "");
          String infoText = versionInfo;
          CMIScheduler.runTask(
              cmi,
              () -> {
                Plugin cmiLibPlugin = Bukkit.getPluginManager().getPlugin("CMILib");
                String cmiLibVersion = null;
                if (cmiLibPlugin != null) {
                  cmiLibVersion = cmiLibPlugin.getDescription().getVersion();
                }

                LC.info_Spliter.sendMessage(commandSender, new Object[0]);
                RawMessage rawMessage = new RawMessage();
                rawMessage.addText(
                    CMILC.getIM("version", "cmiVersion", "[version]", string)
                        + (cmi.getBungeeCordManager().isBungeeCord()
                            ? CMILC.getIM(
                                "version",
                                "Network",
                                "[type]",
                                cmi.getBungeeCordManager().getNetworkType())
                            : "")
                        + (cmi.getBungeeCordManager().isCMIBPresent()
                            ? CMILC.getIM("version", "CMIB")
                            : "")
                        + CMILC.getIM(
                            "version",
                            "dbType",
                            "[db]",
                            cmi.getDbManager().getDbType().toString()));
                if (!string.equalsIgnoreCase(officialVersion)) {
                  rawMessage
                      .addText(
                          CMILC.getIM("version", "cmiVersionNew", "[newVersion]", officialVersion))
                      .addUrl("https://www.spigotmc.org/resources/3742/updates")
                      .addHover("&r{gcu}New version available");
                }

                rawMessage.show(commandSender);
                if (cmiLibVersion != null) {
                  rawMessage = new RawMessage();
                  rawMessage.addText(CMILC.getIM("version", "CMILib", "[version]", cmiLibVersion));
                  if (latestVersion != null
                      && CMI.getInstance().getVersionCheckManager().convertVersion(cmiLibVersion)
                          < CMI.getInstance()
                              .getVersionCheckManager()
                              .convertVersion(latestVersion)) {
                    rawMessage.addText(
                        CMILC.getIM("version", "cmilVersionNew", "[newVersion]", latestVersion));
                    rawMessage
                        .addUrl(
                            "https://www.spigotmc.org/resources/"
                                + CMIOnEnable.cmiLibId
                                + "/updates")
                        .addHover("&r{gcu}New version available");
                  }

                  rawMessage.show(commandSender);
                }

                CMILC.info(
                    "version",
                    commandSender,
                    "newServer",
                    "[version]",
                    CMIText.firstToUpperCase(Version.getPlatform().toString())
                        + buildSuffix
                        + " "
                        + bukkitVersion
                        + (Bukkit.getOnlineMode() ? "+" : "-"));
                CMILC.sendMessage(
                    commandSender,
                    CMILC.getIM(
                            "version", "Economy", "[state]", cmi.getEconomyManager().isEnabled())
                        + (infoText != null
                            ? CMILC.getIM("version", "newVault", "[version]", infoText)
                            : "")
                        + CMILC.getIM(
                            "version",
                            "Chat",
                            "[state]",
                            cmi.getChatManager().isModifyChatFormat()));
                HashSet<String> hashSet = new HashSet();
                HashSet<String> disabledModules = new HashSet();
                CMIModule[] cmiModules;
                int value = (cmiModules = CMIModule.values()).length;

                for (int moduleIndex = 0; moduleIndex < value; moduleIndex++) {
                  CMIModule cmiModule = cmiModules[moduleIndex];
                  if (cmiModule.isEnabled()) {
                    hashSet.add(cmiModule.name());
                  } else {
                    disabledModules.add(cmiModule.name());
                  }
                }

                StringBuilder stringBuilder = new StringBuilder();

                for (String moduleName : disabledModules) {
                  if (!stringBuilder.toString().isEmpty()) {
                    stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
                  }

                  stringBuilder.append(moduleName);
                }

                CMILC.info(
                    "version",
                    commandSender,
                    "Modules",
                    "[enabled]",
                    hashSet.size(),
                    "[disabled]",
                    disabledModules.size(),
                    "[list]",
                    stringBuilder.toString());
                LC.info_Spliter.sendMessage(commandSender, new Object[0]);
              });
        });
    return true;
  }
}
