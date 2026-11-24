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

   public static boolean repairVanillaItem(@NotNull PlayerData var0, @NotNull NBTItem var1, @NotNull Consumable var2, int var3) {
      Player var4 = var0.getPlayer();
      if (!var1.getBoolean("Unbreakable")
         && var1.getItem().hasItemMeta()
         && var1.getItem().getItemMeta() instanceof Damageable
         && ((Damageable)var1.getItem().getItemMeta()).getDamage() > 0) {
         RepairItemEvent var5 = new RepairItemEvent(var0, var2.getMMOItem(), var1, var3);
         Bukkit.getPluginManager().callEvent(var5);
         if (var5.isCancelled()) {
            return false;
         }

         var3 = var5.getRepaired();
         Damageable var6 = (Damageable)var1.getItem().getItemMeta();
         var6.setDamage(Math.max(0, var6.getDamage() - var3));
         var1.getItem().setItemMeta(var6);
         Message.REPAIRED_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var1.getItem()), "#amount#", String.valueOf(var3)).send(var4);
         CustomSoundListener.playSound(var2.getItem(), CustomSound.ON_CONSUME, var4);
         return true;
      } else {
         return false;
      }
   }
}
