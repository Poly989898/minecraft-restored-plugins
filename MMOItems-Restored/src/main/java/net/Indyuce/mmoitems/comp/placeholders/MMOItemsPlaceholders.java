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

   public String onRequest(@Nullable OfflinePlayer offlinePlayer, @NotNull String text) {
      if (text.startsWith("type_")) {
         String typeId = text.substring(5, text.lastIndexOf("_")).toUpperCase();
         if (!MMOItems.plugin.getTypes().has(typeId)) {
            return "Invalid type";
         }

         net.Indyuce.mmoitems.api.Type type = net.Indyuce.mmoitems.api.Type.get(typeId);
         String itemId = text.substring(6 + typeId.length()).toLowerCase();
         return "total".equals(itemId) ? "" + MMOItems.plugin.getTemplates().getTemplates(type).size() : type.getName();
      } else if (offlinePlayer == null) {
         return null;
      } else if (text.equals("stat_defense_percent")) {
         double stat = MMOPlayerData.get(offlinePlayer).getStatMap().getStat("DEFENSE");
         double value = 100.0 - new DefenseFormula().getAppliedDamage(stat, 100.0);
         return MythicLib.plugin.getMMOConfig().decimal.format(value);
      } else if (text.startsWith("stat_")) {
         String typeId = UtilityMethods.enumName(text.substring(5));
         return StatManager.format(typeId, MMOPlayerData.get(offlinePlayer));
      } else if (text.startsWith("ability_cd_")) {
         return MythicLib.plugin.getMMOConfig().decimal.format(MMOPlayerData.get(offlinePlayer).getCooldownMap().getCooldown("skill_" + text.substring(11)));
      } else if (text.startsWith("tier_")) {
         String typeId = text.substring(5).toUpperCase();
         return !MMOItems.plugin.getTiers().has(typeId) ? "Invalid tier" : MMOItems.plugin.getTiers().get(typeId).getName();
      } else if (!offlinePlayer.isOnline()) {
         return null;
      } else if (text.equals("durability")) {
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(offlinePlayer.getPlayer().getInventory().getItemInMainHand());
         return String.valueOf(nBTItem.hasTag("MMOITEMS_DURABILITY") ? nBTItem.getInteger("MMOITEMS_DURABILITY") : nBTItem.getInteger("MMOITEMS_MAX_DURABILITY"));
      } else if (text.equals("durability_max")) {
         return ""
            + (int)MythicLib.plugin
               .getVersion()
               .getWrapper()
               .getNBTItem(offlinePlayer.getPlayer().getInventory().getItemInMainHand())
               .getDouble("MMOITEMS_MAX_DURABILITY");
      } else if (text.equals("durability_ratio")) {
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(offlinePlayer.getPlayer().getInventory().getItemInMainHand());
         double mMOITEMSDURABILITY = nBTItem.getDouble("MMOITEMS_DURABILITY");
         double mMOITEMSMAXDURABILITY = nBTItem.getDouble("MMOITEMS_MAX_DURABILITY");
         return MythicLib.plugin.getMMOConfig().decimal.format(mMOITEMSDURABILITY / mMOITEMSMAXDURABILITY * 100.0);
      } else if (text.equals("durability_bar_square")) {
         return this.getCurrentDurabilityBar(offlinePlayer.getPlayer().getInventory().getItemInMainHand(), "█", 10);
      } else if (text.equals("durability_bar_diamond")) {
         return this.getCurrentDurabilityBar(offlinePlayer.getPlayer().getInventory().getItemInMainHand(), "◆", 15);
      } else {
         return text.equals("durability_bar_thin") ? this.getCurrentDurabilityBar(offlinePlayer.getPlayer().getInventory().getItemInMainHand(), "|", 20) : null;
      }
   }

   private String getCurrentDurabilityBar(ItemStack item, String text, int count) {
      NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(item);
      double mMOITEMSDURABILITY = nBTItem.getDouble("MMOITEMS_DURABILITY");
      double mMOITEMSMAXDURABILITY = nBTItem.getDouble("MMOITEMS_MAX_DURABILITY");
      long value = Math.round(mMOITEMSDURABILITY / mMOITEMSMAXDURABILITY * count);
      StringBuilder builder = new StringBuilder("" + ChatColor.GREEN);

      for (int i = 0; i < count; i++) {
         builder.append(i == value ? ChatColor.WHITE : "").append(text);
      }

      return builder.toString();
   }

   private boolean hasItem(Player player, EquipmentSlot equipmentSlot) {
      return player.getInventory().getItem(equipmentSlot) != null && player.getInventory().getItem(equipmentSlot).getType() != Material.AIR;
   }
}
