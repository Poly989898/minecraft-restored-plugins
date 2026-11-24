package net.Indyuce.mmoitems.util;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.CustomSound;
import net.Indyuce.mmoitems.api.event.item.RepairItemEvent;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.listener.CustomSoundListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

public class RepairUtils {
   public RepairUtils() {
      throw new IllegalStateException("Utility class");
   }

   public static boolean repairVanillaItem(@NotNull PlayerData playerData, @NotNull NBTItem nbtItem, @NotNull Consumable consumable, int count) {
      Player player = playerData.getPlayer();
      if (!nbtItem.getBoolean("Unbreakable")
         && nbtItem.getItem().hasItemMeta()
         && nbtItem.getItem().getItemMeta() instanceof Damageable
         && ((Damageable)nbtItem.getItem().getItemMeta()).getDamage() > 0) {
         RepairItemEvent event = new RepairItemEvent(playerData, consumable.getMMOItem(), nbtItem, count);
         Bukkit.getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         }

         count = event.getRepaired();
         Damageable itemMeta = (Damageable)nbtItem.getItem().getItemMeta();
         itemMeta.setDamage(Math.max(0, itemMeta.getDamage() - count));
         nbtItem.getItem().setItemMeta(itemMeta);
         Message.REPAIRED_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(nbtItem.getItem()), "#amount#", String.valueOf(count)).send(player);
         CustomSoundListener.playSound(consumable.getItem(), CustomSound.ON_CONSUME, player);
         return true;
      } else {
         return false;
      }
   }
}
