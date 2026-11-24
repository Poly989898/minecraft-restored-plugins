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

   public void setKeepCase(@NotNull String var1) {
      this.keepCase = var1;
   }

   @NotNull
   public String getKeepCase() {
      return this.keepCase;
   }

   public boolean isBlacklisted(@NotNull String var1) {
      return this.blacklistedItems.contains(var1);
   }

   public void addToBlacklist(@NotNull String var1) {
      this.blacklistedItems.add(var1);
   }

   public void clearBlacklist() {
      this.blacklistedItems.clear();
   }

   public ReforgeOptions(ConfigurationSection var1) {
      this.keepName = var1.getBoolean("display-name");
      this.keepLore = var1.getBoolean("lore");
      this.keepEnchantments = var1.getBoolean("enchantments");
      this.keepUpgrades = var1.getBoolean("upgrades");
      this.keepGemStones = var1.getBoolean("gemstones", false) || var1.getBoolean("gems", false);
      this.keepSkins = var1.getBoolean("skins", false);
      this.keepSoulBind = var1.getBoolean("soulbound");
      this.keepCase = var1.getString("kept-lore-prefix", ChatColor.GRAY.toString());
      this.keepExternalSH = var1.getBoolean("external-sh", true);
      this.keepModifications = var1.getBoolean("modifications");
      this.reRoll = var1.getBoolean("reroll");
      this.keepAdvancedEnchantments = var1.getBoolean("advanced-enchantments");
      this.keepTier = var1.contains("tier") ? var1.getBoolean("tier", true) : null;
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

   boolean arr(@NotNull boolean[] var1, int var2) {
      return var1.length > var2 && var1[var2];
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
