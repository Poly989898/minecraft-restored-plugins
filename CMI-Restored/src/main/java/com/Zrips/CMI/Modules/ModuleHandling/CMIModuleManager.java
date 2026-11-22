package com.Zrips.CMI.Modules.ModuleHandling;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CMICommand;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.configuration.file.YamlConfiguration;

public class CMIModuleManager {
  private static String fileName = "Modules.yml";

  public static void load() {
    long longValue = System.currentTimeMillis();
    CMI.getInstance().safeRenameFile(fileName, "Settings" + File.separator + fileName);
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(CMI.getInstance(), "Settings" + File.separator + fileName);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      configReader.header(
          Arrays.asList(
              "",
              "List of modules you can turn on and off if you don't use them to save some server"
                  + " resources",
              "This will COMPLETELY disable some of the features",
              "For this to take effect you will need to perform full server restart",
              "Disabling some of modules will disable appropriate commands, like cuffed will"
                  + " disable cuff command, silentChest will disable silentChest command and so on",
              "Disabled commands will not appear in tab complete and it will return error message"
                  + " that command by this name doesnt exist"));
      HashSet<String> hashSet = new HashSet();
      HashSet<String> moduleNames = new HashSet();
      CMIModule[] cmiModules;
      int value = (cmiModules = CMIModule.values()).length;

      for (int index = 0; index < value; index++) {
        CMIModule cmiModule = cmiModules[index];
        configReader.addComment(
            cmiModule.toString(),
            new String[] {
              " ",
              cmiModule.getDesc(),
              "Applies for servers from "
                  + cmiModule.getFromVersion().getShortFormated()
                  + (cmiModule.getToVersion() == null
                      ? " and up"
                      : " to " + cmiModule.getToVersion().getShortFormated() + " version")
            });
        cmiModule.setEnabled(configReader.get(cmiModule.toString(), true));
        if (cmiModule.isEnabled()) {
          hashSet.add(cmiModule.name());
        } else {
          moduleNames.add(cmiModule.name());
        }
      }

      configReader.save();

      for (var entry : CMI.getInstance().getCommandManager().getCommands().entrySet()) {
        ((CMICommand) entry.getValue()).setEnabled(null);
      }

      CMI.getInstance()
          .loadMessage(
              CMIModule.values().length,
              "&f"
                  + hashSet.size()
                  + " &2Enabled &3and &f"
                  + moduleNames.size()
                  + " &eDisabled &3modules",
              System.currentTimeMillis() - longValue);
    }
  }

  public static void registerListeners() {
    CMIModule[] cmiModules;
    int value = (cmiModules = CMIModule.values()).length;

    for (int index = 0; index < value; index++) {
      CMIModule cmiModule = cmiModules[index];
      if (cmiModule.isEnabled()) {
        try {
          cmiModule.registerListener();
        } catch (Exception | Error value3) {
        }
      } else if (cmiModule == CMIModule.spawnerCharge && CMIModule.spawners.isEnabled()) {
        try {
          cmiModule.registerListener();
        } catch (Exception | Error value4) {
        }
      }
    }
  }

  public static void switchInFile(CMIModule cmiModule, boolean flag) {
    File file = new File(CMI.getInstance().getDataFolder(), "Settings" + File.separator + fileName);
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    yamlConfiguration.options().copyDefaults(true);
    yamlConfiguration.set(cmiModule.toString(), flag);

    try {
      yamlConfiguration.save(file);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }
}
