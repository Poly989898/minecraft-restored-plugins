package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.ReforgeOptions;
import net.Indyuce.mmoitems.api.item.util.ConfigItem;
import net.Indyuce.mmoitems.api.item.util.ConfigItems;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.GemUpgradeScaling;
import net.Indyuce.mmoitems.stat.LuteAttackEffectStat;
import net.Indyuce.mmoitems.util.LanguageFile;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ConfigManager implements Reloadable {
   private ConfigFile loreFormat;
   private ConfigFile dynLore;
   private final Map<TriggerType, String> triggerTypeNames = new HashMap<>();
   private final Map<PotionEffectType, String> potionNames = new HashMap<>();
   public boolean replaceMushroomDrops;
   public boolean worldGenEnabled;
   public boolean upgradeRequirementsCheck;
   public boolean keepSoulboundOnDeath;
   public boolean rerollOnItemUpdate;
   public boolean opStatsEnabled;
   public boolean disableRemovedItems;
   public double soulboundBaseDamage;
   public double soulboundPerLvlDamage;
   public double levelSpread;
   public NumericStatFormula defaultItemCapacity;
   public ReforgeOptions revisionOptions;
   public ReforgeOptions gemRevisionOptions;
   public ReforgeOptions phatLootsOptions;
   public final List<String> opStats = new ArrayList<>();
   public String itemTypeLoreTag;
   public String gemStoneLoreTag;
   public String defaultTierName;

   public ConfigManager() {
      this.mkdir("layouts");
      this.mkdir("item");
      this.mkdir("language");
      this.mkdir("language/lore-formats");
      File file = new File(MMOItems.plugin.getDataFolder() + "/modifiers");
      if (!file.exists()) {
         this.mkdir("modifiers");
         ConfigManager.DefaultFile.EXAMPLE_MODIFIERS.checkFile();
      }

      File file2 = new File(MMOItems.plugin.getDataFolder() + "/crafting-stations");
      if (!file2.exists()) {
         if (file2.mkdir()) {
            try {
               JarFile jarFile = new JarFile(MMOItems.plugin.getJarFile());
               Enumeration enumeration = jarFile.entries();

               while (enumeration.hasMoreElements()) {
                  String name = ((JarEntry)enumeration.nextElement()).getName();
                  if (name.startsWith("default/crafting-stations/") && name.length() > "default/crafting-stations/".length()) {
                     Files.copy(
                        MMOItems.plugin.getResource(name), new File(MMOItems.plugin.getDataFolder() + "/crafting-stations", name.split("/")[2]).toPath()
                     );
                  }
               }

               jarFile.close();
            } catch (IOException exception) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load default crafting stations.");
            }
         } else {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not create directory!");
         }
      }

      for (ConfigManager.DefaultFile defaultFile : ConfigManager.DefaultFile.values()) {
         if (defaultFile.isAutomatic()) {
            defaultFile.checkFile();
         }
      }

      MMOItems.plugin.getTypes().getAll().forEach(type -> type.getConfigFile().setup());
      this.reload();
   }

   private void loadTranslations() {
      ConfigFile configFile = new ConfigFile("/language", "items");

      for (ConfigItem configItem : ConfigItems.values) {
         if (!configFile.getConfig().contains(configItem.getId())) {
            configFile.getConfig().createSection(configItem.getId());
            configItem.setup(configFile.getConfig().getConfigurationSection(configItem.getId()));
         }

         configItem.update(configFile.getConfig());
      }

      configFile.save();
      LanguageFile languageFile = new LanguageFile("messages");

      for (Message message : Message.values()) {
         String text = message.name().toLowerCase().replace("_", "-");
         if (!languageFile.getConfig().contains(text)) {
            languageFile.getConfig().set(text, message.getDefault());
         }

         message.setRaw(languageFile.getConfig().getString(text));
      }

      languageFile.save();
      FileConfiguration config = new ConfigFile("/language", "stats").getConfig();
      this.itemTypeLoreTag = config.getString("item-type", "");
      this.gemStoneLoreTag = config.getString("gem-stone-lore", "");
      LanguageFile languageFile2 = new LanguageFile("potion-effects");

      for (PotionEffectType potionEffectType : PotionEffectType.values()) {
         this.potionNames
            .put(
               potionEffectType,
               languageFile2.computeTranslation(
                  potionEffectType.getName().toLowerCase().replace("_", "-"), () -> UtilityMethods.caseOnWords(potionEffectType.getName().toLowerCase().replace("_", " "))
               )
            );
      }

      languageFile2.save();
      LanguageFile languageFile3 = new LanguageFile("attack-effects");

      for (LuteAttackEffectStat.LuteAttackEffect luteAttackEffect : LuteAttackEffectStat.LuteAttackEffect.values()) {
         luteAttackEffect.setName(
            languageFile3.computeTranslation(
               "lute-attack." + luteAttackEffect.name().toLowerCase().replace("_", "-"), () -> UtilityMethods.caseOnWords(luteAttackEffect.name().toLowerCase().replace("_", " "))
            )
         );
      }

      languageFile3.save();
      this.triggerTypeNames.clear();
      FileConfiguration config2 = new ConfigFile("/language", "abilities").getConfig();

      for (TriggerType triggerType : TriggerType.values()) {
         this.triggerTypeNames.put(triggerType, config2.getString("cast-mode." + triggerType.getLowerCaseId(), triggerType.getName()));
      }
   }

   @Override
   public void reload() {
      MMOItems.plugin.reloadConfig();
      this.loreFormat = new ConfigFile("/language", "lore-format");
      this.dynLore = new ConfigFile("/language", "dynamic-lore");
      this.loadTranslations();
      this.replaceMushroomDrops = MMOItems.plugin.getConfig().getBoolean("custom-blocks.replace-mushroom-drops");
      this.worldGenEnabled = MMOItems.plugin.getConfig().getBoolean("custom-blocks.enable-world-gen");
      this.soulboundBaseDamage = MMOItems.plugin.getConfig().getDouble("soulbound.damage.base");
      this.soulboundPerLvlDamage = MMOItems.plugin.getConfig().getDouble("soulbound.damage.per-lvl");
      this.upgradeRequirementsCheck = MMOItems.plugin.getConfig().getBoolean("item-upgrade-requirements-check");
      GemUpgradeScaling.defaultValue = MMOItems.plugin.getConfig().getString("gem-upgrade-default", GemUpgradeScaling.SUBSEQUENT.getId());
      this.keepSoulboundOnDeath = MMOItems.plugin.getConfig().getBoolean("soulbound.keep-on-death");
      this.rerollOnItemUpdate = MMOItems.plugin.getConfig().getBoolean("item-revision.reroll-when-updated");
      this.levelSpread = MMOItems.plugin.getConfig().getDouble("item-level-spread");
      this.disableRemovedItems = MMOItems.plugin.getConfig().getBoolean("disable-removed-items");
      this.defaultTierName = MMOItems.plugin.getConfig().getString("default-tier-name");
      NumericStatFormula.RELATIVE_SPREAD = !MMOItems.plugin.getConfig().getBoolean("additive-spread-formula", false);
      this.opStatsEnabled = MMOItems.plugin.getConfig().getBoolean("op-item-stats.enabled");
      this.opStats.clear();

      for (String text : MMOItems.plugin.getConfig().getStringList("op-item-stats.stats")) {
         this.opStats.add(UtilityMethods.enumName(text));
      }

      ConfigurationSection section = MMOItems.plugin.getConfig().getConfigurationSection("item-revision.keep-data");
      ConfigurationSection section2 = MMOItems.plugin.getConfig().getConfigurationSection("item-revision.phat-loots");
      ConfigurationSection section3 = MMOItems.plugin.getConfig().getConfigurationSection("item-revision.keep-gem-data");
      ReforgeOptions.dropRestoredGems = MMOItems.plugin.getConfig().getBoolean("item-revision.drop-extra-gems", true);
      this.revisionOptions = section != null ? new ReforgeOptions(section) : new ReforgeOptions();
      this.gemRevisionOptions = section3 != null ? new ReforgeOptions(section3) : new ReforgeOptions();
      this.phatLootsOptions = section2 != null ? new ReforgeOptions(section2) : new ReforgeOptions();

      for (String text : MMOItems.plugin.getConfig().getStringList("item-revision.disable-phat-loot")) {
         this.phatLootsOptions.addToBlacklist(text);
      }

      try {
         this.defaultItemCapacity = new NumericStatFormula(MMOItems.plugin.getConfig().getConfigurationSection("default-item-capacity"));
      } catch (IllegalArgumentException exception) {
         this.defaultItemCapacity = new NumericStatFormula(5.0, 0.05, 0.1, 0.3);
         MMOItems.plugin
            .getLogger()
            .log(Level.SEVERE, "An error occurred while trying to load default capacity formula for the item generator, using default: " + exception.getMessage());
      }

      ConfigFile configFile = new ConfigFile("/language", "items");

      for (ConfigItem configItem : ConfigItems.values) {
         configItem.update(configFile.getConfig().getConfigurationSection(configItem.getId()));
      }
   }

   public boolean isBlacklisted(@NotNull Material material) {
      return MMOItems.plugin.getConfig().getStringList("block-blacklist").contains(material.name());
   }

   @Deprecated
   @NotNull
   public String getStatFormat(String text) {
      FileConfiguration config = new ConfigFile("/language", "stats").getConfig();
      String text2 = config.getString(text);
      return text2 == null ? "<TranslationNotFound:" + text + ">" : text2;
   }

   @Deprecated
   public String getMessage(String text) {
      return Message.valueOf(UtilityMethods.enumName(text)).getFormatted();
   }

   @Deprecated
   @NotNull
   public String getCastingModeName(@NotNull TriggerType triggerType) {
      return this.getTriggerTypeName(triggerType);
   }

   @NotNull
   public String getTriggerTypeName(@NotNull TriggerType triggerType) {
      return Objects.requireNonNull(this.triggerTypeNames.get(triggerType), "Trigger type name for '" + triggerType.name() + "' not found");
   }

   @Deprecated
   public String getModifierName(String text) {
      return UtilityMethods.caseOnWords(text.toLowerCase().replace("-", " ").replace("_", " "));
   }

   @NotNull
   public List<String> getDefaultLoreFormat() {
      return this.loreFormat.getConfig().getStringList("lore-format");
   }

   @NotNull
   public String getPotionEffectName(PotionEffectType potionEffectType) {
      return Objects.requireNonNull(this.potionNames.get(potionEffectType), "Potion effect name for '" + potionEffectType.getName() + "' not found");
   }

   @Deprecated
   public String getLuteAttackEffectName(LuteAttackEffectStat.LuteAttackEffect luteAttackEffect) {
      return luteAttackEffect.getName();
   }

   @Deprecated
   public String getDynLoreFormat(String text) {
      return this.dynLore.getConfig().getString("format." + text);
   }

   private void mkdir(String text) {
      File file = new File(MMOItems.plugin.getDataFolder() + "/" + text);
      if (!file.exists() && !file.mkdir()) {
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not create directory!");
      }
   }

   public enum DefaultFile {
      ITEM_TIERS("", "item-tiers"),
      ITEM_TYPES("", "item-types", true),
      DROPS("", "drops"),
      ITEM_SETS("", "item-sets"),
      GEN_TEMPLATES("", "gen-templates"),
      UPGRADE_TEMPLATES("", "upgrade-templates"),
      EXAMPLE_MODIFIERS("modifiers", "example_modifiers", true),
      CUSTOM_STATS("", "custom-stats"),
      ABILITIES("language", "abilities"),
      ATTACK_EFFECTS("language", "attack-effects"),
      CRAFTING_STATIONS("language", "crafting-stations"),
      ITEMS("language", "items"),
      LORE_FORMAT("language", "lore-format"),
      MESSAGES("language", "messages"),
      POTION_EFFECTS("language", "potion-effects"),
      STATS("language", "stats"),
      DEFAULT_LAYOUT("layouts", "default"),
      EXPANDED_LAYOUT("layouts", "expanded"),
      ARMOR,
      AXE,
      BLOCK,
      BOW,
      CATALYST,
      CONSUMABLE,
      CROSSBOW,
      DAGGER,
      GAUNTLET,
      GEM_STONE,
      GREATAXE,
      GREATHAMMER,
      GREATSTAFF,
      GREATSWORD,
      HALBERD,
      HAMMER,
      KATANA,
      LANCE,
      LONG_SWORD,
      MATERIAL,
      MISCELLANEOUS,
      MUSKET,
      OFF_CATALYST,
      ORNAMENT,
      SHIELD,
      SPEAR,
      STAFF,
      SWORD,
      TALISMAN,
      THRUSTING_SWORD,
      TOME,
      TOOL,
      WAND,
      WHIP;

      private final String folderPath;
      private final String fileName;
      private final boolean manual;

      DefaultFile() {
         this.fileName = this.name().toLowerCase() + ".yml";
         this.folderPath = "item";
         this.manual = false;
      }

      DefaultFile(String nullxx, String nullxxx) {
         this(nullxx, nullxxx, false);
      }

      DefaultFile(String nullxx, String nullxxx, boolean nullxxxx) {
         this.folderPath = nullxx;
         this.fileName = nullxxx + ".yml";
         this.manual = nullxxxx;
      }

      public boolean isAutomatic() {
         return !this.manual;
      }

      public File getFile() {
         return new File(MMOItems.plugin.getDataFolder() + (this.folderPath.equals("") ? "" : "/" + this.folderPath), this.fileName);
      }

      public void checkFile() {
         File file = this.getFile();
         if (!file.exists()) {
            try {
               if (!new ConfigManager.YamlConverter(file).convert()) {
                  Files.copy(
                     MMOItems.plugin.getResource("default/" + (this.folderPath.isEmpty() ? "" : this.folderPath + "/") + this.fileName),
                     file.getAbsoluteFile().toPath()
                  );
               }
            } catch (IOException exception) {
               exception.printStackTrace();
            }
         }
      }
   }

   public static class YamlConverter {
      private final File file;
      private final String fileName;

      public YamlConverter(File file) {
         this.file = file;
         this.fileName = file.getName();
      }

      /**
       * The original method was compiled with a sneaky-throws wrapper: it declares no checked
       * exception but lets the IOException of the file operations escape unchanged.
       */
      public boolean convert() {
         try {
            return this.convertFile();
         } catch (IOException exception) {
            YamlConverter.<RuntimeException>sneakyThrow(exception);
            return false;
         }
      }

      @SuppressWarnings("unchecked")
      private static <E extends Throwable> void sneakyThrow(Throwable exception) throws E {
         throw (E)exception;
      }

      private boolean convertFile() throws IOException {
         if (!this.file.exists()
            && this.fileName.equalsIgnoreCase("block.yml")
            && new File(MMOItems.plugin.getDataFolder(), "custom-blocks.yml").exists()
            && this.file.createNewFile()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(MMOItems.plugin.getDataFolder(), "custom-blocks.yml"));

            for (String key : config.getKeys(false)) {
               ConfigurationSection section = config.getConfigurationSection(key);
               section.set("material", "STONE");
               section.set("block-id", Integer.parseInt(key));

               for (String key2 : section.getKeys(false)) {
                  Object obj = section.get(key2);
                  if (key2.equalsIgnoreCase("display-name")) {
                     section.set("display-name", null);
                     section.set("name", obj);
                  }
               }
            }

            config.save(this.file);
            MMOItems.plugin.getLogger().log(Level.CONFIG, "Successfully converted custom-blocks.yml");
            return true;
         } else {
            return false;
         }
      }
   }
}
