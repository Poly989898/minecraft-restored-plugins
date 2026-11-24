package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.PluginUpdate;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginUpdateManager {
   private final Map<Integer, PluginUpdate> updates = new HashMap<>();

   public PluginUpdateManager() {
      this.register(
         new PluginUpdate(
            1,
            new String[]{"Applies a fix for skull textures values in 4.7.1.", "Texture values data storage changed in 4.7.1 due to the UUID change."},
            sender -> {
               for (Type type : MMOItems.plugin.getTypes().getAll()) {
                  ConfigFile configFile = type.getConfigFile();

                  for (String key : configFile.getConfig().getKeys(false)) {
                     ConfigurationSection section = configFile.getConfig().getConfigurationSection(key);
                     if (section.contains("skull-texture") && section.get("skull-texture") instanceof String) {
                        section.set("skull-texture.value", section.getString("skull-texture"));
                        section.set("skull-texture.uuid", UUID.randomUUID().toString());
                     }
                  }

                  configFile.save();
               }
            }
         )
      );
      this.register(
         new PluginUpdate(
            3,
            new String[]{
               "5.3.2: converts all your crafting station recipes to the newest config format.",
               "&cWarning, running this update will get rid of your # config file comments."
            },
            sender -> {
               for (File listFile : new File(MMOItems.plugin.getDataFolder() + "/crafting-stations").listFiles()) {
                  YamlConfiguration config = YamlConfiguration.loadConfiguration(listFile);
                  if (config.contains("recipes")) {
                     for (String key : config.getConfigurationSection("recipes").getKeys(false)) {
                        try {
                           List<String> texts = config.getStringList("recipes." + key + ".ingredients");
                           ArrayList list = new ArrayList();

                           for (String text : texts) {
                              String[] oldParts = text.split(" ");
                              if (oldParts[0].equals("mmoitem")) {
                                 String newCondition = "mmoitem{type=" + oldParts[1] + ",id=" + oldParts[2];
                                 if (oldParts.length > 3) {
                                    newCondition = newCondition + ",amount=" + oldParts[3];
                                 }

                                 if (oldParts.length > 4) {
                                    newCondition = newCondition + ",display=\"" + oldParts[4].replace("_", " ") + "\"";
                                 }

                                 list.add(newCondition + "}");
                              } else if (oldParts[0].equals("vanilla")) {
                                 String newCondition = "vanilla{type=" + oldParts[1];
                                 if (oldParts.length > 2 && !oldParts[2].equals(".")) {
                                    newCondition = newCondition + ",name=\"" + oldParts[2] + "\"";
                                 }

                                 if (oldParts.length > 3) {
                                    newCondition = newCondition + ",amount=" + oldParts[3];
                                 }

                                 if (oldParts.length > 4) {
                                    newCondition = newCondition + ",display=\"" + oldParts[4].replace("_", " ") + "\"";
                                 }

                                 list.add(newCondition + "}");
                              } else {
                                 MMOItems.plugin
                                    .getLogger()
                                    .log(
                                       Level.SEVERE,
                                       "Config Update 3: Could not match ingredient from '" + text + "' from recipe '" + key + "', added it anyway."
                                    );
                                 list.add(text);
                              }
                           }

                           config.set("recipes." + key + ".ingredients", list);
                           List<String> conditions = config.getStringList("recipes." + key + ".conditions");
                           list = new ArrayList();

                           for (String text : conditions) {
                              String[] conditionParts = text.split(" ");
                              if (conditionParts[0].equalsIgnoreCase("class")) {
                                 list.add("class{list=\"" + text.replace(conditionParts[0] + " ", "").replace(" ", ",") + "\"}");
                              } else if (conditionParts[0].equalsIgnoreCase("perms")) {
                                 list.add("permission{list=\"" + text.replace(conditionParts[0] + " ", "").replace(" ", ",") + "\"}");
                              } else if (conditionParts[0].equalsIgnoreCase("food") || conditionParts[0].equals("mana") || conditionParts[0].equals("stamina")) {
                                 list.add(conditionParts[0] + "{amount=" + conditionParts[1] + "}");
                              } else if (conditionParts[0].equalsIgnoreCase("level")) {
                                 list.add("level{level=" + conditionParts[1] + "}");
                              } else if (conditionParts[0].equalsIgnoreCase("profession")) {
                                 list.add("profession{profession=" + conditionParts[1] + ",level=" + conditionParts[2] + "}");
                              } else if (conditionParts[0].equalsIgnoreCase("exp")) {
                                 list.add("exp{profession=" + conditionParts[1] + ",amount=" + conditionParts[2] + "}");
                              } else {
                                 MMOItems.plugin
                                    .getLogger()
                                    .log(
                                       Level.SEVERE,
                                       "Config Update 3: Could not match condition from '" + text + "' from recipe '" + key + "', added it anyway."
                                    );
                                 list.add(text);
                              }
                           }

                           config.set("recipes." + key + ".conditions", list);
                        } catch (Exception exception) {
                           MMOItems.plugin
                              .getLogger()
                              .log(Level.SEVERE, "Config Update 3: Could not convert recipe with key '" + key + "': " + exception.getMessage());
                        }
                     }

                     try {
                        config.save(listFile);
                     } catch (IOException exception) {
                        MMOItems.plugin.getLogger().log(Level.SEVERE, "Config Update 3: Could not save config '" + listFile.getName() + "': " + exception.getMessage());
                     }
                  }
               }
            }
         )
      );
      this.register(
         new PluginUpdate(
            2,
            new String[]{"Enables the item updater for every item.", "&cNot recommended unless you know what you are doing.", "&e(No longer available)"},
            sender -> {
               sender.sendMessage(ChatColor.RED + "This command is no longer available.");
               sender.sendMessage(ChatColor.RED + "Please refer to the Revision System on the wiki.");
            }
         )
      );
      this.register(new PluginUpdate(5, new String[]{"Transition to trigger types in 6.6.3", "Only scans through item configs"}, sender -> {
         for (Type type : MMOItems.plugin.getTypes().getAll()) {
            ConfigFile configFile = type.getConfigFile();

            for (String key : configFile.getConfig().getKeys(false)) {
               ConfigurationSection section = configFile.getConfig().getConfigurationSection(key);
               if (section.contains("base.ability")) {
                  for (String abilityKey : configFile.getConfig().getConfigurationSection(key + ".base.ability").getKeys(false)) {
                     String text = configFile.getConfig().getString(key + ".base.ability." + abilityKey + ".mode");
                     if (text == null) {
                        return;
                     }

                     String newMode = text.equalsIgnoreCase("ON_HIT") ? "ATTACK" : (text.equalsIgnoreCase("WHEN_HIT") ? "DAMAGED" : text);
                     configFile.getConfig().set(key + ".base.ability." + abilityKey + ".mode", newMode);
                  }
               }
            }

            configFile.save();
         }
      }));
      this.register(
         new PluginUpdate(
            4,
            new String[]{
               "Transforms all your current MMOItems into item templates and fixes some stat formats which have been changed.",
               "&cIt is REALLY important to save a backup before using this config update!"
            },
            sender -> {
               for (Type type : MMOItems.plugin.getTypes().getAll()) {
                  ConfigFile configFile = type.getConfigFile();

                  for (String key : configFile.getConfig().getKeys(false)) {
                     if (!configFile.getConfig().getConfigurationSection(key).contains("base")) {
                        configFile.getConfig().createSection(key + ".base", configFile.getConfig().getConfigurationSection(key).getValues(false));

                        for (String entryKey : configFile.getConfig().getConfigurationSection(key).getKeys(false)) {
                           if (!entryKey.equals("base")) {
                              configFile.getConfig().set(key + "." + entryKey, null);
                           }
                        }

                        this.rename(configFile.getConfig().getConfigurationSection(key + ".base"), "regeneration", "health-regeneration");
                        this.rename(configFile.getConfig().getConfigurationSection(key + ".base"), "element.light", "element.lightness");
                        if (configFile.getConfig().getConfigurationSection(key + ".base").contains("consume-sound")) {
                           this.rename(configFile.getConfig().getConfigurationSection(key + ".base"), "consume-sound", "sounds.on-consume.sound");
                           configFile.getConfig().set(key + ".base.sounds.on-consume.volume", 1.0);
                           configFile.getConfig().set(key + ".base.sounds.on-consume.pitch", 1.0);
                        }

                        if (configFile.getConfig().getConfigurationSection(key + ".base").contains("effects")) {
                           for (String effectKey : configFile.getConfig().getConfigurationSection(key + ".base.effects").getKeys(false)) {
                              String[] texts = configFile.getConfig().getString(key + ".base.effects." + effectKey).split(",");
                              if (texts.length > 1) {
                                 configFile.getConfig().set(key + ".base.new-effects." + effectKey + ".duration", Double.parseDouble(texts[0]));
                                 configFile.getConfig().set(key + ".base.new-effects." + effectKey + ".amplifier", Double.parseDouble(texts[1]));
                              }
                           }

                           configFile.getConfig().set(key + ".base.effects", null);
                           this.rename(configFile.getConfig().getConfigurationSection(key + ".base"), "new-effects", "effects");
                        }

                        if (configFile.getConfig().getConfigurationSection(key + ".base").contains("restore")) {
                           configFile.getConfig().set(key + ".base.restore-health", configFile.getConfig().getDouble(key + ".base.restore.health"));
                           configFile.getConfig().set(key + ".base.restore-food", configFile.getConfig().getDouble(key + ".base.restore.food"));
                           configFile.getConfig().set(key + ".base.restore-saturation", configFile.getConfig().getDouble(key + ".base.restore.saturation"));
                           configFile.getConfig().set(key + ".base.restore", null);
                        }

                        for (String statKey : configFile.getConfig().getConfigurationSection(key + ".base").getKeys(false)) {
                           String text = configFile.getConfig().getString(key + ".base." + statKey);
                           if (text != null) {
                              try {
                                 String[] texts = text.split("=");
                                 Validate.isTrue(texts.length == 2);
                                 double value = Double.parseDouble(texts[0]);
                                 double maxValue = Double.parseDouble(texts[1]);
                                 double average = (value + maxValue) / 2.0;
                                 double largest = Math.max(Math.abs(value), Math.abs(maxValue));
                                 double spread = (largest - average) / largest;
                                 configFile.getConfig().set(key + ".base." + statKey + ".base", average);
                                 configFile.getConfig().set(key + ".base." + statKey + ".spread", spread / 3.0);
                                 configFile.getConfig().set(key + ".base." + statKey + ".max-spread", spread);
                              } catch (Exception exception) {
                              }
                           }
                        }
                     }
                  }

                  configFile.save();
               }
            }
         )
      );
      this.register(
         new PluginUpdate(
            6,
            new String[]{
               "MMOItems 6.7 introduced individual config files for skills. This update reads your previous language folder and applies it to the new individual config files.",
               "This can also be used to apply an old plugin translation"
            },
            sender -> {
               FileConfiguration config = new ConfigFile("/language", "abilities").getConfig();

               for (RegisteredSkill registeredSkill : MMOItems.plugin.getSkills().getAll()) {
                  ConfigFile configFile = new ConfigFile("/skill", registeredSkill.getHandler().getLowerCaseId());
                  FileConfiguration updatedConfig = configFile.getConfig();
                  updatedConfig.set("name", Objects.requireNonNullElse(config.getString("ability." + registeredSkill.getHandler().getLowerCaseId()), registeredSkill.getName()));

                  for (String modifier : registeredSkill.getHandler().getModifiers()) {
                     updatedConfig.set("modifier." + modifier + ".name", Objects.requireNonNullElse(config.getString("modifier." + modifier), registeredSkill.getParameterName(modifier)));
                  }

                  configFile.save();
               }

               sender.sendMessage("Config updates successfully applied, reloading skills..");
               MMOItems.plugin.getSkills().initialize(true);
            }
         )
      );
      this.register(
         new PluginUpdate(
            7,
            new String[]{
               "MI 6.7 introduced a 'timer' skill modifier for all skills.",
               "This update registers that modifier in every of your skills.",
               "Has to be ran after &6/mi update apply 6&7."
            },
            sender -> {
               for (SkillHandler handler : MythicLib.plugin.getSkills().getHandlers()) {
                  ConfigFile configFile = new ConfigFile("/skill", handler.getLowerCaseId());
                  configFile.getConfig().set("modifier.timer.name", "Timer");
                  configFile.getConfig().set("modifier.timer.default-value", 0.0);
                  configFile.save();
               }

               sender.sendMessage("Config updates successfully applied, reloading skills..");
               MMOItems.plugin.getSkills().initialize(true);
            }
         )
      );
   }

   public void register(PluginUpdate pluginUpdate) {
      this.updates.put(pluginUpdate.getId(), pluginUpdate);
   }

   public boolean has(int count) {
      return this.updates.containsKey(count);
   }

   public PluginUpdate get(int index) {
      return this.updates.get(index);
   }

   public Collection<PluginUpdate> getAll() {
      return this.updates.values();
   }

   private void rename(ConfigurationSection section, String text, String newPath) {
      if (section.contains(text)) {
         Object obj = section.get(text);
         section.set(text, null);
         section.set(newPath, obj);
      }
   }
}
