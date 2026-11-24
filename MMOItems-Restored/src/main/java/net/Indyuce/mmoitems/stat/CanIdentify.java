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
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, Type type
   ) {
      if (type != null) {
         return false;
      }

      if (consumable.getNBTItem().getBoolean("MMOITEMS_CAN_IDENTIFY") && nbtItem.hasTag("MMOITEMS_UNIDENTIFIED_ITEM")) {
         Player player = playerData.getPlayer();
         if (nbtItem.getItem().getAmount() > 1) {
            Message.CANNOT_IDENTIFY_STACKED_ITEMS.format(ChatColor.RED).send(player);
            return false;
         }

         IdentifyItemEvent event2 = new IdentifyItemEvent(playerData, consumable.getMMOItem(), nbtItem);
         Bukkit.getPluginManager().callEvent(event2);
         if (event2.isCancelled()) {
            return false;
         }

         event.setCurrentItem(new IdentifiedItem(nbtItem).identify());
         Message.SUCCESSFULLY_IDENTIFIED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(nbtItem.getItem())).send(player);
         player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
         return true;
      } else {
         return false;
      }
   }
}
