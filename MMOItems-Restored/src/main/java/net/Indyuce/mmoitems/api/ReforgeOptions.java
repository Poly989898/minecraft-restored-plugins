package net.Indyuce.mmoitems.api;

import java.util.ArrayList;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReforgeOptions {
   public static boolean dropRestoredGems;
   private final boolean keepName;
   private final boolean keepLore;
   private final boolean keepEnchantments;
   private final boolean keepSkins;
   private final boolean keepUpgrades;
   private final boolean keepGemStones;
   private final boolean keepSoulBind;
   private final boolean keepExternalSH;
   private final boolean keepModifications;
   @Nullable
   private final Boolean keepTier;
   private final boolean reRoll;
   private final boolean keepAdvancedEnchantments;
   @NotNull
   String keepCase = ChatColor.GRAY.toString();
   @NotNull
   ArrayList<String> blacklistedItems = new ArrayList<>();

   public void setKeepCase(@NotNull String text) {
      this.keepCase = text;
   }

   @NotNull
   public String getKeepCase() {
      return this.keepCase;
   }

   public boolean isBlacklisted(@NotNull String text) {
      return this.blacklistedItems.contains(text);
   }

   public void addToBlacklist(@NotNull String text) {
      this.blacklistedItems.add(text);
   }

   public void clearBlacklist() {
      this.blacklistedItems.clear();
   }

   public ReforgeOptions(ConfigurationSection section) {
      this.keepName = section.getBoolean("display-name");
      this.keepLore = section.getBoolean("lore");
      this.keepEnchantments = section.getBoolean("enchantments");
      this.keepUpgrades = section.getBoolean("upgrades");
      this.keepGemStones = section.getBoolean("gemstones", false) || section.getBoolean("gems", false);
      this.keepSkins = section.getBoolean("skins", false);
      this.keepSoulBind = section.getBoolean("soulbound");
      this.keepCase = section.getString("kept-lore-prefix", ChatColor.GRAY.toString());
      this.keepExternalSH = section.getBoolean("external-sh", true);
      this.keepModifications = section.getBoolean("modifications");
      this.reRoll = section.getBoolean("reroll");
      this.keepAdvancedEnchantments = section.getBoolean("advanced-enchantments");
      this.keepTier = section.contains("tier") ? section.getBoolean("tier", true) : null;
   }

   public ReforgeOptions() {
      this.keepName = false;
      this.keepLore = false;
      this.keepEnchantments = true;
      this.keepUpgrades = true;
      this.keepGemStones = true;
      this.keepSoulBind = true;
      this.keepExternalSH = true;
      this.reRoll = false;
      this.keepModifications = true;
      this.keepAdvancedEnchantments = true;
      this.keepSkins = true;
      this.keepTier = true;
   }

   boolean arr(@NotNull boolean[] flags, int index) {
      return flags.length > index && flags[index];
   }

   public boolean shouldReRoll() {
      return this.reRoll;
   }

   public boolean shouldKeepName() {
      return this.keepName;
   }

   public boolean shouldKeepMods() {
      return this.keepModifications;
   }

   public boolean shouldKeepLore() {
      return this.keepLore;
   }

   public boolean shouldKeepSkins() {
      return this.keepSkins;
   }

   public boolean shouldKeepTier() {
      return this.keepTier == null ? MMOItemReforger.keepTiersWhenReroll : this.keepTier;
   }

   public boolean shouldKeepEnchantments() {
      return this.keepEnchantments;
   }

   public boolean shouldKeepAdvancedEnchants() {
      return this.keepAdvancedEnchantments;
   }

   public boolean shouldKeepExternalSH() {
      return this.keepExternalSH;
   }

   public boolean shouldKeepUpgrades() {
      return this.keepUpgrades;
   }

   public boolean shouldKeepGemStones() {
      return this.keepGemStones;
   }

   public boolean shouldKeepSoulBind() {
      return this.keepSoulBind;
   }
}
