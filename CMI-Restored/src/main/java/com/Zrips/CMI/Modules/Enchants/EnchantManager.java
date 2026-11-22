package com.Zrips.CMI.Modules.Enchants;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

public class EnchantManager {
  private Map<String, Object> maxValues = new HashMap<>();
  private HashMap<String, Enchantment> disabled = new HashMap<>();
  private CMI plugin;
  private boolean RequireSpecificPermission;
  private boolean EnforceValidEnchants;
  private boolean PermissionLevelLimit;
  private boolean enchantLimitEnabled = false;
  private String fileName = "DisabledEnchants.yml";

  public EnchantManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Enchanting.enchantLimits.Enabled",
        new String[] {
          "By disabling this, no limitation to enchanting will be applied",
          "This only applies for enchant command not for natural enchanting"
        });
    this.enchantLimitEnabled = configReader.get("Enchanting.enchantLimits.Enabled", true);
    Enchantment[] enchantments;
    int value = (enchantments = Enchantment.values()).length;

    for (int index = 0; index < value; index++) {
      Enchantment enchantment = enchantments[index];

      try {
        if (enchantment != null) {
          String string = enchantment.getName();
          if (string != null && !string.isEmpty() && string != " ") {
            configReader.get(
                "Enchanting.enchantLimits.MaxLevel." + string.toLowerCase(),
                enchantment.getMaxLevel());
          }
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }

    this.maxValues.clear();
    if (this.enchantLimitEnabled
        && configReader.getC().isConfigurationSection("Enchanting.enchantLimits.MaxLevel")) {
      this.maxValues =
          configReader
              .getC()
              .getConfigurationSection("Enchanting.enchantLimits.MaxLevel")
              .getValues(true);
    }

    configReader.addComment(
        "Enchanting.RequireSpecificPermission",
        new String[] {
          "When set to true, players will be required to have "
              + PermissionsManager.CMIPerm.enchantments_$1.getPermission("[enchantName]")
              + " permission node"
        });
    this.RequireSpecificPermission =
        configReader.get("Enchanting.RequireSpecificPermission", false);
    configReader.addComment(
        "Enchanting.EnforceValidEnchants",
        new String[] {
          "When enabled we will only allow to enchant items with valid enchantments",
          "This can be bypasses with "
              + PermissionsManager.CMIPerm.command_enchant_bypassinvalid.getPermission()
              + " permission node"
        });
    this.EnforceValidEnchants = configReader.get("Enchanting.EnforceValidEnchants", true);
    configReader.addComment(
        "Enchanting.PermissionLevelLimit",
        new String[] {
          "When set to true, players will be required to have "
              + PermissionsManager.CMIPerm.enchantments_$1_$2.getPermission(
                  "[enchantName]", "[maxLevel]")
              + " permission node to be abble to enchant item to defined max level",
          "Higest permission will be taken if player has more then one",
          "Keep in mind that this will not prevent player from enchanting item to lower levels then"
              + " permission was set too",
          "And keep in mind that players without defined permission node will have access to level"
              + " 1 enchants by default"
        });
    this.PermissionLevelLimit = configReader.get("Enchanting.PermissionLevelLimit", false);
  }

  public Integer getMaxLevel(Enchantment enchantment) {
    if (enchantment == null) {
      return 0;
    }

    String string = enchantment.getName();
    if (string != null && !string.isEmpty() && string != " ") {
      for (var entry : this.maxValues.entrySet()) {
        if (((String) entry.getKey())
            .replace("_", "")
            .equalsIgnoreCase(enchantment.getName().replace("_", ""))) {
          return (Integer) entry.getValue();
        }
      }

      return enchantment.getMaxLevel();
    } else {
      return 0;
    }
  }

  public void reEnableEnchantsOnDisable() {
    for (var entry : this.disabled.entrySet()) {
      this.plugin.getReflectionManager().manageEnchantment((Enchantment) entry.getValue(), false);
    }
  }

  public void disableEnchantsOnLoad() {
    for (var entry : this.disabled.entrySet()) {
      this.plugin.getReflectionManager().manageEnchantment((Enchantment) entry.getValue(), true);
    }
  }

  public HashMap<String, Enchantment> getDisabled() {
    return this.disabled;
  }

  public boolean isDisabled(Enchantment enchantment) {
    String string = enchantment.getName();
    return string != null && !string.isEmpty() && string != " "
        ? this.disabled.containsKey(enchantment.getName())
        : false;
  }

  public void addDisabled(Enchantment enchantment) {
    String string = enchantment.getName();
    if (string != null && !string.isEmpty() && string != " ") {
      this.disabled.put(enchantment.getName(), enchantment);
    }
  }

  public void removeDisabled(Enchantment enchantment) {
    String string = enchantment.getName();
    if (string != null && !string.isEmpty() && string != " ") {
      this.disabled.remove(enchantment.getName());
    }
  }

  public void loadDisabledEnchants() {
    this.plugin.safeRenameFile("DisabledEnchants.yml", "Saves" + File.separator + this.fileName);
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      configReader.addComment(
          "List",
          new String[] {
            "List of enchants you want to disable ingame. Optionaly /cmi disableenchant command can"
                + " be used"
          });
      List list = configReader.get("List", new ArrayList());
      this.disabled.clear();

      for (String string : ((Iterable<String>) list)) {
        try {
          Enchantment enchantment = CMIEnchantment.get(string);
          if (enchantment != null) {
            String enchantName = enchantment.getName();
            if (enchantName != null && !enchantName.isEmpty() && enchantName != " ") {
              this.disabled.put(enchantment.getName(), enchantment);
            }
          }
        } catch (Exception | Error value) {
          value.printStackTrace();
        }
      }

      configReader.save();
    }
  }

  public void updateConfig() {
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    ArrayList<String> arrayList = new ArrayList();

    for (var entry : this.disabled.entrySet()) {
      try {
        String string = ((Enchantment) entry.getValue()).getName();
        if (string != null && !string.isEmpty() && string != " ") {
          arrayList.add(((Enchantment) entry.getValue()).getName());
        }
      } catch (Exception | Error value) {
        value.printStackTrace();
      }
    }

    yamlConfiguration.set("List", arrayList);

    try {
      yamlConfiguration.save(file);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  public boolean isRequireSpecificPermission() {
    return this.RequireSpecificPermission;
  }

  public boolean isPermissionLevelLimit() {
    return this.PermissionLevelLimit;
  }

  public boolean isEnchantLimitEnabled() {
    return this.enchantLimitEnabled;
  }

  public boolean isEnforceValidEnchants() {
    return this.EnforceValidEnchants;
  }
}
