package net.Indyuce.mmoitems.comp.placeholders;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.manager.StatManager;
import io.lumine.mythic.lib.util.DefenseFormula;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemsPlaceholders extends PlaceholderExpansion {
   public String getAuthor() {
      return "Indyuce";
   }

   public String getIdentifier() {
      return "mmoitems";
   }

   public String getVersion() {
      return MMOItems.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onRequest(@Nullable OfflinePlayer var1, @NotNull String var2) {
      if (var2.startsWith("type_")) {
         String var12 = var2.substring(5, var2.lastIndexOf("_")).toUpperCase();
         if (!MMOItems.plugin.getTypes().has(var12)) {
            return "Invalid type";
         }

         net.Indyuce.mmoitems.api.Type var13 = net.Indyuce.mmoitems.api.Type.get(var12);
         String var14 = var2.substring(6 + var12.length()).toLowerCase();
         return "total".equals(var14) ? "" + MMOItems.plugin.getTemplates().getTemplates(var13).size() : var13.getName();
      } else if (var1 == null) {
         return null;
      } else if (var2.equals("stat_defense_percent")) {
         double var11 = MMOPlayerData.get(var1).getStatMap().getStat("DEFENSE");
         double var5 = 100.0 - new DefenseFormula().getAppliedDamage(var11, 100.0);
         return MythicLib.plugin.getMMOConfig().decimal.format(var5);
      } else if (var2.startsWith("stat_")) {
         String var10 = UtilityMethods.enumName(var2.substring(5));
         return StatManager.format(var10, MMOPlayerData.get(var1));
      } else if (var2.startsWith("ability_cd_")) {
         return MythicLib.plugin.getMMOConfig().decimal.format(MMOPlayerData.get(var1).getCooldownMap().getCooldown("skill_" + var2.substring(11)));
      } else if (var2.startsWith("tier_")) {
         String var9 = var2.substring(5).toUpperCase();
         return !MMOItems.plugin.getTiers().has(var9) ? "Invalid tier" : MMOItems.plugin.getTiers().get(var9).getName();
      } else if (!var1.isOnline()) {
         return null;
      } else if (var2.equals("durability")) {
         NBTItem var8 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getPlayer().getInventory().getItemInMainHand());
         return String.valueOf(var8.hasTag("MMOITEMS_DURABILITY") ? var8.getInteger("MMOITEMS_DURABILITY") : var8.getInteger("MMOITEMS_MAX_DURABILITY"));
      } else if (var2.equals("durability_max")) {
         return ""
            + (int)MythicLib.plugin
               .getVersion()
               .getWrapper()
               .getNBTItem(var1.getPlayer().getInventory().getItemInMainHand())
               .getDouble("MMOITEMS_MAX_DURABILITY");
      } else if (var2.equals("durability_ratio")) {
         NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getPlayer().getInventory().getItemInMainHand());
         double var4 = var3.getDouble("MMOITEMS_DURABILITY");
         double var6 = var3.getDouble("MMOITEMS_MAX_DURABILITY");
         return MythicLib.plugin.getMMOConfig().decimal.format(var4 / var6 * 100.0);
      } else if (var2.equals("durability_bar_square")) {
         return this.getCurrentDurabilityBar(var1.getPlayer().getInventory().getItemInMainHand(), "█", 10);
      } else if (var2.equals("durability_bar_diamond")) {
         return this.getCurrentDurabilityBar(var1.getPlayer().getInventory().getItemInMainHand(), "◆", 15);
      } else {
         return var2.equals("durability_bar_thin") ? this.getCurrentDurabilityBar(var1.getPlayer().getInventory().getItemInMainHand(), "|", 20) : null;
      }
   }

   private String getCurrentDurabilityBar(ItemStack var1, String var2, int var3) {
      NBTItem var4 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1);
      double var5 = var4.getDouble("MMOITEMS_DURABILITY");
      double var7 = var4.getDouble("MMOITEMS_MAX_DURABILITY");
      long var9 = Math.round(var5 / var7 * var3);
      StringBuilder var11 = new StringBuilder("" + ChatColor.GREEN);

      for (int var12 = 0; var12 < var3; var12++) {
         var11.append(var12 == var9 ? ChatColor.WHITE : "").append(var2);
      }

      return var11.toString();
   }

   private boolean hasItem(Player var1, EquipmentSlot var2) {
      return var1.getInventory().getItem(var2) != null && var1.getInventory().getItem(var2).getType() != Material.AIR;
   }
}
