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

   public DroppedItems(ConfigurationSection section) {
      this.tierGlow = section.getBoolean("tier-glow");
      this.hints = section.getBoolean("hints");
   }

   @EventHandler
   public void applyOnSpawn(ItemSpawnEvent event) {
      ItemStack itemStack = event.getEntity().getItemStack();
      ItemTier itemTier = MMOItems.plugin.getTiers().get(NBTItem.get(itemStack).getString("MMOITEMS_TIER"));
      if (itemTier != null) {
         if (this.hints && itemTier.isHintEnabled()) {
            event.getEntity().setCustomNameVisible(true);
            event.getEntity().setCustomName(itemStack.getItemMeta().getDisplayName());
         }

         if (this.tierGlow && itemTier.hasColor()) {
            MythicLib.plugin.getGlowing().setGlowing(event.getEntity(), itemTier.getColor());
         }
      }
   }
}
