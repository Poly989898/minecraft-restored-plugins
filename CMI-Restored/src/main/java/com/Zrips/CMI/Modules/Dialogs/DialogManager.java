package com.Zrips.CMI.Modules.Dialogs;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.utils.CMIThread;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class DialogManager {
  private CMI plugin;
  private Map<String, CMICustomDialog> map = Collections.synchronizedMap(new LinkedHashMap<>());

  public DialogManager(CMI cmi) {
    this.plugin = cmi;
  }

  private void loadConfig() {}

  public void clearCache(UUID uuid) {
    for (CMICustomDialog cmiCustomDialog : this.map.values()) {
      cmiCustomDialog.clearCache(uuid);
    }
  }

  public void load() {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R4) && Version.isPaperBranch()) {
      File file = new File(CMI.getInstance().getDataFolder(), "Dialogs");
      if (this.map.isEmpty()) {
        this.loadFolder(file);
      } else {
        CompletableFuture.runAsync(() -> this.loadFolder(file), CMIThread.getExecutor());
      }
    }
  }

  private void loadFolder(File file) {
    Long startTime = System.currentTimeMillis();
    ArrayList<String> arrayList = new ArrayList();

    for (String string : this.map.keySet()) {
      arrayList.add(string);
    }

    if (file.isDirectory()) {
      File[] files;
      int value = (files = file.listFiles()).length;

      for (int index = 0; index < value; index++) {
        File dialogFile = files[index];
        if (!dialogFile.exists()) {
          try {
            dialogFile.createNewFile();
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }

          return;
        }

        if (dialogFile.getName().endsWith(".yml")) {
          YamlConfiguration yamlConfiguration = null;

          try {
            yamlConfiguration = YamlConfiguration.loadConfiguration(dialogFile);
          } catch (Exception exception) {
            CMIMessages.consoleMessage("Could not load dialog file " + dialogFile.getName());
            continue;
          }

          if (!yamlConfiguration.getKeys(false).isEmpty()) {
            for (String dialogName : yamlConfiguration.getKeys(false)) {
              var dialogMap = flatten(yamlConfiguration.getConfigurationSection(dialogName));
              CMICustomDialog cmiCustomDialog = CMICustomDialog.deserialize(dialogName, dialogMap);
              if (cmiCustomDialog != null) {
                this.map.put(cmiCustomDialog.getName().toLowerCase(), cmiCustomDialog);
                arrayList.remove(cmiCustomDialog.getName().toLowerCase());
              }
            }
          }
        }
      }

      for (String staleName : arrayList) {
        this.map.remove(staleName);
      }

      this.plugin.loadMessage(this.map.size(), "dialogs", System.currentTimeMillis() - startTime);
    }
  }

  private static Map<String, Object> flatten(ConfigurationSection configurationSection) {
    LinkedHashMap linkedHashMap = new LinkedHashMap();

    for (String string : configurationSection.getKeys(false)) {
      Object object = configurationSection.get(string);
      if (object instanceof ConfigurationSection) {
        linkedHashMap.put(string, flatten((ConfigurationSection) object));
      } else {
        linkedHashMap.put(string, object);
      }
    }

    return linkedHashMap;
  }

  public CMICustomDialog getDialog(String string, Player player) {
    CMICustomDialog cmiCustomDialog =
        this.getDialog(
            string,
            player != null
                ? PermissionsManager.CMIPerm.command_dialog_disabled.hasPermission(player)
                : true);
    if (cmiCustomDialog == null) {
      return null;
    } else {
      return !cmiCustomDialog.getCondition().isConditionValid(player, cmiCustomDialog)
          ? null
          : cmiCustomDialog;
    }
  }

  public CMICustomDialog getDialog(String string, boolean flag) {
    CMICustomDialog cmiCustomDialog = this.map.get(string.toLowerCase());
    return cmiCustomDialog == null || !flag && !cmiCustomDialog.isEnabled()
        ? null
        : cmiCustomDialog;
  }

  public List<String> getDialogNames(CommandSender commandSender) {
    boolean flag = PermissionsManager.CMIPerm.command_dialog_disabled.hasPermission(commandSender);
    ArrayList<String> arrayList = new ArrayList();
    if (!flag) {
      for (CMICustomDialog cmiCustomDialog : this.map.values()) {
        if (cmiCustomDialog.isEnabled()
            && (!(commandSender instanceof Player)
                || cmiCustomDialog
                    .getCondition()
                    .isConditionValid((Player) commandSender, cmiCustomDialog))) {
          arrayList.add(cmiCustomDialog.getName());
        }
      }
    } else {
      arrayList.addAll(this.map.keySet());
    }

    return arrayList;
  }

  public List<String> getDialogNames(boolean flag) {
    ArrayList<String> arrayList = new ArrayList();
    if (!flag) {
      for (CMICustomDialog cmiCustomDialog : this.map.values()) {
        if (cmiCustomDialog.isEnabled()) {
          arrayList.add(cmiCustomDialog.getName());
        }
      }
    } else {
      arrayList.addAll(this.map.keySet());
    }

    return arrayList;
  }
}
