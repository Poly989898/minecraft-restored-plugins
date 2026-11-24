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
      File var1 = new File(MMOItems.plugin.getDataFolder() + "/modifiers");
      if (!var1.exists()) {
         this.mkdir("modifiers");
         ConfigManager.DefaultFile.EXAMPLE_MODIFIERS.checkFile();
      }

      File var2 = new File(MMOItems.plugin.getDataFolder() + "/crafting-stations");
      if (!var2.exists()) {
         if (var2.mkdir()) {
            try {
               JarFile var3 = new JarFile(MMOItems.plugin.getJarFile());
               Enumeration var4 = var3.entries();

               while (var4.hasMoreElements()) {
                  String var5 = ((JarEntry)var4.nextElement()).getName();
                  if (var5.startsWith("default/crafting-stations/") && var5.length() > "default/crafting-stations/".length()) {
                     Files.copy(
                        MMOItems.plugin.getResource(var5), new File(MMOItems.plugin.getDataFolder() + "/crafting-stations", var5.split("/")[2]).toPath()
                     );
                  }
               }

               var3.close();
            } catch (IOException var7) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load default crafting stations.");
            }
         } else {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not create directory!");
         }
      }

      for (ConfigManager.DefaultFile var6 : ConfigManager.DefaultFile.values()) {
         if (var6.isAutomatic()) {
            var6.checkFile();
         }
      }

      MMOItems.plugin.getTypes().getAll().forEach(var0 -> var0.getConfigFile().setup());
      this.reload();
   }

   private void loadTranslations() {
      ConfigFile var1 = new ConfigFile("/language", "items");

      for (ConfigItem var5 : ConfigItems.values) {
         if (!var1.getConfig().contains(var5.getId())) {
            var1.getConfig().createSection(var5.getId());
            var5.setup(var1.getConfig().getConfigurationSection(var5.getId()));
         }

         var5.update(var1.getConfig());
      }

      var1.save();
      LanguageFile var10 = new LanguageFile("messages");

      for (Message var6 : Message.values()) {
         String var7 = var6.name().toLowerCase().replace("_", "-");
         if (!var10.getConfig().contains(var7)) {
            var10.getConfig().set(var7, var6.getDefault());
         }

         var6.setRaw(var10.getConfig().getString(var7));
      }

      var10.save();
      FileConfiguration var12 = new ConfigFile("/language", "stats").getConfig();
      this.itemTypeLoreTag = var12.getString("item-type", "");
      this.gemStoneLoreTag = var12.getString("gem-stone-lore", "");
      LanguageFile var14 = new LanguageFile("potion-effects");

      for (PotionEffectType var8 : PotionEffectType.values()) {
         this.potionNames
            .put(
               var8,
               var14.computeTranslation(
                  var8.getName().toLowerCase().replace("_", "-"), () -> UtilityMethods.caseOnWords(var8.getName().toLowerCase().replace("_", " "))
               )
            );
      }

      var14.save();
      LanguageFile var17 = new LanguageFile("attack-effects");

      for (LuteAttackEffectStat.LuteAttackEffect var9 : LuteAttackEffectStat.LuteAttackEffect.values()) {
         var9.setName(
            var17.computeTranslation(
               "lute-attack." + var9.name().toLowerCase().replace("_", "-"), () -> UtilityMethods.caseOnWords(var9.name().toLowerCase().replace("_", " "))
            )
         );
      }

      var17.save();
      this.triggerTypeNames.clear();
      FileConfiguration var20 = new ConfigFile("/language", "abilities").getConfig();

      for (TriggerType var25 : TriggerType.values()) {
         this.triggerTypeNames.put(var25, var20.getString("cast-mode." + var25.getLowerCaseId(), var25.getName()));
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

      for (String var2 : MMOItems.plugin.getConfig().getStringList("op-item-stats.stats")) {
         this.opStats.add(UtilityMethods.enumName(var2));
      }

      ConfigurationSection var11 = MMOItems.plugin.getConfig().getConfigurationSection("item-revision.keep-data");
      ConfigurationSection var12 = MMOItems.plugin.getConfig().getConfigurationSection("item-revision.phat-loots");
      ConfigurationSection var3 = MMOItems.plugin.getConfig().getConfigurationSection("item-revision.keep-gem-data");
      ReforgeOptions.dropRestoredGems = MMOItems.plugin.getConfig().getBoolean("item-revision.drop-extra-gems", true);
      this.revisionOptions = var11 != null ? new ReforgeOptions(var11) : new ReforgeOptions();
      this.gemRevisionOptions = var3 != null ? new ReforgeOptions(var3) : new ReforgeOptions();
      this.phatLootsOptions = var12 != null ? new ReforgeOptions(var12) : new ReforgeOptions();

      for (String var6 : MMOItems.plugin.getConfig().getStringList("item-revision.disable-phat-loot")) {
         this.phatLootsOptions.addToBlacklist(var6);
      }

      try {
         this.defaultItemCapacity = new NumericStatFormula(MMOItems.plugin.getConfig().getConfigurationSection("default-item-capacity"));
      } catch (IllegalArgumentException var10) {
         this.defaultItemCapacity = new NumericStatFormula(5.0, 0.05, 0.1, 0.3);
         MMOItems.plugin
            .getLogger()
            .log(Level.SEVERE, "An error occurred while trying to load default capacity formula for the item generator, using default: " + var10.getMessage());
      }

      ConfigFile var13 = new ConfigFile("/language", "items");

      for (ConfigItem var9 : ConfigItems.values) {
         var9.update(var13.getConfig().getConfigurationSection(var9.getId()));
      }
   }

   public boolean isBlacklisted(@NotNull Material var1) {
      return MMOItems.plugin.getConfig().getStringList("block-blacklist").contains(var1.name());
   }

   @Deprecated
   @NotNull
   public String getStatFormat(String var1) {
      FileConfiguration var2 = new ConfigFile("/language", "stats").getConfig();
      String var3 = var2.getString(var1);
      return var3 == null ? "<TranslationNotFound:" + var1 + ">" : var3;
   }

   @Deprecated
   public String getMessage(String var1) {
      return Message.valueOf(UtilityMethods.enumName(var1)).getFormatted();
   }

   @Deprecated
   @NotNull
   public String getCastingModeName(@NotNull TriggerType var1) {
      return this.getTriggerTypeName(var1);
   }

   @NotNull
   public String getTriggerTypeName(@NotNull TriggerType var1) {
      return Objects.requireNonNull(this.triggerTypeNames.get(var1), "Trigger type name for '" + var1.name() + "' not found");
   }

   @Deprecated
   public String getModifierName(String var1) {
      return UtilityMethods.caseOnWords(var1.toLowerCase().replace("-", " ").replace("_", " "));
   }

   @NotNull
   public List<String> getDefaultLoreFormat() {
      return this.loreFormat.getConfig().getStringList("lore-format");
   }

   @NotNull
   public String getPotionEffectName(PotionEffectType var1) {
      return Objects.requireNonNull(this.potionNames.get(var1), "Potion effect name for '" + var1.getName() + "' not found");
   }

   @Deprecated
   public String getLuteAttackEffectName(LuteAttackEffectStat.LuteAttackEffect var1) {
      return var1.getName();
   }

   @Deprecated
   public String getDynLoreFormat(String var1) {
      return this.dynLore.getConfig().getString("format." + var1);
   }

   private void mkdir(String var1) {
      File var2 = new File(MMOItems.plugin.getDataFolder() + "/" + var1);
      if (!var2.exists() && !var2.mkdir()) {
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
         File var1 = this.getFile();
         if (!var1.exists()) {
            try {
               if (!new ConfigManager.YamlConverter(var1).convert()) {
                  Files.copy(
                     MMOItems.plugin.getResource("default/" + (this.folderPath.isEmpty() ? "" : this.folderPath + "/") + this.fileName),
                     var1.getAbsoluteFile().toPath()
                  );
               }
            } catch (IOException var3) {
               var3.printStackTrace();
            }
         }
      }
   }

   public static class YamlConverter {
      private final File file;
      private final String fileName;

      public YamlConverter(File var1) {
         this.file = var1;
         this.fileName = var1.getName();
      }

      /**
       * The original method was compiled with a sneaky-throws wrapper: it declares no checked
       * exception but lets the IOException of the file operations escape unchanged.
       */
      public boolean convert() {
         try {
            return this.convertFile();
         } catch (IOException var1) {
            YamlConverter.<RuntimeException>sneakyThrow(var1);
            return false;
         }
      }

      @SuppressWarnings("unchecked")
      private static <E extends Throwable> void sneakyThrow(Throwable var0) throws E {
         throw (E)var0;
      }

      private boolean convertFile() throws IOException {
         if (!this.file.exists()
            && this.fileName.equalsIgnoreCase("block.yml")
            && new File(MMOItems.plugin.getDataFolder(), "custom-blocks.yml").exists()
            && this.file.createNewFile()) {
            YamlConfiguration var1 = YamlConfiguration.loadConfiguration(new File(MMOItems.plugin.getDataFolder(), "custom-blocks.yml"));

            for (String var3 : var1.getKeys(false)) {
               ConfigurationSection var4 = var1.getConfigurationSection(var3);
               var4.set("material", "STONE");
               var4.set("block-id", Integer.parseInt(var3));

               for (String var6 : var4.getKeys(false)) {
                  Object var7 = var4.get(var6);
                  if (var6.equalsIgnoreCase("display-name")) {
                     var4.set("display-name", null);
                     var4.set("name", var7);
                  }
               }
            }

            var1.save(this.file);
            MMOItems.plugin.getLogger().log(Level.CONFIG, "Successfully converted custom-blocks.yml");
            return true;
         } else {
            return false;
         }
      }
   }
}
