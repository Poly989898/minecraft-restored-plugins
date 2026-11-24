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
            var0 -> {
               for (Type var2 : MMOItems.plugin.getTypes().getAll()) {
                  ConfigFile var3 = var2.getConfigFile();

                  for (String var5 : var3.getConfig().getKeys(false)) {
                     ConfigurationSection var6 = var3.getConfig().getConfigurationSection(var5);
                     if (var6.contains("skull-texture") && var6.get("skull-texture") instanceof String) {
                        var6.set("skull-texture.value", var6.getString("skull-texture"));
                        var6.set("skull-texture.uuid", UUID.randomUUID().toString());
                     }
                  }

                  var3.save();
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
            var0 -> {
               for (File var4 : new File(MMOItems.plugin.getDataFolder() + "/crafting-stations").listFiles()) {
                  YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
                  if (var5.contains("recipes")) {
                     for (String var7 : var5.getConfigurationSection("recipes").getKeys(false)) {
                        try {
                           List<String> var8 = var5.getStringList("recipes." + var7 + ".ingredients");
                           ArrayList var9 = new ArrayList();

                           for (String var11 : var8) {
                              String[] var12 = var11.split(" ");
                              if (var12[0].equals("mmoitem")) {
                                 String var13 = "mmoitem{type=" + var12[1] + ",id=" + var12[2];
                                 if (var12.length > 3) {
                                    var13 = var13 + ",amount=" + var12[3];
                                 }

                                 if (var12.length > 4) {
                                    var13 = var13 + ",display=\"" + var12[4].replace("_", " ") + "\"";
                                 }

                                 var9.add(var13 + "}");
                              } else if (var12[0].equals("vanilla")) {
                                 String var20 = "vanilla{type=" + var12[1];
                                 if (var12.length > 2 && !var12[2].equals(".")) {
                                    var20 = var20 + ",name=\"" + var12[2] + "\"";
                                 }

                                 if (var12.length > 3) {
                                    var20 = var20 + ",amount=" + var12[3];
                                 }

                                 if (var12.length > 4) {
                                    var20 = var20 + ",display=\"" + var12[4].replace("_", " ") + "\"";
                                 }

                                 var9.add(var20 + "}");
                              } else {
                                 MMOItems.plugin
                                    .getLogger()
                                    .log(
                                       Level.SEVERE,
                                       "Config Update 3: Could not match ingredient from '" + var11 + "' from recipe '" + var7 + "', added it anyway."
                                    );
                                 var9.add(var11);
                              }
                           }

                           var5.set("recipes." + var7 + ".ingredients", var9);
                           List<String> var17 = var5.getStringList("recipes." + var7 + ".conditions");
                           var9 = new ArrayList();

                           for (String var19 : var17) {
                              String[] var21 = var19.split(" ");
                              if (var21[0].equalsIgnoreCase("class")) {
                                 var9.add("class{list=\"" + var19.replace(var21[0] + " ", "").replace(" ", ",") + "\"}");
                              } else if (var21[0].equalsIgnoreCase("perms")) {
                                 var9.add("permission{list=\"" + var19.replace(var21[0] + " ", "").replace(" ", ",") + "\"}");
                              } else if (var21[0].equalsIgnoreCase("food") || var21[0].equals("mana") || var21[0].equals("stamina")) {
                                 var9.add(var21[0] + "{amount=" + var21[1] + "}");
                              } else if (var21[0].equalsIgnoreCase("level")) {
                                 var9.add("level{level=" + var21[1] + "}");
                              } else if (var21[0].equalsIgnoreCase("profession")) {
                                 var9.add("profession{profession=" + var21[1] + ",level=" + var21[2] + "}");
                              } else if (var21[0].equalsIgnoreCase("exp")) {
                                 var9.add("exp{profession=" + var21[1] + ",amount=" + var21[2] + "}");
                              } else {
                                 MMOItems.plugin
                                    .getLogger()
                                    .log(
                                       Level.SEVERE,
                                       "Config Update 3: Could not match condition from '" + var19 + "' from recipe '" + var7 + "', added it anyway."
                                    );
                                 var9.add(var19);
                              }
                           }

                           var5.set("recipes." + var7 + ".conditions", var9);
                        } catch (Exception var15) {
                           MMOItems.plugin
                              .getLogger()
                              .log(Level.SEVERE, "Config Update 3: Could not convert recipe with key '" + var7 + "': " + var15.getMessage());
                        }
                     }

                     try {
                        var5.save(var4);
                     } catch (IOException var14) {
                        MMOItems.plugin.getLogger().log(Level.SEVERE, "Config Update 3: Could not save config '" + var4.getName() + "': " + var14.getMessage());
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
            var0 -> {
               var0.sendMessage(ChatColor.RED + "This command is no longer available.");
               var0.sendMessage(ChatColor.RED + "Please refer to the Revision System on the wiki.");
            }
         )
      );
      this.register(new PluginUpdate(5, new String[]{"Transition to trigger types in 6.6.3", "Only scans through item configs"}, var0 -> {
         for (Type var2 : MMOItems.plugin.getTypes().getAll()) {
            ConfigFile var3 = var2.getConfigFile();

            for (String var5 : var3.getConfig().getKeys(false)) {
               ConfigurationSection var6 = var3.getConfig().getConfigurationSection(var5);
               if (var6.contains("base.ability")) {
                  for (String var8 : var3.getConfig().getConfigurationSection(var5 + ".base.ability").getKeys(false)) {
                     String var9 = var3.getConfig().getString(var5 + ".base.ability." + var8 + ".mode");
                     if (var9 == null) {
                        return;
                     }

                     String var10 = var9.equalsIgnoreCase("ON_HIT") ? "ATTACK" : (var9.equalsIgnoreCase("WHEN_HIT") ? "DAMAGED" : var9);
                     var3.getConfig().set(var5 + ".base.ability." + var8 + ".mode", var10);
                  }
               }
            }

            var3.save();
         }
      }));
      this.register(
         new PluginUpdate(
            4,
            new String[]{
               "Transforms all your current MMOItems into item templates and fixes some stat formats which have been changed.",
               "&cIt is REALLY important to save a backup before using this config update!"
            },
            var1 -> {
               for (Type var3 : MMOItems.plugin.getTypes().getAll()) {
                  ConfigFile var4 = var3.getConfigFile();

                  for (String var6 : var4.getConfig().getKeys(false)) {
                     if (!var4.getConfig().getConfigurationSection(var6).contains("base")) {
                        var4.getConfig().createSection(var6 + ".base", var4.getConfig().getConfigurationSection(var6).getValues(false));

                        for (String var8 : var4.getConfig().getConfigurationSection(var6).getKeys(false)) {
                           if (!var8.equals("base")) {
                              var4.getConfig().set(var6 + "." + var8, null);
                           }
                        }

                        this.rename(var4.getConfig().getConfigurationSection(var6 + ".base"), "regeneration", "health-regeneration");
                        this.rename(var4.getConfig().getConfigurationSection(var6 + ".base"), "element.light", "element.lightness");
                        if (var4.getConfig().getConfigurationSection(var6 + ".base").contains("consume-sound")) {
                           this.rename(var4.getConfig().getConfigurationSection(var6 + ".base"), "consume-sound", "sounds.on-consume.sound");
                           var4.getConfig().set(var6 + ".base.sounds.on-consume.volume", 1.0);
                           var4.getConfig().set(var6 + ".base.sounds.on-consume.pitch", 1.0);
                        }

                        if (var4.getConfig().getConfigurationSection(var6 + ".base").contains("effects")) {
                           for (String var24 : var4.getConfig().getConfigurationSection(var6 + ".base.effects").getKeys(false)) {
                              String[] var9 = var4.getConfig().getString(var6 + ".base.effects." + var24).split(",");
                              if (var9.length > 1) {
                                 var4.getConfig().set(var6 + ".base.new-effects." + var24 + ".duration", Double.parseDouble(var9[0]));
                                 var4.getConfig().set(var6 + ".base.new-effects." + var24 + ".amplifier", Double.parseDouble(var9[1]));
                              }
                           }

                           var4.getConfig().set(var6 + ".base.effects", null);
                           this.rename(var4.getConfig().getConfigurationSection(var6 + ".base"), "new-effects", "effects");
                        }

                        if (var4.getConfig().getConfigurationSection(var6 + ".base").contains("restore")) {
                           var4.getConfig().set(var6 + ".base.restore-health", var4.getConfig().getDouble(var6 + ".base.restore.health"));
                           var4.getConfig().set(var6 + ".base.restore-food", var4.getConfig().getDouble(var6 + ".base.restore.food"));
                           var4.getConfig().set(var6 + ".base.restore-saturation", var4.getConfig().getDouble(var6 + ".base.restore.saturation"));
                           var4.getConfig().set(var6 + ".base.restore", null);
                        }

                        for (String var25 : var4.getConfig().getConfigurationSection(var6 + ".base").getKeys(false)) {
                           String var26 = var4.getConfig().getString(var6 + ".base." + var25);
                           if (var26 != null) {
                              try {
                                 String[] var10 = var26.split("=");
                                 Validate.isTrue(var10.length == 2);
                                 double var11 = Double.parseDouble(var10[0]);
                                 double var13 = Double.parseDouble(var10[1]);
                                 double var15 = (var11 + var13) / 2.0;
                                 double var17 = Math.max(Math.abs(var11), Math.abs(var13));
                                 double var19 = (var17 - var15) / var17;
                                 var4.getConfig().set(var6 + ".base." + var25 + ".base", var15);
                                 var4.getConfig().set(var6 + ".base." + var25 + ".spread", var19 / 3.0);
                                 var4.getConfig().set(var6 + ".base." + var25 + ".max-spread", var19);
                              } catch (Exception var21) {
                              }
                           }
                        }
                     }
                  }

                  var4.save();
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
            var0 -> {
               FileConfiguration var1 = new ConfigFile("/language", "abilities").getConfig();

               for (RegisteredSkill var3 : MMOItems.plugin.getSkills().getAll()) {
                  ConfigFile var4 = new ConfigFile("/skill", var3.getHandler().getLowerCaseId());
                  FileConfiguration var5 = var4.getConfig();
                  var5.set("name", Objects.requireNonNullElse(var1.getString("ability." + var3.getHandler().getLowerCaseId()), var3.getName()));

                  for (String var7 : var3.getHandler().getModifiers()) {
                     var5.set("modifier." + var7 + ".name", Objects.requireNonNullElse(var1.getString("modifier." + var7), var3.getParameterName(var7)));
                  }

                  var4.save();
               }

               var0.sendMessage("Config updates successfully applied, reloading skills..");
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
            var0 -> {
               for (SkillHandler var2 : MythicLib.plugin.getSkills().getHandlers()) {
                  ConfigFile var3 = new ConfigFile("/skill", var2.getLowerCaseId());
                  var3.getConfig().set("modifier.timer.name", "Timer");
                  var3.getConfig().set("modifier.timer.default-value", 0.0);
                  var3.save();
               }

               var0.sendMessage("Config updates successfully applied, reloading skills..");
               MMOItems.plugin.getSkills().initialize(true);
            }
         )
      );
   }

   public void register(PluginUpdate var1) {
      this.updates.put(var1.getId(), var1);
   }

   public boolean has(int var1) {
      return this.updates.containsKey(var1);
   }

   public PluginUpdate get(int var1) {
      return this.updates.get(var1);
   }

   public Collection<PluginUpdate> getAll() {
      return this.updates.values();
   }

   private void rename(ConfigurationSection var1, String var2, String var3) {
      if (var1.contains(var2)) {
         Object var4 = var1.get(var2);
         var1.set(var2, null);
         var1.set(var3, var4);
      }
   }
}
