package net.Indyuce.mmoitems.listener.option;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class DroppedItems implements Listener {
   private final boolean tierGlow;
   private final boolean hints;

   public DroppedItems(ConfigurationSection var1) {
      this.tierGlow = var1.getBoolean("tier-glow");
      this.hints = var1.getBoolean("hints");
   }

   @EventHandler
   public void applyOnSpawn(ItemSpawnEvent var1) {
      ItemStack var2 = var1.getEntity().getItemStack();
      ItemTier var3 = MMOItems.plugin.getTiers().get(NBTItem.get(var2).getString("MMOITEMS_TIER"));
      if (var3 != null) {
         if (this.hints && var3.isHintEnabled()) {
            var1.getEntity().setCustomNameVisible(true);
            var1.getEntity().setCustomName(var2.getItemMeta().getDisplayName());
         }

         if (this.tierGlow && var3.hasColor()) {
            MythicLib.plugin.getGlowing().setGlowing(var1.getEntity(), var3.getColor());
         }
      }
   }
}
