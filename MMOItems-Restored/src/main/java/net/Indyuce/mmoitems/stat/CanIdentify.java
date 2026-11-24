package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.IdentifyItemEvent;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.item.util.identify.IdentifiedItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class CanIdentify extends BooleanStat implements ConsumableItemInteraction {
   public CanIdentify() {
      super(
         "CAN_IDENTIFY",
         Material.PAPER,
         "Can Identify?",
         new String[]{"Players can identify & make their", "item usable using this consumable."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, Type var5
   ) {
      if (var5 != null) {
         return false;
      }

      if (var3.getNBTItem().getBoolean("MMOITEMS_CAN_IDENTIFY") && var4.hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
         Player var6 = var2.getPlayer();
         if (var4.getItem().getAmount() > 1) {
            Message.CANNOT_IDENTIFY_STACKED_ITEMS.format(ChatColor.RED).send(var6);
            return false;
         }

         IdentifyItemEvent var7 = new IdentifyItemEvent(var2, var3.getMMOItem(), var4);
         Bukkit.getPluginManager().callEvent(var7);
         if (var7.isCancelled()) {
            return false;
         }

         var1.setCurrentItem(new IdentifiedItem(var4).identify());
         Message.SUCCESSFULLY_IDENTIFIED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var4.getItem())).send(var6);
         var6.playSound(var6.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
         return true;
      } else {
         return false;
      }
   }
}
